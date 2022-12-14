package component;

import generictype.DeepCloneable;
import generictype.MappingPair;
import dto.MachineState;

import java.io.Serializable;
import java.sql.Ref;
import java.util.List;
import java.util.Optional;

public interface EncryptionMachine extends DeepCloneable, Serializable {

    IOWheel getIoWheel();
    PlugBoard getPlugBoard();
    Reflector getReflector();
    List<Rotor> getRotors();


    public String encrypt(String input);

    public String decrypt(String output);

    /**
     * assembles a machine with the given parts
     * @param plugBoard
     * @param reflector
     * @param rotors
     * @param ioWheel
     * @throws IllegalArgumentException
     */
    public void buildMachine(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors, IOWheel ioWheel) throws IllegalArgumentException;

    public void buildMachine(Reflector reflector, List<Rotor> rotors , IOWheel ioWheel, String ABC) throws IllegalArgumentException;

    /**
     * connect the plugs on the left to the plugs on the right
     * @param leftList
     * @param rightList
     */
    public void connectPlugs(List<String> leftList, List<String> rightList);

    public void connectPlugs(List<MappingPair<String,String>> plugMappingList);

    public void setRotorsStartingPosition(List<Integer> valuesToSetTheHead);

    public Optional<MachineState> getMachineState();

    public void setMachineState(MachineState machineState);

    public String getABC();

    @Override
    public EncryptionMachine getDeepClone();
}


