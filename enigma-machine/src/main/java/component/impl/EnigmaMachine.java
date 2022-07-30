package main.java.component.impl;

import main.java.component.*;

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
        return null;
    }

    @Override
    public boolean setupMachine(Object... args) {
        return false;
    }

    @Override
    public List<String> getMachineState() {
        return null;
    }
}
