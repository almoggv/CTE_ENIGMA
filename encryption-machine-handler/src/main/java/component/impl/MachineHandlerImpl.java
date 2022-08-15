package main.java.component.impl;


import lombok.Getter;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import main.java.dto.EncryptionInfoHistory;
import main.java.enums.XmlVerifierState;
import main.java.generictype.MappingPair;
import main.java.handler.FileConfigurationHandler;
import main.java.component.*;
import main.java.enums.ReflectorsId;
import main.java.verifier.XmlSchemaVerifier;
import main.java.verifier.impl.XmlSchemaVerifierImpl;
import main.resources.generated.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

public class MachineHandlerImpl implements MachineHandler {
    private static final Logger log = Logger.getLogger(MachineHandlerImpl.class);
    private PlugBoard plugBoardInventory;
    private List<Rotor> rotorsInventory;
    private IOWheel ioWheelInventory;
    private List<Reflector> reflectorsInventory;
    private int expectedNumOfRotors;
    private EncryptionMachine encryptionMachine = new EnigmaMachine();
    private MachineState initialMachineState = new MachineState();
    @Getter private final Map<MachineState, List<EncryptionInfoHistory>> machineStatisticsHistory = new HashMap<>();
    private final XmlSchemaVerifier xmlSchemaVerifier = new XmlSchemaVerifierImpl();

