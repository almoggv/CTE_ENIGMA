package component;

import dto.BattlefieldInfo;
import dto.InventoryInfo;
import generictype.MappingPair;
import dto.MachineState;
import dto.EncryptionInfoHistory;
import enums.ReflectorsId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    public void assembleMachine(MachineState machineState) throws Exception;

    public void assembleMachine(ReflectorsId reflectorId, List<Integer> rotorIds,
                                String rotorsStartingPositions,
                                List<MappingPair<String,String>> plugMapping ) throws Exception;

    public void assembleMachineParts(ReflectorsId reflectorId, List<Integer> rotorIds) throws Exception;

    public void setStartingMachineState(String rotorsStartingPositions,
                                        List<MappingPair<String,String>> plugMapping);

    public Optional<MachineState> getMachineState();
    public void setMachineState(MachineState machineState);
    public Optional<MachineState> getInitialMachineState();

    public Optional<InventoryInfo> getInventoryInfo();
    public Optional<BattlefieldInfo> getBattlefieldInfo();

    public void resetToLastSetState();

    /**
     * encrypt message by machine
     * @param input
     * @return encrypted message
     */
    public String encrypt(String input) throws IOException;
    public String encryptWithoutHistory(String input) throws IOException;

    public Map<MachineState, List<EncryptionInfoHistory>> getMachineStatisticsHistory();

    public Optional<String> verifyInputInAbcAndFix(String input);

    public EncryptionMachine getEncryptionMachineClone();

    void buildMachinePartsInventory(InputStream inputStream) throws Exception;

    public BattlefieldInfo buildBattlefieldInfoInventory(InputStream inputStream) throws Exception;
    }
