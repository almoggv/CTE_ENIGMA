package DecryptionWorkerTest;

import agent.DecryptionWorker;
import agent.impl.DecryptionWorkerImpl;
import component.MachineHandler;
import dto.MachineState;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WorkerFindingInputTest {


    @Test
    public void TestIfWorkerFindsTheRightInput() throws Exception {
        List<String> candidates = new ArrayList<>();
        MachineHandler handler = WorkerTestUtils.loadAMachineHandlerManually(WorkerTestUtils.getInitialState());
        DecryptionWorker worker = new DecryptionWorkerImpl(handler.getEncryptionMachineClone());
        List<MachineState> work = WorkerTestUtils.createWorkToDo(100,WorkerTestUtils.getBasicState());
        String inputToDecrypt = handler.encrypt(WorkerTestUtils.INPUT_TO_ENCRYPT);
        worker.assignWork(work,inputToDecrypt);
        worker.getLastFoundCandidateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                System.out.println("[######] Worker Found a Candidate - " + newValue);
                candidates.add(newValue.getOutput());
            }
        });
        worker.getProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue.getLeft()%5==0){
                System.out.println("Progress = " + newValue);
            }
        });

        Thread workerThread = new Thread(worker);
        workerThread.start();
        workerThread.join();
        Assert.assertTrue(candidates.contains(WorkerTestUtils.INPUT_TO_ENCRYPT));
    }
}
