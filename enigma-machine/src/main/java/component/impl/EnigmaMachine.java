package main.java.component.impl;

import main.java.component.*;
import main.java.dto.MachineState;
import main.java.generictype.MappingPair;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class EnigmaMachine implements EncryptionMachine {
    private static final Logger log = Logger.getLogger(EnigmaMachine.class);
    private PlugBoard plugBoard;
    private List<Rotor> rotors;
    private IOWheel ioWheel;
    private Reflector reflector;

    static {
        String log4JPropertyFile = "./enigma-machine/src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + EnigmaMachine.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    @Override
    public String encrypt(String input) {
        String result = new String();
        List<Character> rawResult = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            char encryptedInput = encryptSingle(input.charAt(i));
            result = result.concat(String.valueOf(encryptedInput));
        }
        return result;
    }

    private Character encryptSingle(Character input){
        log.debug("char: " + input);
        String rawInput = String.valueOf(input);
        rawInput = plugBoard.getMappedValue(rawInput);
        log.debug("after plug: " + rawInput);
        int rawInputIndex = ioWheel.handleInput(rawInput);
        log.debug("after io: " + rawInputIndex);
        boolean rotateCurrent = true;
        for (Rotor rotor : rotors ) {
            if(rotateCurrent){
                rotor.rotate();
            }
            rotateCurrent = rotor.doesNotchAllowRotation();
            rawInputIndex = rotor.fromInputWheelToReflector(rawInputIndex);
            log.debug("after rotor: " + rawInputIndex);
        }
        rawInputIndex = reflector.getReflectedValue(rawInputIndex);
        log.debug("after reflector: " + rawInputIndex);

        for (int i = rotors.size() -1; i >=0; i--) {
            rawInputIndex = rotors.get(i).fromReflectorToInputWheel(rawInputIndex);
            log.debug("after rotor (back): " + rawInputIndex);
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
    public void setRotorsStartingPosition(List<Integer> rotorsPosition) {
        if(rotorsPosition.size() != rotors.size()){
            log.warn("Failed to set Rotors positions, different sizes : number of positions:" + rotorsPosition.size() + "number of rotors:" +  rotors.size());
        }
        int posIndex = 0;
        for (Rotor rotor : this.rotors) {
            rotor.setRotorStartingPosition(rotorsPosition.get(posIndex));
            posIndex++;
        }
    }

    @Override
    public void setRotorsStartingPositionByString(List<String> rotorsPosition) {
        if(rotorsPosition.size() != rotors.size()){
            log.warn("Failed to set Rotors positions, different sizes : number of positions:" + rotorsPosition.size() + "number of rotors:" +  rotors.size());
        }
        int posIndex = 0;
        for (Rotor rotor : this.rotors) {
            rotor.setRotorStartingPosition(ioWheel.getABC().indexOf(rotorsPosition.get(posIndex)));
            posIndex++;
        }
    }

    @Override
    public MachineState getMachineState() {
        List<Integer> rotorIds = new ArrayList<>();
        List<Integer> rotorsHeadPositions = new ArrayList<>();
        List<MappingPair<String,String>> minimalPlugMapping = this.plugBoard.getCurrentMapping();
        for (Rotor rotor : this.rotors) {
            rotorIds.add(rotor.getId());
            rotorsHeadPositions.add(rotor.getHeadLocation());
        }
        return new MachineState(reflector.getId(),rotorIds,rotorsHeadPositions,minimalPlugMapping);
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
        if(machineState.getPlugMapping() == null){
            log.warn("setting machine state - No plug board mapping to set");
        }
        else{
            this.plugBoard.connectMultiple(machineState.getPlugMapping());
        }

        if(machineState.getRotorsStartingPositions() == null || this.rotors.size()!=machineState.getRotorsStartingPositions().size()){
            log.warn("setting machine state - No rotors initial rotor positions");
        }
        else{
            this.setRotorsStartingPosition(machineState.getRotorsStartingPositions());
        }
        //TODO: validate rotors ids?
        //TODO: validate reflector's id?
    }

    @Override
    public String getABC() {
        return this.ioWheel.getABC();
    }

}
