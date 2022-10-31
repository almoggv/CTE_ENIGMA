package adapter;

import agent.DecryptionAgent;
import agent.DecryptionWorker;
import dto.AgentDecryptionInfo;
import dto.DecryptionCandidateInfo;
import generictype.MappingPair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import manager.impl.AgentClientDMImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import service.XmlFileLoader;

import java.io.IOException;
import java.util.*;

public class ListenerAdapter implements Runnable{

    private static final Logger log = Logger.getLogger(ListenerAdapter.class);
    static {
        try {
            Properties p = new Properties();
            p.load(ListenerAdapter.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + ListenerAdapter.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + ListenerAdapter.class.getSimpleName() ) ;
        }
    }

    /**
     assignedWorkProgressProperty references the assigned work to the agents - and not the amount actually completed by the agents;
     */
    @Getter private final ObjectProperty<MappingPair<Integer,Integer>> finishedWorkProgressProperty = new SimpleObjectProperty<>(new MappingPair<>());
    @Getter private final BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty(true); //considering no work as work completed
    @Getter private final Map<UUID, DecryptionWorker> agentIdToDecryptAgentMap = new HashMap<>();
    @Getter private final ObjectProperty<List<DecryptionCandidateInfo>> decryptionCandidatesProperty = new SimpleObjectProperty<>(new ArrayList<>());

    public ListenerAdapter() {
        finishedWorkProgressProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue == null || oldValue == null){
                return;
            }
            if(newValue.getLeft() == null || newValue.getRight() == null){
                return;
            }
            if(newValue.getLeft() >= newValue.getRight()){
                log.debug("Progress reached 100% - clearing agents list");
                agentIdToDecryptAgentMap.clear();
                isWorkCompletedProperty.setValue(true);
            }
            else{
                isWorkCompletedProperty.setValue(false);
            }
        });
    }

    public ObjectProperty<MappingPair<Integer,Integer>> getProgressProperty(){
        return this.getFinishedWorkProgressProperty();
    }

    public void connectToAgent(DecryptionWorker newAgent){
        if(newAgent == null || agentIdToDecryptAgentMap.containsKey(newAgent.getId())){
            log.debug("Failed to connect to agent, Agent is null or already exists, newAgent=" + newAgent);
            return;
        }
        agentIdToDecryptAgentMap.putIfAbsent(newAgent.getId(),newAgent);
        newAgent.getAllFoundDecryptionCandidatesProperty().addListener((observable, oldValue, newValue) -> {
            synchronized (this){
                if(newValue != null && !newValue.isEmpty()){
                    List<DecryptionCandidateInfo> newListToTriggerChange = new ArrayList<>(decryptionCandidatesProperty.get());
                    newListToTriggerChange.addAll(newValue);
                    this.decryptionCandidatesProperty.setValue(newListToTriggerChange);
                }
            }
        });
        newAgent.getIsWorkerFinishedWorkProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == true){
                int amountOfWorkFinished = (newAgent.getProgressProperty().get() == null) ? 0 : newAgent.getProgressProperty().get().getRight();
                int newProgress = finishedWorkProgressProperty.get().getLeft() + amountOfWorkFinished;
                MappingPair<Integer,Integer> updatedProgress = new MappingPair<>(newProgress, finishedWorkProgressProperty.get().getRight());
                finishedWorkProgressProperty.setValue(updatedProgress);
            }
        });
    }

    @Override
    public void run() {
        //do nothing, only listen
    }

    public synchronized void kill() {
        finishedWorkProgressProperty.setValue(new MappingPair<>(1,1));
        isWorkCompletedProperty.setValue(true);
        agentIdToDecryptAgentMap.clear();
        decryptionCandidatesProperty.get().clear();
    }
}
