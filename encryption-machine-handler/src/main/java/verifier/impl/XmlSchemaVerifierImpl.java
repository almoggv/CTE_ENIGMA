package main.java.verifier.impl;

import main.java.component.impl.MachineHandlerImpl;
import main.java.enums.ReflectorsId;
import main.java.enums.XmlVerifierState;
import main.java.verifier.XmlSchemaVerifier;
import main.resources.generated.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class XmlSchemaVerifierImpl implements XmlSchemaVerifier {

    private static final Logger log = Logger.getLogger(XmlSchemaVerifierImpl.class);

    static {
        try {
            URL log4JPropertyUrl = XmlSchemaVerifierImpl.class.getResource("/main/resources/log4j.properties");
            String log4JPropertyFile = log4JPropertyUrl.getFile();
            Properties p = new Properties();
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + XmlSchemaVerifierImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    public XmlVerifierState isMachineConfigurationValid(CTEEnigma cteEnigma) {
        CTEMachine machine = cteEnigma.getCTEMachine();
        String ABC = machine.getABC();
        List<CTERotor> rotors = machine.getCTERotors().getCTERotor();
        List<CTEReflector> reflectors = machine.getCTEReflectors().getCTEReflector();

        boolean abcCountEvenResult =  isABCCountEven(ABC);
        boolean rotorCountLegalResult = isRotorCountGood(machine.getRotorsCount(), rotors.size());
        boolean rotorIdLegalResult = isRotorsIdsLegal(rotors);
        boolean rotorMappingLegalResult = isRotorsMappingLegal(rotors);
        boolean rotorNotchLegalResult = isRotorsNotchLegal(rotors, ABC);
        boolean reflectorIdLegalResult = isReflectorsIdsLegal(reflectors);
        boolean reflectorMappingLegalResult = isReflectorsMappingLegal(reflectors);

        log.debug("MachineHandler check xml validity: Is even ABC: " +abcCountEvenResult);
        log.debug("MachineHandler check xml validity: Is good rotors count: " + rotorCountLegalResult);
        log.debug("MachineHandler check xml validity: Is rotors ids legal: " + rotorIdLegalResult);
        log.debug("MachineHandler check xml validity: Is rotors map legal: " + rotorMappingLegalResult);
        log.debug("MachineHandler check xml validity: Is rotors notches legal: " + rotorNotchLegalResult);
        log.debug("MachineHandler check xml validity: Is reflector ids legal: " + reflectorIdLegalResult);
        log.debug("MachineHandler check xml validity: Is reflectors mapping legal: " + reflectorMappingLegalResult);

        if(abcCountEvenResult == false){
            return XmlVerifierState.ERROR_ABC_COUNT_ODD;
        }
        if(rotorCountLegalResult == false){
            return XmlVerifierState.ERROR_BAD_ROTOR_COUNT;
        }
        if(rotorIdLegalResult == false){
            return XmlVerifierState.ERROR_ILLEGAL_ROTOR_ID;
        }
        if(rotorMappingLegalResult == false){
            return XmlVerifierState.ERROR_ILLEGAL_ROTOR_MAPPING;
        }
        if(rotorNotchLegalResult == false){
            return XmlVerifierState.ERROR_ILLEGAL_NOTCH_LOCATION;
        }
        if(reflectorIdLegalResult == false){
            return XmlVerifierState.ERROR_ILLEGAL_REFLECTOR_ID;
        }
        if(reflectorMappingLegalResult == false){
            return XmlVerifierState.ERROR_ILLEGAL_REFLECTOR_MAPPING;
        }
        log.info("Machine Configuration schema passed all tests.");
        return XmlVerifierState.VALID;
    }

    public boolean isFileInExistenceAndXML(String path) throws IOException {
        String extension = "";
        int i = path.lastIndexOf(".");
        if (i > 0) {
            extension = path.substring(i+1);
        }

        File file = Paths.get(path).toRealPath().toFile();
//        File f = new File(path);
        return extension.toLowerCase(Locale.ROOT).equals("xml") && file.exists();

    }

    public boolean isABCCountEven(String ABC){
        String noWhiteSpaces = ABC.trim();
        return noWhiteSpaces.length() % 2 == 0;
    }

    public boolean isRotorCountGood(int requiredRotorsCount, int numOfRotors){
        return (requiredRotorsCount <= numOfRotors) && (requiredRotorsCount >= 2) && (requiredRotorsCount <= 99);
    }

    public boolean isRotorsDefinitionGood(CTERotors rotors, String ABC) {

        return isRotorsIdsLegal(rotors.getCTERotor())
                && isRotorsMappingLegal(rotors.getCTERotor())
                && isRotorsNotchLegal(rotors.getCTERotor(), ABC);
    }

    public boolean isRotorsIdsLegal(List<CTERotor> rotors){
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

    public boolean isRotorsMappingLegal(List<CTERotor> rotors){
        boolean isMappingLegal = true;
        for(CTERotor rotor: rotors){
            isMappingLegal = isMappingLegal && isRotorMappingLegal(rotor);
        }
        return isMappingLegal;
    }

    public boolean isRotorMappingLegal(CTERotor rotor){
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

    public boolean isRotorsNotchLegal(List<CTERotor> rotors, String ABC){
        boolean isLegal = true;
        int ABCLen = ABC.trim().length();

        for (CTERotor rotor: rotors) {
            isLegal = isLegal && rotor.getNotch() <= ABCLen;
        }
        return isLegal;
    }

    public boolean isReflectorsIdsLegal(List<CTEReflector> reflectors){
        List<Integer> reflectorsIds = new ArrayList<>();
        for (CTEReflector reflector : reflectors) {
            int id = ReflectorsId.valueOf(reflector.getId()).getId();
            reflectorsIds.add(id);
        }

        reflectorsIds.sort(Comparator.naturalOrder());

        List<Integer> list = IntStream.range(1, reflectors.size() + 1)
                .boxed()
                .collect(Collectors.toList());

        return list.equals(reflectorsIds) && (list.size() <= 5 && list.size() > 0);
    }

    public boolean isReflectorsMappingLegal(List<CTEReflector> reflectors){
        boolean isMappingLegal = true;

        for(CTEReflector reflector: reflectors){
            isMappingLegal = isMappingLegal && isReflectorMappingLegal(reflector);
        }

        return isMappingLegal;
    }

    public boolean isReflectorMappingLegal(CTEReflector reflector){
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
