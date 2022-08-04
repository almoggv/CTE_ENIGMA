package main.java.component.impl;

import main.java.component.*;
import main.resources.generated.CTEMachine;

import java.util.List;

public class EnigmaMachine implements EncryptionMachine {

    private PlugBoard plugBoard;
    private List<Rotor> rotors;
    private IOWheel ioWheel;
    private Reflector reflector;


    @Override
    public String encrypt(String input) {

        return null;
    }

    @Override
    public String decrypt(String output) {
        return encrypt(output);
    }

    @Override
    public void buildMachine(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors) throws IllegalArgumentException {

    }

    @Override
    public void buildMachine(Reflector reflector, List<Rotor> rotors ) throws IllegalArgumentException {

    }

    public void connectPlugs(){

    }

    @Override
    public void setRotorsStartingPosition(List<Integer> rotorsPosition) {

    }


    @Override
    public List<String> getMachineState() {
        return null;
    }
}
