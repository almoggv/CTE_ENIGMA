package manager.impl;

import dto.InventoryInfo;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import enums.ReflectorsId;
import generictype.MappingPair;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import manager.AllyClientDM;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.MathService;
import service.PropertiesService;
import service.WorkDispatcher;

import java.io.IOException;
import java.util.*;

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
    @Getter private InventoryInfo inventoryInfo;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel = null;
    @Getter private int taskSize = -1;
    @Getter @Setter private MachineState initialMachineConfig = null;
    private MachineState lastCreatedworkBatchLastState = null;
    @Getter ObjectProperty<MappingPair<Long,Long>> progressProperty = new SimpleObjectProperty<>();

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
            if(progressProperty.get() == null || progressProperty.get().getRight() <= 0){
                progressProperty.setValue(new MappingPair<>(0L,calculateAmountOfWork()));
            }
            fillQueueWithWork();
        }
    }

    @Override
    public synchronized List<MachineState> getNextBatch() {
        if(workBatchesQueue.isEmpty()){
            return null;
        }
        List<MachineState> nextBatch = workBatchesQueue.poll();
        //update progress
        MappingPair<Long,Long> newProgress = new MappingPair<>(progressProperty.get().getLeft(),progressProperty.get().getRight());
        newProgress.setLeft(newProgress.getLeft() + nextBatch.size());
        progressProperty.setValue(newProgress);
        return nextBatch;
    }

    @Override
    public synchronized List<List<MachineState>> getNextBatch(int numberOfBatches) {
        List<List<MachineState>> resultBatches = new ArrayList<>(numberOfBatches);
        for (int i = 0; i < numberOfBatches; i++) {
            resultBatches.add(getNextBatch());
        }
        return resultBatches;
    }

    @Override
    public void setInventoryInfo(InventoryInfo inventoryInfo) {
        this.inventoryInfo = inventoryInfo;
        initialMachineConfig = createInitialMachineConfig(inventoryInfo);
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

    @Override
    public void clear() {
        workBatchesQueue.clear();
        progressProperty.setValue(null);
        difficultyLevel = null;
        initialMachineConfig = null;
        lastCreatedworkBatchLastState= null;
        inventoryInfo = null;
        log.debug("Was cleared");
    }

    private void fillQueueWithWork(){
        if(workBatchesQueue.isEmpty()){
            log.info("Beginning to fill Queue - progress="+ progressProperty.get());
        }
        if(workBatchesQueue.size() >= PropertiesService.getMaxWorkBatchesQueueSize()){
//            log.info("Queue is full of work, size=" + workBatchesQueue.size() + "/" + PropertiesService.getMaxWorkBatchesQueueSize());
            return;
        }
        if(progressProperty.get().getLeft() >= progressProperty.get().getRight()){
            return;
        }
        MachineState lastUsedState = (this.lastCreatedworkBatchLastState == null) ? initialMachineConfig : this.lastCreatedworkBatchLastState;
        int amountOfBatchesToCreate = PropertiesService.getMaxWorkBatchesQueueSize() - workBatchesQueue.size();
        for (int i = 0; i < amountOfBatchesToCreate; i++) {
            List<MachineState> newBatch = WorkDispatcher.getWorkBatch(lastUsedState,difficultyLevel,taskSize,inventoryInfo);
            workBatchesQueue.add(newBatch);
            lastUsedState = newBatch.get(newBatch.size() - 1);
        }
        this.lastCreatedworkBatchLastState = lastUsedState;
    }

    private MachineState createInitialMachineConfig(InventoryInfo inventoryInfo) {
        MachineState firstState = new MachineState();
        String firstRotorPos = inventoryInfo.getABC().substring(0,1);
        List<Integer> initialRotorsIds = new ArrayList<>();
        List<String> initialRotorsPositions = new ArrayList<>();
        for (int i = 1; i <= inventoryInfo.getNumOfRotorsInUse(); i++) {
            initialRotorsIds.add(i);
            initialRotorsPositions.add(firstRotorPos);
        }
        firstState.setRotorIds(initialRotorsIds);
        firstState.setReflectorId(ReflectorsId.I);
        firstState.setRotorsHeadsInitialValues(initialRotorsPositions);
        firstState.setPlugMapping(new ArrayList<>());
        firstState.setNotchDistancesFromHead(new ArrayList<>());
        return firstState;
    }

    private boolean isDmConfiguredYet() {
        if(this.difficultyLevel == null){
            log.info("DM is not configured yet - missing difficultyLevel");
            return false;
        }
        if(this.taskSize <= 0){
            log.info("DM is not configured yet - illegal taskSize");
            return false;
        }
        if(initialMachineConfig == null){
            log.info("DM is not configured yet - missing machine config");
            return false;
        }
        if(inventoryInfo == null){
            log.info("DM is not configured yet - missing inventoryInfo");
            return false;
        }
        return true;
    }

    private long calculateAmountOfWork(){
        if(!isDmConfiguredYet()){
            return -1;
        }
        long result = 0;
        int abcSize = inventoryInfo.getABC().length();
        int numberOfRotorsInUse = inventoryInfo.getNumOfRotorsInUse();
        int numberOfReflectorsAvailable = inventoryInfo.getNumOfAvailableReflectors();
        int numberOfRotorsAvailable = inventoryInfo.getNumOfAvailableRotors();
        switch(difficultyLevel) {
            case INTERMEDIATE:
                result = ((int) Math.pow(abcSize, numberOfRotorsInUse)) * numberOfReflectorsAvailable;
                break;
            case HARD:
                result = ((int) Math.pow(abcSize, numberOfRotorsInUse)) * numberOfReflectorsAvailable * MathService.factorial(numberOfRotorsInUse);
                break;
            case IMPOSSIBLE:
                result = ((int) Math.pow(abcSize, numberOfRotorsInUse)) * numberOfReflectorsAvailable * MathService.factorial(numberOfRotorsInUse) * MathService.nChooseKSize(numberOfRotorsInUse, numberOfRotorsAvailable);
                break;
            case EASY:
            default:
                result = (int) Math.pow(abcSize, numberOfRotorsInUse);
        }
        return result;
    }
}
