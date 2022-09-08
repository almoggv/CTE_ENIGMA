package main.java.manager.impl;

import lombok.Getter;
import lombok.Setter;
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
import java.util.Properties;

public class DecryptionManagerImpl implements DecryptionManager {
    private static final Logger log = Logger.getLogger(DecryptionManagerImpl.class);

    @Getter private MachineHandler machineHandler;
    private DictionaryManager dictionaryManager = new DictionaryManagerImpl();
    @Getter private int numberOfAgents;
    @Getter @Setter private int taskSize;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel;

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
    }

    public void bruteForceDecryption(String sourceInput){
        
    }



}
