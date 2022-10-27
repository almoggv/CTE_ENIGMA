package adapter;

import agent.DecryptionAgent;
import dto.AgentDecryptionInfo;
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
            p.load(XmlFileLoader.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
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
    @Getter private final Map<UUID, DecryptionAgent> agentIdToDecryptAgentMap = new HashMap<>();
    @Getter private final ObjectProperty<List<AgentDecryptionInfo>> decryptionCandidatesProperty = new SimpleObjectProperty<>(new ArrayList<>());

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
        });
    }

    public ObjectProperty<MappingPair<Integer,Integer>> getProgressProperty(){
        return this.getFinishedWorkProgressProperty();
    }

    public void connectToAgent(DecryptionAgent newAgent){
        if(newAgent == null || agentIdToDecryptAgentMap.containsKey(newAgent.getId())){
            log.debug("Failed to connect to agent, Agent is null or already exists, newAgent=" + newAgent);
            return;
        }
        agentIdToDecryptAgentMap.putIfAbsent(newAgent.getId(),newAgent);
        newAgent.getPotentialCandidatesListProperty().addListener((observable, oldValue, newValue) -> {
            synchronized (this){
                if(!newValue.isEmpty()){
                    List<AgentDecryptionInfo> newListToTriggerChange = new ArrayList<>(decryptionCandidatesProperty.get());
                    newListToTriggerChange.addAll(newValue);
                    this.decryptionCandidatesProperty.setValue(newListToTriggerChange);
                }
            }
        });
        newAgent.getProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newAgent.getIsFinishedProperty().get()){
                synchronized (this){
                    int currentProgress = newAgent.getProgressProperty().get().getRight() + this.finishedWorkProgressProperty.get().getLeft();
                    this.finishedWorkProgressProperty.setValue(new MappingPair<Integer,Integer>(currentProgress, this.finishedWorkProgressProperty.get().getRight()));
                }
            }
        });
    }

    @Override
    public void run() {
        //do nothing, only listen
    }
}
