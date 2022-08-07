package main.java.component;

import main.java.dto.InventoryInfo;
import main.java.generictype.MappingPair;
import main.java.dto.MachineState;
import main.java.dto.EncryptionInfoHistory;
import main.java.enums.ReflectorsId;

import java.util.List;

public interface MachineHandler {

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
    public void assembleMachine();
    /**
     * call buildMachinePartsInventory before assembling!!!!
     * assembles a machine from user instructions
     * sets reflector, plugBoardMapping, rotors and their starting position
     */
    public void assembleMachine(ReflectorsId reflectorId, List<Integer> rotorIds,
           List<Integer> rotorsStartingPositions,
           List<MappingPair<String,String>> plugMapping );


    public void assembleMachineParts(ReflectorsId reflectorId, List<Integer> rotorIds);

    public void setStartingMachineState(List<Integer> rotorsStartingPositions,
            List<MappingPair<String,String>> plugMapping);

    public MachineState getMachineState();

    public InventoryInfo getInventoryInfo();

    public void resetToLastSetState();

    /**
     * encrypt message by machine
     * @param input
     * @return encrypted message
     */
    public String encrypt(String input);

    public List<EncryptionInfoHistory> getMachineStatisticsHistory();

    //todo - decide if better and worth the headache
//    public Map<MachineState,List<EncryptionInfoHistory>> getMachineStatisticsHistoryMap();

    //bonus
    public boolean loadStateFromFile(String absolutePath);

    //bonus
    public boolean saveStateToFile(String fileName);
 }
