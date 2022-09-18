package main.java.manager.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import main.java.adapter.UIAdapter;
import main.java.component.MachineHandler;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.manager.AgentWorkManager;
import main.java.manager.CandidatesListener;
import main.java.manager.DictionaryManager;
import main.java.manager.DecryptionManager;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

public class DecryptionManagerImpl implements DecryptionManager {
    private static final Logger log = Logger.getLogger(DecryptionManagerImpl.class);

    private final int THREAD_POOL_QEUEU_MAX_CAPACITY = 10;
    @Getter private MachineHandler machineHandler;
    private DictionaryManager dictionaryManager;
    @Getter @Setter private int numberOfAgents;
    @Getter @Setter private int taskSize;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel;
    private ThreadPoolExecutor threadPoolService;
    private AgentWorkManager agentWorkManager;
    private CandidatesListener candidatesListener;
    @Getter @Setter private UIAdapter uiAdapter;
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty();

    private Thread workManagerThread;
    private Thread candidatesListenerThread;
//    private final UiAdapter uiAdapter;

    static {
        try {
            Properties p = new Properties();
            p.load(DecryptionManagerImpl.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DecryptionManagerImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DecryptionManagerImpl.class.getSimpleName() ) ;
        }
    }

    public DecryptionManagerImpl(MachineHandler machineHandler, int numberOfAgents, DecryptionDifficultyLevel difficultyLevel, int taskSize) {
        this.machineHandler = machineHandler;
        if(machineHandler.getInventoryInfo().isPresent()){
            dictionaryManager.setAbc(machineHandler.getInventoryInfo().get().getABC());
        }
        this.numberOfAgents = numberOfAgents;
        this.difficultyLevel= difficultyLevel;
        this.taskSize = taskSize;
        int keepAliveForWhenIdle = 1;
        threadPoolService = new ThreadPoolExecutor(numberOfAgents, numberOfAgents, keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(THREAD_POOL_QEUEU_MAX_CAPACITY));
        isRunningProperty.setValue(false);
    }

    public DecryptionManagerImpl(MachineHandler machineHandler,UIAdapter uiAdapter) {
        this.machineHandler = machineHandler;
        this.uiAdapter = uiAdapter;
        if(machineHandler.getInventoryInfo().isPresent()){
            dictionaryManager.setAbc(machineHandler.getInventoryInfo().get().getABC());
        }
        isRunningProperty.setValue(false);
    }

    public void bruteForceDecryption(String sourceInput) {
        int keepAliveForWhenIdle = 1;
        if(threadPoolService==null){
            threadPoolService = new ThreadPoolExecutor(numberOfAgents, numberOfAgents, keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(THREAD_POOL_QEUEU_MAX_CAPACITY));
        }
        agentWorkManager = new AgentWorkManagerImpl(this.threadPoolService,this.machineHandler,this.difficultyLevel,this.taskSize, sourceInput);
        candidatesListener = new CandidatesListenerImpl(uiAdapter,agentWorkManager.getIsWorkCompletedProperty(),agentWorkManager.getProgressProperty().get().getRight());
//        candidatesListener.BindToWorkers(agentWorkManager.getNumberOfAgentsProperty(), agentWorkManager.getAgentIdToDecryptAgentMap());
        candidatesListener.BindToWorkers(agentWorkManager.getNewestAgentProperty());
        this.workManagerThread = new Thread(agentWorkManager,"agentManagerThread");
        this.candidatesListenerThread = new Thread(candidatesListener, "candidatesListenerThread");
        agentWorkManager.getIsWorkCompletedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == true){
                workManagerThread.interrupt();
            }
        });
        candidatesListener.getIsWorkCompletedProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue == true){
                candidatesListenerThread.interrupt();
            }
        }));
        isRunningProperty.bind(candidatesListener.getIsRunningProperty());
        isRunningProperty.addListener(((observable, oldValue, newValue) -> {
            if(newValue == false){
                this.threadPoolService = null;
            }
        }));
        workManagerThread.start();
        candidatesListenerThread.start();
    }

    public void pauseWork(){
        if(agentWorkManager != null){
            agentWorkManager.pause();
        }
        if(candidatesListener!=null){
            candidatesListener.pause();
        }
    }

    public void resumeWork(){
        if(agentWorkManager!=null){
            agentWorkManager.resume();
        }
        if(candidatesListener!=null){
            candidatesListener.resume();
        }
    }

    @Override
    public void stopWork() {
        if(agentWorkManager!=null){
            agentWorkManager.stop();
        }
        if(candidatesListener!=null){
            candidatesListener.stop();
        }
    }

    @Override
    public void awaitWork() {
        if(workManagerThread!=null){
            try {
                workManagerThread.join();
            } catch (InterruptedException e) {
                log.error("DecryptionManager - failed to await work -" + e.getMessage());
            }
        }
    }


    @Override
    public int getAmountOfTasks() {
        return 0;
    }

}