    static {
        String log4JPropertyFile = "./src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + MachineHandlerImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    @Override
    public void buildMachinePartsInventory(String absolutePath) throws Exception {
        //todo - reset history - check if anything else
        String usingLastloadedInvntoryMsg = "\n--Last successful load is used.--";
        try{
            xmlSchemaVerifier.isFileInExistenceAndXML(absolutePath);
        }
        catch(IOException e){
            String msg = "The File: \"" + absolutePath + "\" - Does not exist Or is not a valid .xml file.";
            msg = getInventoryInfo().isPresent() ? msg + usingLastloadedInvntoryMsg : msg;
            throw new Exception(msg);
        }
        CTEEnigma cteEnigma = FileConfigurationHandler.fromXmlFileToCTE(absolutePath);
        XmlVerifierState xmlVerifierResponse = xmlSchemaVerifier.isMachineConfigurationValid(cteEnigma);
        if(xmlVerifierResponse == XmlVerifierState.VALID){
            clearInventory();
            buildMachinePartsInventory(cteEnigma);
            machineStatisticsHistory.clear();
        }
        else{
            String msg = "Failed to build machine inventory - CteMachine configured in file is invalid: " + xmlVerifierResponse;
            log.error(msg);
            msg = getInventoryInfo().isPresent() ? msg + usingLastloadedInvntoryMsg : msg;
            throw new Exception(msg);
        }
    }

    private void clearInventory() {
        this.ioWheelInventory = null;
        this.reflectorsInventory = null;
        this.plugBoardInventory = null;
        this.rotorsInventory = null;
    }

    @Override
    public void assembleMachine() {
        String ABC = ioWheelInventory.getABC();
        Random random = new Random();
        ReflectorsId reflectorId = ReflectorsId.getByNum(random.nextInt(reflectorsInventory.size()) + 1);
        List<Integer> rotorIdList = generateRandomRotorList();
        String rotorStartingPositions = generateRandomRotorPositions(ABC);
        List<MappingPair<String,String>> plugPairsMapping = generateRandomPlugboardConnections(ABC);
        log.debug("assemble machine randomly");
        log.debug("reflector id: "+reflectorId);
        log.debug("rotor id list: "+ rotorIdList);
        log.debug("rotor starting positions: "+ rotorStartingPositions);
        log.debug("plugboard mapping: " + plugPairsMapping);

        assembleMachine(reflectorId,rotorIdList,rotorStartingPositions,plugPairsMapping);
    }

    private List<MappingPair<String,String>> generateRandomPlugboardConnections(String ABC) {
        Random random = new Random();
        List<String> plugsLeft = new ArrayList<>();
        List<String> plugsRight = new ArrayList<>();
        int numOfPlugs = random.nextInt(ABC.length()/2);
        int randStartingPoint1 = random.nextInt(ABC.length());
        int randStartingPoint2 = random.nextInt(ABC.length());
        String endPoint1 = ABC.substring(randStartingPoint1,randStartingPoint1 + 1);
        String endPoint2 = ABC.substring(randStartingPoint2,randStartingPoint2 + 1);
        for (int i = 0; i < numOfPlugs; i++) {
            while (plugsLeft.contains(endPoint1)) {
                randStartingPoint1 = random.nextInt(ABC.length());
                endPoint1 = ABC.substring(randStartingPoint1,randStartingPoint1 + 1);
            }
            while (plugsRight.contains(endPoint2)) {
                randStartingPoint2 = random.nextInt(ABC.length());
                endPoint2 = ABC.substring(randStartingPoint2, randStartingPoint2 + 1);
            }
            plugsLeft.add(endPoint1);
            plugsRight.add(endPoint2);
        }
        List<MappingPair<String,String>> plugPairs = new ArrayList<>();
        for (int i = 0; i < plugsLeft.size() && i < plugsRight.size(); i++) {
             plugPairs.add(new MappingPair<String,String>(plugsLeft.get(i), plugsRight.get(i)));
        }
        return plugPairs;
    }

    private String generateRandomRotorPositions(String ABC) {
        Random random = new Random();
        int startingPos;
        String currentGeneratedLetter;
        String result = "";
        for (int i = 0; i < expectedNumOfRotors; i++) {
            startingPos = random.nextInt(ABC.length()) /*+ 1*/;
            System.out.println("starting pos:" + startingPos);
            currentGeneratedLetter = ABC.substring(startingPos,startingPos+1);
            result = result.concat(currentGeneratedLetter);
        }
        return result;
    }

    private List<Integer> generateRandomRotorList() {
        Random random = new Random();
        List<Integer> rotorIdList = new ArrayList<>();
        int rotorId = random.nextInt(rotorsInventory.size()) + 1;
        for (int i = 0; i < expectedNumOfRotors; i++) {
            while (rotorIdList.contains(rotorId)) {
                rotorId = random.nextInt(rotorsInventory.size()) + 1;
            }
            rotorIdList.add(rotorId);
        }
        return rotorIdList;
    }

    @Override
    public void assembleMachine(ReflectorsId reflectorId, List<Integer> rotorIds, String rotorsStartingPositions, List<MappingPair<String, String>> plugMapping) {
        assembleMachineParts(reflectorId, rotorIds);
        setStartingMachineState(rotorsStartingPositions, plugMapping);
    }

    @Override
    public void assembleMachineParts(ReflectorsId reflectorId, List<Integer> rotorIds) {
        Predicate<Reflector> idReflectorPredicate = (reflector) -> reflector.getId() == reflectorId;
        Reflector reflector = reflectorsInventory.stream().filter(idReflectorPredicate).findFirst().orElse(null);
        if(reflector == null){
            log.error("Failed to assemble machine parts - could not find REFLECTOR in inventory with id: " + reflectorId);
            return;
        }
        List<Rotor> rotorListForMachine = new ArrayList<>();
        for (int rotorId : rotorIds) {
            Predicate<Rotor> idRotorPredicate = (rotor) -> rotor.getId() == rotorId;
            Rotor rotorFromInventory = rotorsInventory.stream().filter(idRotorPredicate).findFirst().orElse(null);
            rotorListForMachine.add(rotorFromInventory);
        }
        if(rotorListForMachine.size() != rotorIds.size()){
            log.error("Failed to assemble machine parts - not all ROTORS were found, missing" + (rotorIds.size()-rotorListForMachine.size()));
            return;
        }
        if(encryptionMachine == null){
            log.error("Failed to assemble machine parts - no machine found");
            return;
        }
        this.encryptionMachine.buildMachine(plugBoardInventory, reflector, rotorListForMachine, ioWheelInventory);
        this.initialMachineState.setReflectorId(reflector.getId());
        this.initialMachineState.setRotorIds(rotorIds);
        log.info("Machine Handler - assembled machine parts finished");
    }

    @Override
    public void setStartingMachineState(String rotorsStartingPositions, List<MappingPair<String, String>> plugMapping) {
        if(encryptionMachine == null){
            log.error("Failed to set Machine initial state - no machine found");
            return;
        }
        String currentLetter;
        int letterAsNumber;
        String abc = ioWheelInventory.getABC();
        List<Integer> valuesOfHeadToSetRotors = new ArrayList<>(rotorsStartingPositions.length());
        for (int i = 0; i < rotorsStartingPositions.length(); i++) {
            currentLetter = rotorsStartingPositions.substring(i,i+1);
            letterAsNumber = abc.indexOf(currentLetter);
            valuesOfHeadToSetRotors.add(letterAsNumber);
        }
        encryptionMachine.setRotorsStartingPosition(valuesOfHeadToSetRotors);
        encryptionMachine.connectPlugs(plugMapping);
        //TODO: FIX here
        this.initialMachineState.setRotorsHeadsInitialValues(encryptionMachine.getMachineState().get().getRotorsHeadsInitialValues());
        this.initialMachineState.setPlugMapping(plugMapping);
        log.info("Machine Handler - initial state of machine state set");
        addToHistory((MachineState) initialMachineState.getDeepClone());
    }

    @Override
    public Optional<MachineState> getMachineState() {
        if(this.encryptionMachine == null){
            return Optional.empty();
        }
        return encryptionMachine.getMachineState();
    }

    @Override
    public Optional<InventoryInfo> getInventoryInfo() {
        if(this.ioWheelInventory == null || this.plugBoardInventory == null || this.rotorsInventory == null || this.reflectorsInventory == null){
            return Optional.empty();
        }
        InventoryInfo inventoryInfo = new InventoryInfo();
        inventoryInfo.setNumOfAvailableReflectors(reflectorsInventory.size());
        inventoryInfo.setNumOfAvailableRotors(rotorsInventory.size());
        inventoryInfo.setNumOfRotorsInUse(expectedNumOfRotors);
        Map<Integer,Integer> rotorIdToNotch = new HashMap<>();
        for (Rotor rotor: rotorsInventory ) {
            rotorIdToNotch.putIfAbsent(rotor.getId(), rotor.howCloseNotchToHead());
        }
        inventoryInfo.setRotorIdToNotchLocation(rotorIdToNotch);
        inventoryInfo.setABC(ioWheelInventory.getABC());
        return Optional.of(inventoryInfo);
    }

    private void buildMachinePartsInventory(CTEEnigma cteEnigma) {
        try {
            CTEMachine cteMachine = cteEnigma.getCTEMachine();
            String ABC = cteMachine.getABC().trim();
            List<CTERotor> cteRotors = cteMachine.getCTERotors().getCTERotor();
            List<CTEReflector> cteReflectors = cteMachine.getCTEReflectors().getCTEReflector();
            this.expectedNumOfRotors = cteMachine.getRotorsCount();

            ioWheelInventory = new IOWheelImpl(ABC);
            plugBoardInventory = new PlugBoardImpl(ABC);
            rotorsInventory = new ArrayList<>();
            reflectorsInventory = new ArrayList<>();
            for (CTERotor cteRotor : cteRotors) {
                Rotor rotor = new RotorImpl(cteRotor, ABC);
                rotorsInventory.add(rotor);
            }
            for (CTEReflector cteReflector : cteReflectors) {
                Reflector reflector = new ReflectorImpl(cteReflector);
                reflectorsInventory.add(reflector);
            }
            if(ioWheelInventory == null || plugBoardInventory == null || rotorsInventory == null
                    || rotorsInventory.size() <= 0 || reflectorsInventory == null || reflectorsInventory.size()<=0){
                log.error("Machine Handler - failed to create an inventory item");
                throw new IllegalArgumentException("failed to create inventory");
            }
            log.info("Machine handler: created inventory successfully");
        }
        catch (Exception e){
            log.error("Machine handler - failed to build inventory:" + e.getMessage());
        }
    }

    @Override
    public boolean loadStateFromFile(String absolutePath) {
        return false;
    }

    @Override
    public boolean saveStateToFile(String fileName) {
        return false;
    }

    @Override
    public void resetToLastSetState() {
        this.encryptionMachine.setMachineState(initialMachineState);
    }

    @Override
    public String encrypt(String input) throws RuntimeException{
        Optional<String> verifiedInput = verifyInputInAbcAndFix(input);
        log.debug("encrypt: input after verification: "+ verifiedInput);
        //For History documentation
        MachineState machineStateBeforeEncrypt;
        if(encryptionMachine.getMachineState().isPresent() && verifiedInput.isPresent()) {
            machineStateBeforeEncrypt = encryptionMachine.getMachineState().get();
        }
        else{
            throw new RuntimeException("Machine is null");
        }

        Instant startEncryptionTime = Instant.now();
        String encryptedOutput = encryptionMachine.encrypt(verifiedInput.get());
        Instant endEncryptionTime = Instant.now();
        Duration encryptionTime = Duration.between(startEncryptionTime, endEncryptionTime);//.toNanos();
        log.info("time it took to encrypt:" + encryptionTime);
        System.out.println(encryptionTime.toNanos());
        //todo - make sure - initial state
        addToHistory(initialMachineState,verifiedInput.get(),encryptedOutput,encryptionTime);

        return encryptedOutput;
    }

    private void addToHistory(MachineState machineStateBeforeEncrypt, String input, String output, Duration duration){
        EncryptionInfoHistory infoHistory = new EncryptionInfoHistory(input,output,duration);
        if(!machineStatisticsHistory.containsKey(machineStateBeforeEncrypt)){
            machineStatisticsHistory.put(machineStateBeforeEncrypt,new ArrayList<EncryptionInfoHistory>());
        }
        machineStatisticsHistory.get(machineStateBeforeEncrypt).add(infoHistory);
    }

    private void addToHistory(MachineState machineStateBeforeEncrypt){
        if(!machineStatisticsHistory.containsKey(machineStateBeforeEncrypt)){
            machineStatisticsHistory.put(machineStateBeforeEncrypt,new ArrayList<EncryptionInfoHistory>());
        }
    }
    public Optional<String> verifyInputInAbcAndFix(String input) {
        if(ioWheelInventory == null){
            log.error("Failed to verify and fix input, no IOWheel present yet in inventory");
            return Optional.empty();
        }
        String ABC = ioWheelInventory.getABC();
        String upperAbcRegex = "[A-Z]+";//[0-9]+";
        String lowerAbcRegex = "[a-z]+";//[0-9]+";
        if(ABC.matches(upperAbcRegex) && !ABC.matches(lowerAbcRegex)){
            input = input.toUpperCase();
        }
        else if(!ABC.matches(upperAbcRegex) && ABC.matches(lowerAbcRegex)){
            input = input.toLowerCase();
        }

        for (int i = 0; i < input.length(); i++) {
            if(!ABC.contains(input.substring(i,i+1))){
                return Optional.empty();
            }
        }
        return Optional.of(input);
    }
}