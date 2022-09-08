package main.java.manager.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import main.java.agent.DecryptionAgent;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.handler.FileConfigurationHandler;
import main.java.manager.DecryptionManager;
import main.java.manager.DictionaryManager;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class DecryptionManagerImpl implements DecryptionManager {
    private static final Logger log = Logger.getLogger(DecryptionManagerImpl.class);
    private final int THREAD_POOL_QEUEU_MAX_CAPACITY = 100;
    @Getter private MachineHandler machineHandler;
    private DictionaryManager dictionaryManager = new DictionaryManagerImpl();
    @Getter private int numberOfAgents;
    @Getter @Setter private int taskSize;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel;
    private ThreadPoolExecutor  threadPoolService;
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty();

    static {
        try {
            Properties p = new Properties();
            p.load(FileConfigurationHandler.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
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
//        threadPoolService = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfAgents); //Queue max size is not controllable
        int keepAliveForWhenIdle = 1;
        ThreadPoolExecutor ex = new ThreadPoolExecutor(numberOfAgents, numberOfAgents, keepAliveForWhenIdle , TimeUnit.SECONDS, new ArrayBlockingQueue(THREAD_POOL_QEUEU_MAX_CAPACITY));
        isRunningProperty.setValue(false);
    }

    public void bruteForceDecryption(String sourceInput){


        //Cases by level
    }

    private int calcTaskSize(){
        return -1;
    }
}
