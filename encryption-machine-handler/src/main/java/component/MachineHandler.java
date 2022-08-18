package main.java.component;

import main.java.dto.InventoryInfo;
import main.java.generictype.MappingPair;
import main.java.dto.MachineState;
import main.java.dto.EncryptionInfoHistory;
import main.java.enums.ReflectorsId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MachineHandler extends Serializable {

    /**
     * given an XML file path describing an CTEEnigma machine schema.
     * verifies file and schema information.
     * creates inventory of usable components of enigma machine.
     * @param path
     */
    void buildMachinePartsInventory (String path) throws Exception;

    /**
     * call buildMachinePartsInventory before assembling!!!!
     * assembles a machine randomly from existing inventory
     * picks reflector, plugBoardMapping, rotors and their starting position 
     * @return
     */
    public void assembleMachine() throws Exception;

    public void assembleMachine(ReflectorsId reflectorId, List<Integer> rotorIds,
                                String rotorsStartingPositions,
                                List<MappingPair<String,String>> plugMapping ) throws Exception;

    public void assembleMachineParts(ReflectorsId reflectorId, List<Integer> rotorIds) throws Exception;

    public void setStartingMachineState(String rotorsStartingPositions,
                                        List<MappingPair<String,String>> plugMapping);

    public Optional<MachineState> getMachineState();
    public Optional<MachineState> getInitialMachineState();

    public Optional<InventoryInfo> getInventoryInfo();

    public void resetToLastSetState();

    /**
     * encrypt message by machine
     * @param input
     * @return encrypted message
     */
    public String encrypt(String input) throws IOException;

    public Map<MachineState, List<EncryptionInfoHistory>> getMachineStatisticsHistory();

//    //bonus
//    public void loadStateFromFile(String absolutePath) throws FileNotFoundException, Exception;
//
//    //bonus
//    public void saveStateToFile(String fileName) throws Exception;

    public Optional<String> verifyInputInAbcAndFix(String input);
 }
