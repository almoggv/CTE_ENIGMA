package manager.impl;

import dto.InventoryInfo;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import lombok.Getter;
import lombok.Setter;
import manager.AllyClientDM;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import service.WorkDispatcher;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

public class AllyClientDMImpl implements AllyClientDM {
    private static final Logger log = Logger.getLogger(AllyClientDMImpl.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllyClientDMImpl.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllyClientDMImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllyClientDMImpl.class.getSimpleName() ) ;
        }
    }

    private Queue<List<MachineState>> workBatchesQueue = new LinkedList<List<MachineState>>();
    @Getter @Setter private InventoryInfo inventoryInfo;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel = null;
    @Getter private int taskSize = -1;
    @Getter @Setter private MachineState initialMachineConfig = null;
    private MachineState lastCreatedworkBatchLastState = null;

    private boolean isKilled = false;

    public AllyClientDMImpl(DecryptionDifficultyLevel difficultyLevel, int taskSize, InventoryInfo inventoryInfo) {
        this.difficultyLevel = difficultyLevel;
        this.taskSize = taskSize;
        this.inventoryInfo = inventoryInfo;
    }

    public AllyClientDMImpl() {}

    @Override
    public void run() {
        log.info("Started Running");
        while(!this.isKilled){
            if(!isDmConfiguredYet()){
                continue;
            }
            fillQueueWithWork();
        }
    }

    @Override
    public synchronized List<MachineState> getNextBatch() {
        return workBatchesQueue.poll();
    }

    @Override
    public void setTaskSize(int taskSize) {
        if(taskSize<=0){
            log.error("Failed to set taskSize, given taskSize Value is negative value=" + taskSize);
            throw new IllegalArgumentException("taskSize needs to be positive");
        }
        this.taskSize = taskSize;
    }

    public synchronized void kill(){
        isKilled = true;
    }

    private void fillQueueWithWork(){
        if(workBatchesQueue.size() >= PropertiesService.getMaxWorkBatchesQueueSize()){
            return;
        }
        MachineState lastUsedState = (this.lastCreatedworkBatchLastState == null) ? initialMachineConfig : this.lastCreatedworkBatchLastState;
        int amountOfBatchesToCreate = PropertiesService.getMaxWorkBatchesQueueSize() - workBatchesQueue.size();
        for (int i = 0; i < amountOfBatchesToCreate; i++) {
            List<MachineState> newBatch = WorkDispatcher.getWorkBatch(lastUsedState,difficultyLevel,taskSize);
            workBatchesQueue.add(newBatch);
            lastUsedState = newBatch.get(newBatch.size() - 1);
        }
    }

    private boolean isDmConfiguredYet() {
        if(this.difficultyLevel == null){
            return false;
        }
        if(this.taskSize <= 0){
            return false;
        }
        if(initialMachineConfig == null){
            return false;
        }
        if(inventoryInfo == null){
            return false;
        }
        return true;
    }
}
