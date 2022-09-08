package main.java.component.impl;

import main.java.component.*;
import main.java.dto.MachineState;
import main.java.generictype.DeepCloneable;
import main.java.generictype.MappingPair;
import main.java.handler.FileConfigurationHandler;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.*;

public class EnigmaMachine implements EncryptionMachine {
    private static final Logger log = Logger.getLogger(EnigmaMachine.class);
    private PlugBoard plugBoard;
    private List<Rotor> rotors;
    private IOWheel ioWheel;
    private Reflector reflector;

    static {
        try {
            Properties p = new Properties();
            p.load(FileConfigurationHandler.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + EnigmaMachine.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + EnigmaMachine.class.getSimpleName() ) ;
        }
    }

    @Override
    public String encrypt(String input) {
        String result = "";
        for (int i = 0; i < input.length(); i++) {
            char encryptedInput = encryptSingle(input.charAt(i));
            result = result.concat(String.valueOf(encryptedInput));
        }
        return result;
    }

    private Character encryptSingle(Character input){
        String ABC = ioWheel.getABC();
        log.debug("char: " + input);
        String rawInput = String.valueOf(input);
        rawInput = plugBoard.getMappedValue(rawInput);
        log.debug("after plug: " + rawInput);
        int rawInputIndex = ioWheel.handleInput(rawInput);
        log.debug("after io: " + rawInputIndex + ":" + ABC.charAt(rawInputIndex));
        boolean rotateCurrent = true;
        for (Rotor rotor : rotors ) {
            if(rotateCurrent){
                rotor.rotate();
            }
            rotateCurrent = rotor.doesNotchAllowRotation();
            rawInputIndex = rotor.fromInputWheelToReflector(rawInputIndex);
            log.debug("after rotor: " + rawInputIndex + ":" + ABC.charAt(rawInputIndex));
        }
        rawInputIndex = reflector.getReflectedValue(rawInputIndex);
        log.debug("after reflector: " + rawInputIndex + ":" + ABC.charAt(rawInputIndex));

        for (int i = rotors.size() -1; i >=0; i--) {
            rawInputIndex = rotors.get(i).fromReflectorToInputWheel(rawInputIndex);
            log.debug("after rotor (back): " + rawInputIndex + ":" + ABC.charAt(rawInputIndex));
        }
        rawInput = ioWheel.handleInput(rawInputIndex);
        log.debug("after io: " + rawInput);
        rawInput = plugBoard.getMappedValue(rawInput);
        log.debug("after plug: " + rawInput);
        return rawInput.charAt(0);
    }

    @Override
    public String decrypt(String output) {
        return encrypt(output);
    }

    @Override
    public void buildMachine(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors, IOWheel ioWheel) throws IllegalArgumentException {
           this.plugBoard =  plugBoard;
           this.rotors = rotors;
           this.reflector = reflector;
           this.ioWheel = ioWheel;
    }

    @Override
    public void buildMachine(Reflector reflector, List<Rotor> rotors , IOWheel ioWheel, String ABC) throws IllegalArgumentException {
        this.rotors = rotors;
        this.reflector = reflector;
        this.ioWheel = ioWheel;
        this.plugBoard = new PlugBoardImpl(ABC);
    }

    @Override
    public void connectPlugs(List<String> leftList, List<String> rightList) {
        plugBoard.clearAllPlugs();
        plugBoard.connectMultiple(leftList,rightList);
    }

    @Override
    public void connectPlugs(List<MappingPair<String, String>> plugMappingList) {
        plugBoard.clearAllPlugs();
        plugBoard.connectMultiple(plugMappingList);
    }

    @Override
    public void setRotorsStartingPosition(List<Integer> valuesToSetTheHead) {
        if(valuesToSetTheHead.size() != rotors.size()){
            log.warn("Failed to set Rotors positions, different sizes : number of positions:" + valuesToSetTheHead.size() + "number of rotors:" +  rotors.size());
        }
        int posIndex = 0;
        for (Rotor rotor : this.rotors) {
            rotor.setRotorStartingPosition(valuesToSetTheHead.get(posIndex));
            posIndex++;
        }
    }

    @Override
    public Optional<MachineState> getMachineState() {
        if(ioWheel == null || rotors == null || rotors.size() <= 0 || plugBoard == null){
            log.error("Failed To get machine state, machine not assembled yet");
            return Optional.empty();
        }
        List<Integer> rotorIds = new ArrayList<>();
        List<String> rotorsHeadsInitialValues = new ArrayList<>();
        List<MappingPair<String,String>> minimalPlugMapping = this.plugBoard.getCurrentMapping();
        List<Integer> notchDistances = new ArrayList<>();
        String numberAsLetter;
        int valueInHead;
        for (Rotor rotor : this.rotors) {
            rotorIds.add(rotor.getId());
            notchDistances.add(rotor.howCloseNotchToHead());
            valueInHead = rotor.getValueInHead();
            numberAsLetter = ioWheel.getABC().substring(valueInHead,valueInHead+1);
            rotorsHeadsInitialValues.add(numberAsLetter);
        }
        return  Optional.of(new MachineState(reflector.getId(),rotorIds,rotorsHeadsInitialValues,minimalPlugMapping,notchDistances));
    }

    @Override
    public void setMachineState(MachineState machineState) {
        if(this.rotors == null || this.plugBoard == null || this.reflector == null || this.ioWheel == null){
            log.error("Failed to set machine state - No machine parts present");
            return;
        }
        if(machineState == null){
            log.error("Failed to set machine state from given machineState DTO");
            return;
        }
        //setting - plugboard
        if(machineState.getPlugMapping() == null){
            log.warn("setting machine state - No plug board mapping to set");
        }
        else{
            this.plugBoard.clearAllPlugs();
            this.plugBoard.connectMultiple(machineState.getPlugMapping());
        }
        //Setting - rotors starting positions
        if(machineState.getRotorsHeadsInitialValues() == null || this.rotors.size()!=machineState.getRotorsHeadsInitialValues().size()){
            log.warn("setting machine state - No rotors initial rotor positions or different size");
        }
        else{
            List<Integer> rotorsHeadsInitialValues = new ArrayList<>(machineState.getRotorsHeadsInitialValues().size());
            int letterAsNumber;
            for (String startingValue : machineState.getRotorsHeadsInitialValues()) {
                letterAsNumber = ioWheel.getABC().indexOf(startingValue);
                rotorsHeadsInitialValues.add(letterAsNumber);
            }
            this.setRotorsStartingPosition(rotorsHeadsInitialValues);
        }
    }

    @Override
    public String getABC() {
        if(ioWheel == null) return "";
        return this.ioWheel.getABC();
    }

    @Override
    public EnigmaMachine getDeepClone() {
        EnigmaMachine clonedEnigmaMachine = new EnigmaMachine();
        PlugBoard clonedPlugBoard = this.plugBoard.getDeepClone();
        List<Rotor> clonedRotors = new ArrayList<>();
        for (Rotor rotor: this.rotors ) {
            clonedRotors.add(rotor.getDeepClone());
        }
        IOWheel clonedIoWheel = this.ioWheel.getDeepClone();
        Reflector clonedReflector = this.reflector.getDeepClone();
        clonedEnigmaMachine.buildMachine(clonedPlugBoard,clonedReflector,clonedRotors,clonedIoWheel);
        return clonedEnigmaMachine;
    }
}
