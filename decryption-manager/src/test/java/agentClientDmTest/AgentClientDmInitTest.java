package agentClientDmTest;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import manager.AgentClientDM;
import manager.impl.AgentClientDMImpl;
import org.junit.Test;
import service.MathService;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static enums.DecryptionDifficultyLevel.*;

public class AgentClientDmInitTest {

    @Test
    public void initializationTest() throws Exception {
        MachineHandler machineHandler = AgentDmTestUtils.loadAMachineHandler();
        AgentClientDM agentDm = new AgentClientDMImpl(machineHandler,AgentDmTestUtils.MAX_NUM_OF_TASKS,AgentDmTestUtils.MAX_NUM_OF_THREADS,AgentDmTestUtils.ALLY_NAME);
        List<MachineState> workToDo = AgentDmTestUtils.createWorkToDo(AgentDmTestUtils.MAX_NUM_OF_TASKS, machineHandler.getMachineState().get());
        agentDm.assignWork(workToDo,AgentDmTestUtils.INPUT_TO_DECRYPT);
        try{
            agentDm.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
