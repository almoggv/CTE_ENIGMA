package main.java.component.impl;

import main.java.component.EncryptionMachine;
import main.java.component.MachineHandler;

import java.io.File;
import java.util.List;

public class MachineHandleImpl implements MachineHandler {

    private EncryptionMachine machine = new EnigmaMachine();
    //TODO: add machine state member

    @Override
    public boolean loadMachineConfiguration(File file) {
        return false;
    }

    @Override
    public boolean setState() {
        return false;
    }

    @Override
    public boolean setState(File file) {
        return false;
    }

    @Override
    public boolean setState(Object... args) {
        return false;
    }

    @Override
    public boolean saveState(File file) {
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
