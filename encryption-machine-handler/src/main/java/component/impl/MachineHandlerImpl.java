package main.java.component.impl;


import javafx.util.Pair;
import main.java.dto.MachineState;
import main.java.dto.MachineStatisticsHistory;
import main.java.generictype.MappingPair;
import main.java.handler.FileConfigurationHandler;
import main.java.component.*;
import main.java.enums.ReflectorsId;
import main.resources.generated.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MachineHandlerImpl implements MachineHandler {
    private static final Logger log = Logger.getLogger(MachineHandlerImpl.class);
    private PlugBoard plugBoardInventory;
    private List<Rotor> rotorsInventory;
    private IOWheel ioWheelInventory;
    private List<Reflector> reflectorsInventory;
    private int expectedNumOfRotors;
    private EncryptionMachine encryptionMachine = new EnigmaMachine();
    private MachineState initialMachineState = new MachineState();

    static {
        String log4JPropertyFile = "./enigma-machine/src/main/resources/log4j.properties";
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
        if (isFileInExistenceAndXML(absolutePath)){
            CTEEnigma cteEnigma = FileConfigurationHandler.fromXmlFileToCTE(absolutePath);
            if(isMachineConfigurationValid(cteEnigma)){
                buildMachinePartsInventory(cteEnigma);
            }
            else{
                log.error("Failed to build machine inventory - CteMachine configured in file is invalid");
                throw new Exception("Failed to build machine inventory - CteMachine configured in file is invalid");
            }
        }
    }

    @Override
    public void assembleMachine() {
        String ABC = ioWheelInventory.getABC();
        Random random = new Random();
        ReflectorsId reflectorId = ReflectorsId.getByNum(random.nextInt(ReflectorsId.values().length) + 1);
        List<Integer> rotorIdList = generateRandomRotorList();
        List<Integer> rotorStartingPositions = generateRandomRotorPositions(ABC);
        List<MappingPair<String,String>> plugPairsMapping = generateRandomPlugboardConnections(ABC);
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

    private List<Integer> generateRandomRotorPositions(String ABC) {
        Random random = new Random();
        List<Integer> rotorsStartingPositions = new ArrayList<>();
        int startingPos;
        for (int i = 0; i < expectedNumOfRotors; i++) {
            startingPos = random.nextInt(ABC.length()) + 1;
            rotorsStartingPositions.add(startingPos);
        }
        return rotorsStartingPositions;
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
    public void assembleMachine(ReflectorsId reflectorId, List<Integer> rotorIds, List<Integer> rotorsStartingPositions, List<MappingPair<String, String>> plugMapping) {
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
    public void setStartingMachineState(List<Integer> rotorsStartingPositions, List<MappingPair<String, String>> plugMapping) {
        if(encryptionMachine == null){
            log.error("Failed to set Machine initial state - no machine found");
            return;
        }
        encryptionMachine.setRotorsStartingPosition(rotorsStartingPositions);
        encryptionMachine.connectPlugs(plugMapping);
        this.initialMachineState.setRotorsStartingPositions(rotorsStartingPositions);
        this.initialMachineState.setPlugMapping(plugMapping);
        log.info("Machine Handler - initial state of machine state set");
    }

    @Override
    public MachineState getMachineState() {
        if(this.encryptionMachine == null){
            return null;
        }
        return encryptionMachine.getMachineState();
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
    public boolean resetToLastSetState() {
        this.encryptionMachine.setMachineState(initialMachineState);
        return true;
    }

    @Override
    public String encrypt(String input) {
        //todo is to lower case necessary
        input = input.toLowerCase();

        String abc = ioWheelInventory.getABC();
        for (int i = 0; i < input.length(); i++) {
            if(!abc.contains(input.substring(i,i+1))){
                throw new IllegalArgumentException("input contains letter not in ACB : letter index" + i);
            }
        }

        Instant startEncryptionTime = Instant.now();
        String encrypted = encryptionMachine.encrypt(input);
        Instant endEncryptionTime = Instant.now();
        Long encryptionTime = Duration.between(startEncryptionTime, endEncryptionTime).toNanos();
        log.info("time it took to encrypt:" + encryptionTime);
        return encrypted;
    }

    @Override
    public MachineStatisticsHistory getMachineStatisticsHistory() {
        return null;
    }

    private boolean isMachineConfigurationValid(CTEEnigma cteEnigma) {
        CTEMachine machine = cteEnigma.getCTEMachine();
        String ABC = machine.getABC();
        List<CTERotor> rotors = machine.getCTERotors().getCTERotor();
        List<CTEReflector> reflectors = machine.getCTEReflectors().getCTEReflector();

        log.debug("MachineHandler check xml validity: Is even ABC: " + isABCCountEven(ABC));
        log.debug("MachineHandler check xml validity: Is good rotors count: " + isRotorCountGood(machine.getRotorsCount(), rotors.size()));
        log.debug("MachineHandler check xml validity: Is rotors ids legal: " + isRotorsIdsLegal(rotors));
        log.debug("MachineHandler check xml validity: Is rotors map legal: " + isRotorsMappingLegal(rotors));
        log.debug("MachineHandler check xml validity: Is rotors notches legal: " + isRotorsNotchLegal(rotors, ABC));
        log.debug("MachineHandler check xml validity: Is reflector ids legal: " + isReflectorsIdsLegal(reflectors));
        log.debug("MachineHandler check xml validity: Is reflectors mapping legal: " + isReflectorsMappingLegal(reflectors));

        boolean result = isABCCountEven(ABC) && isRotorCountGood(machine.getRotorsCount(), rotors.size())
                && isRotorsIdsLegal(rotors) && isRotorsMappingLegal(rotors) && isRotorsNotchLegal(rotors, ABC)
                && isReflectorsIdsLegal(reflectors) && isReflectorsMappingLegal(reflectors);

        log.info("MachineHandler check xml validity: did pass all tests: " + result);
        return result;
    }
    private boolean isFileInExistenceAndXML(String path) throws IOException {
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }

        File file = Paths.get(path).toRealPath().toFile();
//        File f = new File(path);
        return extension.toLowerCase(Locale.ROOT).equals("xml") && file.exists();

    }

    private boolean isABCCountEven(String ABC){
        String noWhiteSpaces = ABC.trim();
        return noWhiteSpaces.length() % 2 == 0;
    }

    private boolean isRotorCountGood(int declaredRotorsCount, int numOfRotors){
        return (declaredRotorsCount <= numOfRotors) && (declaredRotorsCount >=2);
    }

    private boolean isRotorsDefinitionGood(CTERotors rotors, String ABC) {

        return isRotorsIdsLegal(rotors.getCTERotor())
                && isRotorsMappingLegal(rotors.getCTERotor())
                && isRotorsNotchLegal(rotors.getCTERotor(), ABC);
    }

    private boolean isRotorsIdsLegal(List<CTERotor> rotors){
        List<Integer> rotorsIds = new ArrayList<>();// = new Arrays(rotors.size());
        for (CTERotor rotor : rotors) {
            rotorsIds.add(rotor.getId());
        }

        rotorsIds.sort(Comparator.naturalOrder());

        List<Integer> list = IntStream.range(1, rotors.size() + 1)
                .boxed()
                .collect(Collectors.toList());

        return list.equals(rotorsIds);
    }

    private boolean isRotorsMappingLegal(List<CTERotor> rotors){
        boolean isMappingLegal = true;
        for(CTERotor rotor: rotors){
            isMappingLegal = isMappingLegal && isRotorMappingLegal(rotor);
        }
        return isMappingLegal;
    }

    private boolean isRotorMappingLegal(CTERotor rotor){
        List<CTEPositioning> positioning = rotor.getCTEPositioning();
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();

        for(CTEPositioning position: positioning){
            String l = position.getLeft();
            String r = position.getRight();
            if (left.contains(l) || right.contains(r)) {
                return false;
            }
            left.add(l);
            right.add(r);
        }

        return true;
    }

    private boolean isRotorsNotchLegal(List<CTERotor> rotors, String ABC){
        boolean isLegal = true;
        int ABCLen = ABC.trim().length();

        for (CTERotor rotor: rotors) {
            isLegal = isLegal && rotor.getNotch() <= ABCLen;
        }
        return isLegal;
    }

    private boolean isReflectorsIdsLegal(List<CTEReflector> reflectors){
        List<Integer> reflectorsIds = new ArrayList<>();
        for (CTEReflector reflector : reflectors) {
            int id = ReflectorsId.valueOf(reflector.getId()).getId();
            reflectorsIds.add(id);
        }

        reflectorsIds.sort(Comparator.naturalOrder());

        List<Integer> list = IntStream.range(1, reflectors.size() + 1)
                .boxed()
                .collect(Collectors.toList());

        return list.equals(reflectorsIds);
    }

    private boolean isReflectorsMappingLegal(List<CTEReflector> reflectors){
        boolean isMappingLegal = true;

        for(CTEReflector reflector: reflectors){
            isMappingLegal = isMappingLegal && isReflectorMappingLegal(reflector);
        }

        return isMappingLegal;
    }

    private boolean isReflectorMappingLegal(CTEReflector reflector){
        List<CTEReflect> reflects = reflector.getCTEReflect();

        for(CTEReflect reflect : reflects) {
            int in = reflect.getInput();
            int out = reflect.getOutput();
            if(in == out) {
                return false;
            }
        }

        return true;
    }
}