package agentClientDmTest;

import component.MachineHandler;
import dto.MachineState;
import manager.AgentClientDM;
import manager.impl.AgentClientDMImpl;
import org.junit.Test;

import java.util.List;

public class AgentClientDmInitTest {

    @Test
    public void initializationTest() throws Exception {
        MachineHandler machineHandler = AgentDmTestUtils.loadAMachineHandlerRandomly();
        AgentClientDM agentDm = new AgentClientDMImpl(machineHandler,AgentDmTestUtils.MAX_NUM_OF_TASKS,AgentDmTestUtils.MAX_NUM_OF_THREADS,AgentDmTestUtils.ALLY_NAME);
        List<MachineState> workToDo = AgentDmTestUtils.createWorkToDo(AgentDmTestUtils.MAX_NUM_OF_TASKS, machineHandler.getMachineState().get());
        agentDm.assignWork(workToDo,AgentDmTestUtils.INPUT_TO_ENCRYPT);
        try{
            agentDm.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
