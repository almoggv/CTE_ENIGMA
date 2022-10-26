package manager.impl;

import agent.DecryptionAgent;
import agent.impl.DecryptionAgentImpl;
import common.ListUtils;
import component.MachineHandler;
import dto.AgentDecryptionInfo;
import dto.MachineState;
import generictype.MappingPair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import manager.AgentClientDM;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;
import service.PropertiesService;
import service.XmlFileLoader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentClientDMImpl implements AgentClientDM {
    private static final int THREAD_POOL_QEUEU_DEFAULT_MAX_CAPACITY = 50;
    private static final Logger log = Logger.getLogger(AgentClientDMImpl.class);
    static {
        try {
            Properties p = new Properties();
            p.load(XmlFileLoader.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AgentClientDMImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AgentClientDMImpl.class.getSimpleName() ) ;
        }
    }

    private String allyTeamName;
    @Getter @Setter private String inputToDecrypt = "";
    @Getter private final int maxNumberOfTasks;
    private int internalAgentTaskSize = PropertiesService.getDefaultTaskSize();
    private final ThreadPoolExecutor threadPoolService;
    private MachineHandler machineHandler;
    private List<MachineState> workToDo = new ArrayList<>();
    private boolean isKilled = false;

    @Getter private final BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty(true); //considering no work as work completed
    /**
     assignedWorkProgressProperty references the assigned work to the agents - and not the amount actually completed by the agents;
     */
    @Getter private final ObjectProperty<MappingPair<Integer,Integer>> finishedWorkProgressProperty = new SimpleObjectProperty<>(new MappingPair<>());
    @Getter private final ObjectProperty<List<AgentDecryptionInfo>> decryptionCandidatesProperty = new SimpleObjectProperty<>(new ArrayList<>());

    @Getter private final Map<UUID, DecryptionAgent> agentIdToDecryptAgentMap = new HashMap<>();
    @Getter private final ObjectProperty<DecryptionAgent> newestAgentProperty = new SimpleObjectProperty<>();

    public AgentClientDMImpl(@NotNull MachineHandler machineHandler, int maxNumberOfTasks, int threadPoolSize, String allyTeamName) {
        if(machineHandler == null){
            throw new NullPointerException("machineHandler is null");
        }
        if(!machineHandler.getInventoryInfo().isPresent()){
            throw new IllegalArgumentException("machineHandler's machine schema was not loaded yet");
        }
        this.maxNumberOfTasks = maxNumberOfTasks;
        if (threadPoolSize > PropertiesService.getMaxThreadPoolSize() || threadPoolSize < PropertiesService.getMinThreadPoolSize()) {
            throw new IllegalArgumentException("AgentClient Constructor - ThreadPool of size=" + threadPoolSize + " is not allowed, range=["  + PropertiesService.getMinThreadPoolSize() + "," + PropertiesService.getMaxThreadPoolSize() + "]");
        }
        if(allyTeamName == null ){
            throw new NullPointerException("AllyTeamName is null");
        }
        long keepAliveForWhenIdle = Long.MAX_VALUE;
        this.threadPoolService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(maxNumberOfTasks));
    }

    private void addPropertyListeners(){
        finishedWorkProgressProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.getLeft() >= newValue.getRight()){
                workToDo.clear();
            }
        });
    }

    @Override
    public void run() {
        isKilled = false;
        while(!isKilled){
            if(!workToDo.isEmpty()){
                divideWorkAndRun();
            }
        }
    }

    private void divideWorkAndRun() {
        if(this.workToDo.isEmpty()){
            log.warn("Failed to divide work, no work to divide");
            return;
        }
        List<List<MachineState>> workBatches = ListUtils.partition(workToDo, internalAgentTaskSize);
        for (List<MachineState> batch: workBatches ) {
            DecryptionAgent newAgent = new DecryptionAgentImpl(machineHandler.getEncryptionMachineClone());
            newAgent.assignWork(batch, inputToDecrypt);
            newestAgentProperty.setValue(newAgent);
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
            threadPoolService.execute(newAgent);
        }
        workToDo.clear();
    }

    @Override
    public ObjectProperty<MappingPair<Integer, Integer>> getProgressProperty() {
        return finishedWorkProgressProperty;
    }

    @Override
    public void assignWork(List<MachineState> assignedWork,String inputToDecrypt) throws IllegalArgumentException , NullPointerException , RuntimeException {
        if(inputToDecrypt == null && (this.inputToDecrypt == null || this.inputToDecrypt.isEmpty())){
            log.error("AgentClientDMImpl - failed to assign work, missing input to decrypt");
            throw new NullPointerException("Failed to assign work, given no new or previous input to decrypt");
        }
        this.inputToDecrypt = (inputToDecrypt == null) ? this.inputToDecrypt : inputToDecrypt;
        if(assignedWork == null){
            log.warn("Failed to assign work, given workload is null");
            throw new NullPointerException("Failed to assign work, given workload is null");
        }
        if(assignedWork.size() > this.maxNumberOfTasks){
            log.error("Failed to assign work, workload size=("+ assignedWork.size() +") exceeds allowed size of=" + maxNumberOfTasks);
            throw new IllegalArgumentException("workload size=("+ assignedWork.size() +") exceeds allowed size of=" + maxNumberOfTasks);
        }
        if(!isWorkCompletedProperty.get()){
            log.warn("Failed to assign work - previous work back hasn't finished yet");
            throw new RuntimeException("Failed to assign work - previous work back hasn't finished yet");
        }
        this.finishedWorkProgressProperty.setValue(new MappingPair<>(0,assignedWork.size()));
        this.workToDo.addAll(assignedWork);
    }

    @Override
    public void kill() {
        isKilled = true;
        log.info("AgentClientDM - was killed");
    }


}
