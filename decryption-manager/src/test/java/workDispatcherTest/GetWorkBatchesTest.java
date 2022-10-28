package workDispatcherTest;

import component.MachineHandler;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import enums.ReflectorsId;
import org.junit.Assert;
import org.junit.Test;
import service.WorkDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetWorkBatchesTest {

    @Test
    public void getEasyWorkBatchTest() throws Exception {
        MachineHandler machineHandler = WorkDispatcherTestUtils.loadAMachineHandlerRandomly();
        MachineState firstWorkState = WorkDispatcherTestUtils.getStartingState();
        DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.EASY;
        List<MachineState> createdWorkBatch = new ArrayList<>();
        createdWorkBatch = WorkDispatcher.getWorkBatch(firstWorkState,difficultyLevel
                ,WorkDispatcherTestUtils.REQUESTED_EASY_BATCH_SIZE,machineHandler.getInventoryInfo().get());
        Assert.assertTrue(!createdWorkBatch.isEmpty());
    }

    @Test
    public void TestRotorPositionLoopOnEasy() throws Exception {
        MachineHandler machineHandler = WorkDispatcherTestUtils.loadAMachineHandlerRandomly();
        MachineState firstWorkState = WorkDispatcherTestUtils.getProgressedState();
        DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.EASY;
        List<MachineState> createdWorkBatch = new ArrayList<>();
        createdWorkBatch = WorkDispatcher.getWorkBatch(firstWorkState,difficultyLevel
                ,WorkDispatcherTestUtils.REQUESTED_EASY_BATCH_SIZE,machineHandler.getInventoryInfo().get());
        Assert.assertTrue(createdWorkBatch.contains(WorkDispatcherTestUtils.getStartingState()));
    }

    @Test
    public void getMediumWorkBatchTest() throws Exception {
        MachineHandler machineHandler = WorkDispatcherTestUtils.loadAMachineHandlerRandomly();
        MachineState firstWorkState = WorkDispatcherTestUtils.getProgressedState();
        DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.INTERMEDIATE;
        List<MachineState> createdWorkBatch = new ArrayList<>();
        createdWorkBatch = WorkDispatcher.getWorkBatch(firstWorkState,difficultyLevel
                ,WorkDispatcherTestUtils.REQUESTED_MEDIUM_BATCH_SIZE,machineHandler.getInventoryInfo().get());
        Assert.assertTrue(!createdWorkBatch.isEmpty());
        MachineState expectedState = WorkDispatcherTestUtils.getStartingState();
        expectedState.setReflectorId(ReflectorsId.II);
        Assert.assertTrue(createdWorkBatch.contains(expectedState));
    }

    @Test
    public void testReflectorIdLoopOnMedium() throws Exception {
        MachineHandler machineHandler = WorkDispatcherTestUtils.loadAMachineHandlerRandomly();
        MachineState firstWorkState = WorkDispatcherTestUtils.getProgressedState();
        firstWorkState.setReflectorId(ReflectorsId.III);
        DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.INTERMEDIATE;
        List<MachineState> createdWorkBatch = new ArrayList<>();
        createdWorkBatch = WorkDispatcher.getWorkBatch(firstWorkState,difficultyLevel
                ,WorkDispatcherTestUtils.REQUESTED_MEDIUM_BATCH_SIZE,machineHandler.getInventoryInfo().get());
        Assert.assertTrue(!createdWorkBatch.isEmpty());
        MachineState expectedState = WorkDispatcherTestUtils.getStartingState();
        Assert.assertTrue(createdWorkBatch.contains(expectedState));
    }

    @Test
    public void getHardWorkBatchTest() throws Exception {
        MachineHandler machineHandler = WorkDispatcherTestUtils.loadAMachineHandlerRandomly();
        MachineState firstWorkState = WorkDispatcherTestUtils.getProgressedState();
        firstWorkState.setReflectorId(ReflectorsId.III);
        firstWorkState.setRotorIds(Arrays.asList(2,1,3));
        DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.HARD;
        List<MachineState> createdWorkBatch = new ArrayList<>();
        createdWorkBatch = WorkDispatcher.getWorkBatch(firstWorkState,difficultyLevel
                ,WorkDispatcherTestUtils.REQUESTED_HARD_BATCH_SIZE,machineHandler.getInventoryInfo().get());
        Assert.assertTrue(!createdWorkBatch.isEmpty());
        MachineState expectedState = WorkDispatcherTestUtils.getStartingState();
        expectedState.setReflectorId(ReflectorsId.I);
        expectedState.setRotorIds(Arrays.asList(2,3,1));
        Assert.assertTrue(createdWorkBatch.contains(expectedState));
    }

    @Test
    public void getImpossibleWorkBatchTest() throws Exception {
        MachineHandler machineHandler = WorkDispatcherTestUtils.loadAMachineHandlerRandomly();
        MachineState firstWorkState = WorkDispatcherTestUtils.getProgressedState();
        firstWorkState.setReflectorId(ReflectorsId.III);
        firstWorkState.setRotorIds(Arrays.asList(2,1,3));
        DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.IMPOSSIBLE;
        List<MachineState> createdWorkBatch = new ArrayList<>();
        createdWorkBatch = WorkDispatcher.getWorkBatch(firstWorkState,difficultyLevel
                ,WorkDispatcherTestUtils.REQUESTED_IMPOSSIBLE_BATCH_SIZE,machineHandler.getInventoryInfo().get());
        Assert.assertTrue(!createdWorkBatch.isEmpty());
        MachineState expectedState = WorkDispatcherTestUtils.getStartingState();
        expectedState.setReflectorId(ReflectorsId.I);
        expectedState.setRotorIds(Arrays.asList(2,1,4));
        Assert.assertTrue(createdWorkBatch.contains(expectedState));
    }



}
