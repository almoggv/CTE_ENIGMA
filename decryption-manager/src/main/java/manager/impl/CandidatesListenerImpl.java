package main.java.manager.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import main.java.adapter.UIAdapter;
import main.java.agent.DecryptionAgent;
import main.java.dto.AgentDecryptionInfo;
import main.java.generictype.MappingPair;
import main.java.manager.AgentWorkManager;
import main.java.manager.CandidatesListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CandidatesListenerImpl implements CandidatesListener {


    private UIAdapter uiAdapter;
    private int totalWorkAmount;
    private int amountOfWorkCompleted = 0;

    private List<AgentDecryptionInfo> decryptionInfoList = new ArrayList<>();
    @Getter private final BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty(false);

    private final Object lockContext = new Object();
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty(true);
    @Getter private final BooleanProperty isPausedProperty = new SimpleBooleanProperty(false);
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
    }

    public void stop(){
        isStoppedProperty.setValue(true);
        isRunningProperty.setValue(false);
    }

    public void pause() {
        isPausedProperty.setValue(true);
    }

    public void resume() {
        synchronized (lockContext) {
            isPausedProperty.setValue(false);
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
        throw new NotImplementedException();
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
