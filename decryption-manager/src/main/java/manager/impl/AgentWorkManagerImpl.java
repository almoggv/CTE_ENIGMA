package main.java.manager.impl;

import javafx.beans.property.*;
import lombok.Getter;
import main.java.agent.DecryptionAgent;
import main.java.agent.impl.DecryptionAgentImpl;
import main.java.dto.AgentDecryptionInfo;
import main.java.dto.MachineState;
import main.java.enums.ReflectorsId;
import main.java.handler.FileConfigurationHandler;
import main.java.manager.AgentWorkManager;
import main.java.component.MachineHandler;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.service.MathService;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentWorkManagerImpl implements AgentWorkManager {

    private static final Logger log = Logger.getLogger(AgentWorkManagerImpl.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    private final MachineHandler machineHandler;
    private final String inputToDecrypt;
    @Getter private DecryptionDifficultyLevel difficultyLevel = DecryptionDifficultyLevel.EASY;
    private int taskSize;
    private int totalWorkToDo = -1;
    private int amountOfWorkCompleted = 0; //Change to Prorperty or listen to agent
    private String abc;
    private int abcSize;
    private int numberOfRotorsInUse;
    private int numberOfRotorsAvailable;
    private MachineState lastCreatedState;
    @Getter private BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty();
    @Getter private BooleanProperty isAllWorkAssignedProperty = new SimpleBooleanProperty();
    @Getter private ObjectProperty<List<AgentDecryptionInfo>> decryptionCandidatesProperty = new SimpleObjectProperty<>();

    static {
        try {
            Properties p = new Properties();
            p.load(FileConfigurationHandler.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AgentWorkManagerImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AgentWorkManagerImpl.class.getSimpleName() ) ;
        }
    }

    public AgentWorkManagerImpl(ThreadPoolExecutor threadPoolExecutor, MachineHandler machineHandler
            , DecryptionDifficultyLevel difficultyLevel, int taskSize, String inputToDecrypt) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.machineHandler = machineHandler;
        this.difficultyLevel = difficultyLevel;
        this.taskSize = taskSize;
        this.inputToDecrypt = inputToDecrypt;
        this.abc = machineHandler.getInventoryInfo().get().getABC();
        this.abcSize = machineHandler.getInventoryInfo().get().getABC().length();
        this.numberOfRotorsInUse = machineHandler.getInventoryInfo().get().getNumOfRotorsInUse();
        this.numberOfRotorsAvailable = machineHandler.getInventoryInfo().get().getNumOfAvailableRotors();
        this.lastCreatedState = machineHandler.getInitialMachineState().get();
        isWorkCompletedProperty.setValue(false);
        isAllWorkAssignedProperty.setValue(false);
        decryptionCandidatesProperty.setValue(new ArrayList<>());

        //Calculate at the end
        totalWorkToDo = calcTotalWorkToDoByDifficulty();
    }

    private int calcTotalWorkToDoByDifficulty() {
        int result = 0;
        switch(difficultyLevel) {
            case INTERMEDIATE:
                result = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * machineHandler.getInventoryInfo().get().getNumOfAvailableReflectors();
                break;
            case HARD:
                result = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * machineHandler.getInventoryInfo().get().getNumOfAvailableReflectors() * MathService.factorial(numberOfRotorsInUse);
                break;
            case IMPOSSIBLE:
                result = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * machineHandler.getInventoryInfo().get().getNumOfAvailableReflectors() *  MathService.factorial(numberOfRotorsInUse) * MathService.nChooseK(numberOfRotorsInUse,numberOfRotorsAvailable);
                break;
            case EASY:
            default:
                result = (int) Math.pow(abcSize,numberOfRotorsInUse);
        }
        return result;
    }

    private List<String> createRotorInitialStartingPosition(){
        //Not Implemented
        return Arrays.asList("A", "A","A");
    }

    private List<Integer> createRotorIDsPlacement(){
        //Use the same rotor ids as in the machineState
        // just adjust their location
        return Arrays.asList(1, 2, 3);
    }

    private List<Integer> selectRotorIDs(){
        //used for Impossible - tachles - we always start from rotors 1,2,3
        return Arrays.asList(1, 2, 3);
    }

    private void initWorkBatchesByDifficulty() {
        List<String> initialPos = createRotorInitialStartingPosition();
        List<Integer> rotorsPlacement;
        ReflectorsId reflectorsId = ReflectorsId.I;
        switch(difficultyLevel) {
            case INTERMEDIATE:
                lastCreatedState.setReflectorId(reflectorsId);
                lastCreatedState.setRotorsHeadsInitialValues(initialPos);
                break;
            case HARD:
                rotorsPlacement = createRotorIDsPlacement();
                lastCreatedState.setRotorIds(rotorsPlacement);
                lastCreatedState.setReflectorId(reflectorsId);
                lastCreatedState.setRotorsHeadsInitialValues(initialPos);
                break;
            case IMPOSSIBLE:
                rotorsPlacement = selectRotorIDs();
                lastCreatedState.setRotorIds(rotorsPlacement);
                lastCreatedState.setReflectorId(reflectorsId);
                lastCreatedState.setRotorsHeadsInitialValues(initialPos);
                break;
            case EASY:
            default:
                lastCreatedState.setRotorsHeadsInitialValues(initialPos);
        }
    }

    private List<MachineState> getNextWorkBatch(){
        switch(difficultyLevel) {
            case INTERMEDIATE:
                return getNextIntermediateWorkBatch();
//                break;
            case HARD:
                return getNextHardWorkBatch();
//                break;
            case IMPOSSIBLE:
                return getNextImpossibleWorkBatch();
//                break;
            case EASY:
            default:
                return getNextEasyWorkBatch();
        }
    }

    private List<MachineState> getNextImpossibleWorkBatch() {
        throw new NotImplementedException();
    }

    private List<MachineState> getNextHardWorkBatch() {
        throw new NotImplementedException();
    }

    private List<MachineState> getNextIntermediateWorkBatch() {
        throw new NotImplementedException();
    }

    private List<MachineState> getNextEasyWorkBatch() {
        List<MachineState> newWorkBatch = new ArrayList<>();
        int currWorkDispatched = fromLettersToBase10(lastCreatedState.getRotorsHeadsInitialValues(),abcSize);
        int maxWorkToDispatch = totalWorkToDo;
        int currTaskSize = Math.min((maxWorkToDispatch - currWorkDispatched), taskSize);
        isAllWorkAssignedProperty.setValue(currWorkDispatched == maxWorkToDispatch);
        for (int i = 0; i < currTaskSize; i++) {
            MachineState newWorkState = lastCreatedState.getDeepClone();
            newWorkBatch.add(newWorkState);
            lastCreatedState.setRotorsHeadsInitialValues(advanceRotorPositions(lastCreatedState.getRotorsHeadsInitialValues()));
        }
        return newWorkBatch;
    }

    private List<String> advanceRotorPositions(List<String> startingPos){
        int positionAsDecimal = fromLettersToBase10(startingPos,abcSize);
        positionAsDecimal += 1;
        List<String> newAdvancedPos = fromBase10ToBaseN(positionAsDecimal, abcSize);
        return newAdvancedPos;
    }

    private int fromLettersToBase10(List<String> numberInBaseN, int base){
        int result = 0;
        for (int i = numberInBaseN.size() - 1, pow = 0 ; i >= 0 ; i--, pow++) {
            int charAsBase10 = abc.indexOf(numberInBaseN.get(i));
            result += charAsBase10 * (Math.pow(base,pow));
        }
        return (int) result;
    }

    // From number to letters i.e. 29 -> ABD
    private List<String> fromBase10ToBaseN(int decimalNumber, int baseN){
        LinkedList<String> result = new LinkedList<>();
        for (int i = 0; i < numberOfRotorsInUse; i++) {
            int currDigit = decimalNumber % baseN;
            decimalNumber = decimalNumber / baseN;
            result.push(abc.substring(currDigit,currDigit+1));
        }
        return result;
    }

    private DecryptionAgent getNextAgent(){
        List<MachineState> workForAgent = getNextWorkBatch();
        DecryptionAgent newAgent = new DecryptionAgentImpl(machineHandler.getEncryptionMachineClone());
        newAgent.assignWork(workForAgent,this.inputToDecrypt);

        newAgent.getPotentialCandidatesListProperty().addListener(((observable, oldValue, newValue) -> {
            log.debug("AgentWorkManager - received potential");
            List<AgentDecryptionInfo> currCandidates = new ArrayList<>(this.decryptionCandidatesProperty.get());
            currCandidates.addAll(newValue);
            this.decryptionCandidatesProperty.setValue(currCandidates);
        }));
        newAgent.getProgressProperty().addListener(((observable, oldValue, newValue) -> {
                int delta = newValue.getLeft() - oldValue.getLeft();
                synchronized (this){
                    this.amountOfWorkCompleted += delta;
                }
                if(this.amountOfWorkCompleted >= this.totalWorkToDo){
                    this.isWorkCompletedProperty.setValue(true);
                }
        }));
        return newAgent;
    }


    @Override
    public void run() {
        this.initWorkBatchesByDifficulty();
        while(!isAllWorkAssignedProperty.get()){
            if(threadPoolExecutor.getQueue().remainingCapacity() > 0){
                log.debug("AgentWorkManager - threadPoolExecutor remainingCapacity of Queue = " + threadPoolExecutor.getQueue().remainingCapacity());
                threadPoolExecutor.submit(getNextAgent());
            }
        }
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("AgentWorkManager - awaited termination caught an InterruptedException = " + e.getMessage());
        }

    }
}





//        agentFactory.build(INTERMEDIATE);
//        agentFactory.deployAgents();
//        //Cases by level
//
//        while(workNotFinished){
//            if(queue != full){
//                agent = getnextAgent();
//                pool.sumbit(agent)
//            }
//        }
