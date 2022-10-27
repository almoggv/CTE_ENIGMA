package manager.impl;

import adapter.ListenerAdapter;
import agent.DecryptionAgent;
import agent.DecryptionWorker;
import agent.impl.DecryptionAgentImpl;
import agent.impl.DecryptionWorkerImpl;
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

    @Getter ListenerAdapter listenerAdapter = new ListenerAdapter();
    private Thread listenerAdapterThread;

    private String allyTeamName;
    @Getter @Setter private String inputToDecrypt = "";
    @Getter private final int maxNumberOfTasks;
    @Getter @Setter private int internalAgentTaskSize = PropertiesService.getDefaultTaskSize();
    private final ThreadPoolExecutor threadPoolService;
    private final MachineHandler machineHandler;
    private final List<MachineState> workToDo = new ArrayList<>();
    private List<List<MachineState>> workBatches = new ArrayList<>();
    private boolean isKilled = false;

    @Getter private final ObjectProperty<DecryptionWorker> newestAgentProperty = new SimpleObjectProperty<>();
    private final BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty(true);

    public AgentClientDMImpl(@NotNull MachineHandler machineHandler, int maxNumberOfTasks, int threadPoolSize, String allyTeamName) {
        if(machineHandler == null){
            throw new NullPointerException("machineHandler is null");
        }
        if(!machineHandler.getInventoryInfo().isPresent()){
            throw new IllegalArgumentException("machineHandler's machine schema was not loaded yet");
        }
        if (threadPoolSize > PropertiesService.getMaxThreadPoolSize() || threadPoolSize < PropertiesService.getMinThreadPoolSize()) {
            throw new IllegalArgumentException("AgentClient Constructor - ThreadPool of size=" + threadPoolSize + " is not allowed, range=["  + PropertiesService.getMinThreadPoolSize() + "," + PropertiesService.getMaxThreadPoolSize() + "]");
        }
        if(allyTeamName == null ){
            throw new NullPointerException("AllyTeamName is null");
        }
        this.machineHandler = machineHandler;
        this.maxNumberOfTasks = maxNumberOfTasks;
        long keepAliveForWhenIdle = Long.MAX_VALUE;
        this.allyTeamName = allyTeamName;
//        int threadPoolQueueSize = (int) Math.ceil((float)(maxNumberOfTasks / internalAgentTaskSize) ); //when too big, overflows
        this.threadPoolService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize,
                keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(THREAD_POOL_QEUEU_DEFAULT_MAX_CAPACITY));
        this.addPropertyListeners();
    }

    private void addPropertyListeners(){
        listenerAdapter.getFinishedWorkProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.getLeft() >= newValue.getRight()){
                workToDo.clear();
            }
        });
        this.getNewestAgentProperty().addListener((observable, oldNewstAgent, newNewestAgent) -> {
            if(newNewestAgent == null || newNewestAgent.equals(oldNewstAgent)){
                return;
            }
            listenerAdapter.connectToAgent(newNewestAgent);
        });
        listenerAdapter.getIsWorkCompletedProperty().bindBidirectional(this.isWorkCompletedProperty);
    }

    @Override
    public void run() {
        if(!areComponentsReadyForWork()){
            throw new RuntimeException("Run Failed, some components are not initialized");
        }
        listenerAdapterThread = new Thread(listenerAdapter);
        listenerAdapterThread.start();
        this.isKilled = false;
        int tiks =0;
        int tiksPerPrint = 100;

        while(!isKilled){
            // Prevent Log Spamming - for testing
            tiks++;
            if(tiks % tiksPerPrint == 0){
                log.debug("AgentClientDM is running");
                tiks = 0;
            }
            ///////////////////////////////////
            if(!workBatches.isEmpty() && threadPoolService.getQueue().remainingCapacity() > 0){
                List<MachineState> batch = workBatches.remove(0);
                DecryptionWorker newAgent = new DecryptionWorkerImpl(machineHandler.getEncryptionMachineClone());
                newAgent.assignWork(batch, inputToDecrypt);
                newestAgentProperty.setValue(newAgent);
                threadPoolService.execute(newAgent);
            }
        }

        listenerAdapterThread.interrupt();
    }

    private boolean areComponentsReadyForWork(){
        if(this.machineHandler == null){
            log.error("Failed to divide work - machineHandler is null");
            return false;
        }
        if(!this.machineHandler.getInventoryInfo().isPresent()){
            log.error("Failed to divide work - machineHandler is missing inventory");
            return false;
        }
        if(!this.machineHandler.getMachineState().isPresent()){
            log.error("Failed to divide work - Encryption Machine in MachineHandler was not configured (assembled)");
            return false;
        }
        return true;
    }

    @Override
    public void assignWork(List<MachineState> assignedWork,String inputToDecrypt) throws IllegalArgumentException , NullPointerException , RuntimeException {
        if(inputToDecrypt == null && (this.inputToDecrypt == null || this.inputToDecrypt.isEmpty())){
            log.error("AgentClientDMImpl - failed to assign work, missing input to decrypt");
            throw new NullPointerException("Failed to assign work, given no new or previous input to decrypt");
        }
        this.inputToDecrypt = (inputToDecrypt == null) ? this.inputToDecrypt : inputToDecrypt;  //if new input is null, use last given input
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
        this.listenerAdapter.getFinishedWorkProgressProperty().setValue(new MappingPair<>(0,assignedWork.size()));
        this.workToDo.addAll(assignedWork);
        workBatches = ListUtils.partition(workToDo, internalAgentTaskSize, true);
        log.info("AgentClientDm - received work, workAmount=" + workToDo.size() + ", on Input="+ this.inputToDecrypt);
    }

    @Override
    public void kill() {
        isKilled = true;
        log.info("AgentClientDM - was killed");
    }


}
