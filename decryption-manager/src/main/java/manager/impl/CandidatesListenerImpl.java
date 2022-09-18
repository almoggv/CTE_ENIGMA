package main.java.manager.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import main.java.adapter.UIAdapter;
import main.java.agent.DecryptionAgent;
import main.java.dto.AgentDecryptionInfo;
import main.java.manager.AgentWorkManager;
import main.java.manager.CandidatesListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CandidatesListenerImpl implements CandidatesListener {


    private UIAdapter uiAdapter;

    private List<AgentDecryptionInfo> decryptionInfoList = new ArrayList<>();

    private final Object lockContext = new Object();
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty();
    @Getter private final BooleanProperty isStoppedProperty = new SimpleBooleanProperty();

    public CandidatesListenerImpl(UIAdapter uiAdapter, BooleanProperty onAllWorkersFinishedSignal) {
        this.uiAdapter = uiAdapter;
        onAllWorkersFinishedSignal.addListener(((observable, oldValue, newValue) -> {
            if(newValue == true){
                this.stop();
            }
        }));
    }

    @Override
    public BooleanProperty getIsRunningProperty() {
        return null;
    }

    @Override
    public BooleanProperty getIsStoppedProperty() {
        return null;
    }

    public void stop(){
        isStoppedProperty.setValue(true);
        isRunningProperty.setValue(false);
    }

    public void pause() {
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
        newestAgentProperty.addListener(((observable, oldValue, newValue) -> {
            newValue.getPotentialCandidatesListProperty().addListener(((observable1, oldInfoListValue, newInfoListValue) -> {
                for (AgentDecryptionInfo info : newInfoListValue ) {
                    this.decryptionInfoList.add(info);
                    uiAdapter.addNewCandidate(info);
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
            //Do nothing
        }
    }
}
