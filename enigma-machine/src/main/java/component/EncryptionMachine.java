package main.java.component;

import java.util.List;

public interface EncryptionMachine {

    public String encrypt(String input);

    public String decrypt(String output);

    public void setupMachine(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors) throws IllegalArgumentException;

    public List<String> getMachineState();
}


