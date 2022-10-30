package component;

import dto.*;
import generictype.MappingPair;
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
    void buildMachinePartsInventory (InventoryComponents inventoryComponents);

    /**
     * call buildMachinePartsInventory before assembling!!!!
     * assembles a machine randomly from existing inventory
     * picks reflector, plugBoardMapping, rotors and their starting position 
     * @return
     */
    void assembleMachine() throws Exception;
    void assembleMachine(MachineState machineState) throws Exception;

    void assembleMachine(ReflectorsId reflectorId, List<Integer> rotorIds,
                                String rotorsStartingPositions,
                                List<MappingPair<String,String>> plugMapping ) throws Exception;

    void assembleMachineParts(ReflectorsId reflectorId, List<Integer> rotorIds) throws Exception;

    void setStartingMachineState(String rotorsStartingPositions,
                                        List<MappingPair<String,String>> plugMapping);

    Optional<MachineState> getMachineState();
    void setMachineState(MachineState machineState);
    Optional<MachineState> getInitialMachineState();
    void setInitialMachineState(MachineState machineState);

    Optional<InventoryComponents> getInventoryComponents();
    Optional<InventoryInfo> getInventoryInfo();
    Optional<BattlefieldInfo> getBattlefieldInfo();
    void setBattlefieldInfo(BattlefieldInfo battlefieldInfo);

    void resetToLastSetState();

    /**
     * encrypt message by machine
     * @param input
     * @return encrypted message
     */
    String encrypt(String input) throws IOException;
    String encryptWithoutHistory(String input) throws IOException;

    Map<MachineState, List<EncryptionInfoHistory>> getMachineStatisticsHistory();
    void setMachineStatisticsHistory(Map<MachineState, List<EncryptionInfoHistory>> statisticsHistory);

    Optional<String> verifyInputInAbcAndFix(String input);
    Optional<String> checkInputIsInAbc(String input);

    void setEncryptionMachine(EncryptionMachine encryptionMachine);
    EncryptionMachine getEncryptionMachineClone();

    void buildMachinePartsInventory(InputStream inputStream) throws Exception;

    BattlefieldInfo buildBattlefieldInfoInventory(InputStream inputStream) throws Exception;


}
