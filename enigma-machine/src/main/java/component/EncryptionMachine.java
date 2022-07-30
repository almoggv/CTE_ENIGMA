package main.java.component;

import java.util.List;

public interface EncryptionMachine {

    public String encrypt(String input);

    public String decrypt(String output);

    public boolean setupMachine(Object... args);

    public List<String> getMachineState();
}


