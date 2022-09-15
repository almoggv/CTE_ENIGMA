package main.java.manager.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.manager.AgentWorkManager;
import main.java.manager.DictionaryManager;
import main.java.service.PausableThreadPoolExecutor;
import main.java.service.XmlFileLoader;
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
    @Getter private int numberOfAgents;
    @Getter @Setter private int taskSize;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel;
    private PausableThreadPoolExecutor threadPoolService;
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty();

    private Thread workManagerThread;
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
        threadPoolService = new PausableThreadPoolExecutor(numberOfAgents, numberOfAgents, keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(THREAD_POOL_QEUEU_MAX_CAPACITY));
        isRunningProperty.setValue(false);
    }

    public void bruteForceDecryption(String sourceInput) {

        AgentWorkManager agentWorkManager = new AgentWorkManagerImpl(this.threadPoolService,this.machineHandler,this.difficultyLevel,this.taskSize, sourceInput);
        this.workManagerThread = new Thread(agentWorkManager,"agentManagerThread");
        //TODO: connect to manager's properties
        workManagerThread.start();
        try{
            workManagerThread.join();
        }
        catch (Exception e){

        }
    }

    public void pauseWork(){
        threadPoolService.pause();
    }

    public void resumeWork(){
        threadPoolService.resume();
    }

}
