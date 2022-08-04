package main.java.component;

import java.util.List;

public interface EncryptionMachine {

    public String encrypt(String input);

    public String decrypt(String output);

    public void buildMachine(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors, IOWheel ioWheel) throws IllegalArgumentException;

    public void buildMachine(Reflector reflector, List<Rotor> rotors , IOWheel ioWheel, String ABC) throws IllegalArgumentException;

    /**
     * connect the plugs on the left to the plugs on the right
     * @param leftList
     * @param rightList
     */
    public void connectPlugs(List<String> leftList, List<String> rightList);

    public void setRotorsStartingPosition(List<Integer> rotorsPosition);

    public List<String> getMachineState();
}


