package main.java.component.impl;


import main.java.dto.MachineState;
import main.java.handler.FileConfigurationHandler;
import main.java.component.*;
import main.java.enums.ReflectorsId;
import main.resources.generated.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private EncryptionMachine encryptionMachine = new EnigmaMachine();
    //TODO: add "machine state" member

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
    public void buildMachinePartsInventory(String absolutePath) {
        if (isFileInExistenceAndXML(absolutePath)){
            CTEEnigma cteEnigma = FileConfigurationHandler.fromXmlFileToCTE(absolutePath);
            checkMachineConfiguration(cteEnigma);
            buildMachinePartsInventory(cteEnigma);
        }
    }

    @Override
    public boolean assembleMachine() {
        return false;
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

        List<Rotor> rotorListForMachine = new ArrayList<>();
        for (int rotorId : rotorIds) {
            Predicate<Rotor> idRotorPredicate = (rotor) -> rotor.getId() == rotorId;
            Rotor rotorFromInventory = rotorsInventory.stream().filter(idRotorPredicate).findFirst().orElse(null);
            rotorListForMachine.add(rotorFromInventory);
        }

        encryptionMachine.buildMachine(plugBoardInventory, reflector, rotorListForMachine, ioWheelInventory);
    }

    @Override
    public void setStartingMachineState(List<Integer> rotorsStartingPositions, List<MappingPair<String, String>> plugMapping) {
        encryptionMachine.setRotorsStartingPosition(rotorsStartingPositions);
        encryptionMachine.connectPlugs(plugMapping);
    }

    @Override
    public MachineState getMachineState() {
        return null;
    }

    private void buildMachinePartsInventory(CTEEnigma cteEnigma) {
        try {
            CTEMachine cteMachine = cteEnigma.getCTEMachine();
            String ABC = cteMachine.getABC().trim();
            List<CTERotor> cteRotors = cteMachine.getCTERotors().getCTERotor();
            List<CTEReflector> cteReflectors = cteMachine.getCTEReflectors().getCTEReflector();

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
            log.debug("Machine handler: created inventory successfully");
        }
        catch (Exception e){
            log.error("Machine handler - failed to build inventory:" + e.getMessage());
        }
    }

    /*
   <45,27,94><AO!><III><A|Z,D|E>
    */


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
        return false;
    }

    @Override
    public String encrypt(String input) {
        return encryptionMachine.encrypt(input);
    }

    @Override
    public String getMachineHistory() {
        return null;
    }

    @Override
    public String getMachineStatistics() {
        return null;
    }

    private void checkMachineConfiguration(CTEEnigma cteEnigma) {
        CTEMachine machine = cteEnigma.getCTEMachine();
        String ABC = machine.getABC();
        List<CTERotor> rotors = machine.getCTERotors().getCTERotor();
        List<CTEReflector> reflectors = machine.getCTEReflectors().getCTEReflector();

        //todo change to logs
        System.out.println("Even ABC: " + isABCCountEven(ABC));

        System.out.println("good rotors count: " + isRotorCountGood(machine.getRotorsCount(), rotors.size()));

        System.out.println("isRotorsIdsLegal: "+isRotorsIdsLegal(rotors));

        System.out.println("isRotorsMappLegal: "+isRotorsMappingLegal(rotors));

        System.out.println("isRotorsNotchLegal: "+isRotorsNotchLegal(rotors, ABC));

        System.out.println("isReflectorIdsLegal: "+isReflectorsIdsLegal(reflectors));

        System.out.println("isReflectorsMappingLegal: "+ isReflectorsMappingLegal(reflectors));
    }
    private boolean isFileInExistenceAndXML(String absolutePath){
        String extension = "";
        int i = absolutePath.lastIndexOf('.');
        if (i > 0) {
            extension = absolutePath.substring(i+1);
        }

        File f = new File(absolutePath);
        return extension.toLowerCase(Locale.ROOT).equals("xml") && f.exists();

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