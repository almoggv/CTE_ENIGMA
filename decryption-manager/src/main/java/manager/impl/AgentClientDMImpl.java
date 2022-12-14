package manager.impl;

import adapter.ListenerAdapter;
import agent.DecryptionWorker;
import agent.impl.DecryptionWorkerImpl;
import common.ListUtils;
import component.MachineHandler;
import dto.MachineState;
import generictype.MappingPair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import manager.AgentClientDM;
import manager.DictionaryManagerStatic;
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

    @Getter @Setter private String inputToDecrypt = "";
    @Getter private final int maxNumberOfTasks;
    @Getter @Setter private int internalAgentTaskSize = PropertiesService.getDefaultTaskSize();
    private final ThreadPoolExecutor threadPoolService;
    @Getter private MachineHandler machineHandler;
    private final List<List<MachineState>> workBatches = new ArrayList<>();
    private boolean isKilled = false;

    @Getter private final ObjectProperty<DecryptionWorker> newestAgentProperty = new SimpleObjectProperty<>();
    @Getter private final BooleanProperty isReadyForMoreWorkProperty = new SimpleBooleanProperty(true);

    //To Prevent Spamming:
    private boolean didLogComponentsReadyMsg = false;


    public AgentClientDMImpl(@NotNull MachineHandler machineHandler, int maxNumberOfTasks, int threadPoolSize) throws IllegalArgumentException{
        if(machineHandler == null){
            throw new NullPointerException("machineHandler is null");
        }
        if(!machineHandler.getInventoryInfo().isPresent()){
            throw new IllegalArgumentException("machineHandler's machine schema was not loaded yet");
        }
        if (threadPoolSize > PropertiesService.getMaxThreadPoolSize() || threadPoolSize < PropertiesService.getMinThreadPoolSize()) {
            throw new IllegalArgumentException("AgentClient Constructor - ThreadPool of size=" + threadPoolSize + " is not allowed, range=["  + PropertiesService.getMinThreadPoolSize() + "," + PropertiesService.getMaxThreadPoolSize() + "]");
        }
        this.machineHandler = machineHandler;
        this.maxNumberOfTasks = maxNumberOfTasks;
        long keepAliveForWhenIdle = Long.MAX_VALUE;
        this.threadPoolService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize,
                keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(THREAD_POOL_QEUEU_DEFAULT_MAX_CAPACITY));
        this.addPropertyListeners();
        isReadyForMoreWorkProperty.setValue(true);
        listenerAdapter.getProgressProperty().setValue(new MappingPair<>(0,0));
    }

    private void addPropertyListeners(){
        this.getNewestAgentProperty().addListener((observable, oldNewstAgent, newNewestAgent) -> {
            if(newNewestAgent == null || newNewestAgent.equals(oldNewstAgent)){
                return;
            }
            listenerAdapter.connectToAgent(newNewestAgent);
        });
        listenerAdapter.getProgressProperty().setValue(null);

    }

    @Override
    public void run() {
        listenerAdapterThread = new Thread(listenerAdapter);
        listenerAdapterThread.start();
        this.isKilled = false;
        int ticks =0;   //for testing
        int ticksPerPrint = 1000000;  //for testing
        while(!isKilled){
            // Prevent Log Spamming - for testing
            ticks++;
            if(ticks % ticksPerPrint == 0){
                log.info("AgentClientDM is running");
                ticks = 0;
                if(workBatches.isEmpty()){
                    log.info("AgentClient - assigned all current workBatches to Workers, WorkBatches Size=" + workBatches.size());
                }
                if(!areComponentsReadyForWork()) {
                    log.warn("Components are not ready for work yet");
                }
            }
            if(!areComponentsReadyForWork()){
                continue;
            }
            ///////////////////////////////////
            if(!workBatches.isEmpty() && threadPoolService.getQueue().remainingCapacity() > 0){
                List<MachineState> batch = workBatches.remove(0);
                DecryptionWorker newAgent = new DecryptionWorkerImpl(machineHandler.getEncryptionMachineClone());
                newAgent.assignWork(batch, inputToDecrypt);
                newestAgentProperty.setValue(newAgent);
                threadPoolService.execute(newAgent);
            }
            if(workBatches.isEmpty() && listenerAdapter.getIsWorkCompletedProperty().get()){
                isReadyForMoreWorkProperty.setValue(true);
            }
        }
        //when killed:
        listenerAdapterThread.interrupt();
    }

    private boolean areComponentsReadyForWork(){
        boolean areComponentsReady = true;
        if(this.machineHandler == null){
            if(!didLogComponentsReadyMsg){
                log.error("Failed to divide work - machineHandler is null");
            }
            areComponentsReady = false;
        }
        if(!this.machineHandler.getInventoryInfo().isPresent()){
            if(!didLogComponentsReadyMsg){
                log.error("Failed to divide work - machineHandler is missing inventory");
            }
            areComponentsReady = false;
        }
        if(!this.machineHandler.getMachineState().isPresent()){
            if(!didLogComponentsReadyMsg){
                log.error("Failed to divide work - Encryption Machine in MachineHandler was not configured (assembled)");
            }
            areComponentsReady = false;
        }
        if(DictionaryManagerStatic.getDictionary().isEmpty()){
            log.error("Failed to divide work - Dictionary is empty");
            areComponentsReady = false;
        }
        didLogComponentsReadyMsg = !areComponentsReady;

        return areComponentsReady;
    }

    @Override
    synchronized public void assignWork(List<MachineState> assignedWork,String inputToDecrypt) throws IllegalArgumentException , NullPointerException , RuntimeException {
        if(inputToDecrypt == null && (this.inputToDecrypt == null || this.inputToDecrypt.isEmpty())){
            log.error("AgentClientDMImpl - failed to assign work, missing input to decrypt");
            return;
        }
        this.inputToDecrypt = (inputToDecrypt == null) ? this.inputToDecrypt : inputToDecrypt;  //if new input is null, use last given input
        if(assignedWork == null || assignedWork.isEmpty()){
            log.warn("Failed to assign work, given workload is null or empty");
            return;
        }
        isReadyForMoreWorkProperty.setValue(false);
        //Update Listeners progress:
        synchronized (listenerAdapter.getProgressProperty()){
            MappingPair<Integer,Integer> currentProgress = listenerAdapter.getProgressProperty().get();
            int newMaxProgress = currentProgress.getRight() + assignedWork.size();
            int currActualProgress = currentProgress.getLeft();
            MappingPair<Integer,Integer> newProgress = new MappingPair<>(currActualProgress,newMaxProgress);
            listenerAdapter.getProgressProperty().setValue(newProgress);
        }
        //////////////////////////
        List<List<MachineState>> newWorkBatches = ListUtils.partition(assignedWork, internalAgentTaskSize, true);
        workBatches.addAll(newWorkBatches);
//        updateListenerMaxProgress(assignedWork.size()); //is doing so him self through connect to worker
        log.info("AgentClientDm - received work, workAmount=" + assignedWork.size() + ", on Input="+ this.inputToDecrypt);
    }

//    private void updateListenerMaxProgress(int amount) {
//        if(listenerAdapter.getProgressProperty().get() == null){
//            MappingPair<Integer,Integer> newProgress =new MappingPair<>(0,0);
//            listenerAdapter.getProgressProperty().setValue(newProgress);
//            return;
//        }
//        MappingPair<Integer,Integer> newProgress =new MappingPair<>(listenerAdapter.getProgressProperty().get().getLeft(),listenerAdapter.getProgressProperty().get().getRight() + amount);
//        listenerAdapter.getProgressProperty().setValue(newProgress);
//    }

    @Override
    public synchronized void kill() {
        isKilled = true;
        listenerAdapter.kill();
        inputToDecrypt = "";
        machineHandler = null;
        workBatches.clear();
        threadPoolService.shutdown();
        log.info("AgentClientDM - was killed");
    }


}
