package main.java.component.impl;

import main.java.component.*;
import main.resources.generated.CTEMachine;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EnigmaMachine implements EncryptionMachine {
    private static final Logger log = Logger.getLogger(EnigmaMachine.class);
    private PlugBoard plugBoard;
    private List<Rotor> rotors;
    private IOWheel ioWheel;
    private Reflector reflector;

    static {
        String log4JPropertyFile = "./src/main/resources/log4j.properties";
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
        String rawInput = String.valueOf(input);
        rawInput = plugBoard.getMappedValue(rawInput);
        int rawInputIndex = ioWheel.handleInput(rawInput);
        boolean rotateNext = true;
        for (Rotor rotor : rotors ) {
            if(rotateNext){
                rotor.rotate();
            }
            rotateNext = rotor.doesNotchAllowRotation();
            rawInputIndex = rotor.fromInputWheelToReflector(rawInputIndex);
        }
        rawInputIndex = reflector.getReflectedValue(rawInputIndex);
        for (Rotor rotor : rotors ) {
            rawInputIndex = rotor.fromReflectorToInputWheel(rawInputIndex);
        }
        rawInput = ioWheel.handleInput(rawInputIndex);
        rawInput = plugBoard.getMappedValue(rawInput);
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
            rotor.setRotorPosition(rotorsPosition.get(posIndex));
            posIndex++;
        }
    }


    @Override
    public List<String> getMachineState() {
        return null;
    }
}
