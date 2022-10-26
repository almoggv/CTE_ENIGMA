package manager.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import service.adapter.UIAdapter;
import agent.DecryptionAgent;
import dto.AgentDecryptionInfo;
import generictype.MappingPair;
import manager.AgentWorkManager;
import manager.CandidatesListener;

import java.util.ArrayList;
import java.util.List;

public class CandidatesListenerImpl implements CandidatesListener {


    private UIAdapter uiAdapter;
    private int totalWorkAmount;
    private int amountOfWorkCompleted = 0;

    private List<AgentDecryptionInfo> decryptionInfoList = new ArrayList<>();
    @Getter private final BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty(false);

    private long meanDecryptionTime = 0;

    private final Object lockContext = new Object();
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty(true);
    @Getter private final BooleanProperty isStoppedProperty = new SimpleBooleanProperty(false);

    public CandidatesListenerImpl(UIAdapter uiAdapter, BooleanProperty onAllWorkersFinishedSignal, int totalWorkAmount) {
        this.uiAdapter = uiAdapter;
        this.totalWorkAmount = totalWorkAmount;
        onAllWorkersFinishedSignal.addListener(((observable, oldValue, newValue) -> {
            if(newValue == true){
                this.stop();
                isWorkCompletedProperty.setValue(true);
            }
        }));
        this.isStoppedProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue == true && meanDecryptionTime != 0){
                uiAdapter.sendMeanTimeOfDecryption(meanDecryptionTime);
            }
        });
        this.isWorkCompletedProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue == true && meanDecryptionTime != 0){
                uiAdapter.sendMeanTimeOfDecryption(meanDecryptionTime);
            }
        });
    }

    public void stop(){
        isStoppedProperty.setValue(true);
        isRunningProperty.setValue(false);
    }

    public void pause() {
//        isPausedProperty.setValue(true);
        isRunningProperty.setValue(false);
    }

    public void resume() {
        synchronized (lockContext) {
            isRunningProperty.setValue(true);
            lockContext.notifyAll(); // Unblocks thread
        }
    }

    //Not in use
//    public void BindToWorkers(IntegerProperty sizeOfMapProperty, Map<UUID,DecryptionAgent> agentIdToDecryptAgentMap ) {
//        this.agentIdToDecryptAgentMap = agentIdToDecryptAgentMap;
//        for (UUID agentId : agentIdToDecryptAgentMap.keySet()) {
//            DecryptionAgent agent = agentIdToDecryptAgentMap.get(agentId);
//            agent.getPotentialCandidatesListProperty().addListener(((observable, oldValue, newValue) -> {
//                for (AgentDecryptionInfo info : newValue ) {
//                    uiAdapter.addNewCandidate(info);
//                }
//            }));
//        }
//    }

    @Override
    public void BindToWorkers(ObjectProperty<DecryptionAgent> newestAgentProperty) {
        newestAgentProperty.addListener(((observable, oldAgent, newAgent) -> {
            newAgent.getIsFinishedProperty().addListener(((observable1, isFinishedOldValue, isFinishedNewValue) -> {
                synchronized (this){
                    if(isFinishedNewValue == true){
                        int oldAmountOfWorkCompleted = amountOfWorkCompleted;
                        amountOfWorkCompleted += newAgent.getProgressProperty().get().getRight();
                        if(oldAmountOfWorkCompleted< amountOfWorkCompleted){
                            uiAdapter.updateProgress(new MappingPair<>(amountOfWorkCompleted,totalWorkAmount));
                        }
                        //Elapsed Encryption Time:
                        if(meanDecryptionTime == 0){
                            meanDecryptionTime += newAgent.getTimeTookToCompleteWork();
                        }
                        else{
                            meanDecryptionTime = (long)(meanDecryptionTime + newAgent.getTimeTookToCompleteWork())/2;
                        }
                    }
                }
            }));
            newAgent.getPotentialCandidatesListProperty().addListener(((observable1, oldInfoListValue, newInfoListValue) -> {
                synchronized (this){
                    for (AgentDecryptionInfo info : newInfoListValue ) {
                        this.decryptionInfoList.add(info);
                        uiAdapter.addNewCandidate(info);
                    }
                }
            }));
        }));
    }

    @Override
    public void BindToManager(AgentWorkManager agentWorkManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        while (!isStoppedProperty.get()) {
            synchronized (lockContext) {
                if (isRunningProperty.get() == false) {
                    try {
                        lockContext.wait();
                    } catch (InterruptedException ignore) {}
                }
            }
            //Actual Work:
            //Do nothing - works with listeners
        }
    }
}
