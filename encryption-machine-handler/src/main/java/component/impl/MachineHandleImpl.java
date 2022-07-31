package main.java.component.impl;

import main.java.component.*;

import java.io.File;
import java.util.List;

public class MachineHandleImpl implements MachineHandler {

    private EncryptionMachine machine = new EnigmaMachine();
    //TODO: add "machine state" member

    @Override
    public boolean loadMachineConfiguration(File file) {
        return false;
    }

    @Override
    public boolean setState() {
        return false;
    }


    @Override
    public boolean loadStateFromFile(String absolutePath) {
        return false;
    }

    @Override
    public boolean loadStateManually(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors) {
        return false;
    }


    @Override
    public boolean saveStateToFile(String fileName) {
        return false;
    }

    @Override
    public List<String> getMachineState() {
        return null;
    }

    @Override
    public boolean resetToLastSetState() {
        return false;
    }

    @Override
    public String sendInputToMachine(String input) {
        return null;
    }

    @Override
    public String getMachineHistory() {
        return null;
    }

    @Override
    public String getMachineStatistics() {
        return null;
    }
}
