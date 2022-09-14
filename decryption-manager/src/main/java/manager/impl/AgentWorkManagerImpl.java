package main.java.manager.impl;

import javafx.beans.property.*;
import lombok.Getter;
import main.java.agent.DecryptionAgent;
import main.java.agent.impl.DecryptionAgentImpl;
import main.java.component.EncryptionMachine;
import main.java.dto.AgentDecryptionInfo;
import main.java.dto.MachineState;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import main.java.service.XmlFileLoader;
import main.java.manager.AgentWorkManager;
import main.java.component.MachineHandler;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.service.MathService;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentWorkManagerImpl implements AgentWorkManager {
    private static final Logger log = Logger.getLogger(AgentWorkManagerImpl.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    private final MachineHandler machineHandler;
    private final String inputToDecrypt;
    private final int numberOfReflectorsAvailable;
    private final List<String> lastPossibleInitialPos;
    private final DecryptionDifficultyLevel difficultyLevel;
    private final List<List<Integer>> possibleRotorIdsCombinationList;
    private int taskSize;
    private int totalWorkToDo = -1;
    private int amountOfWorkCompleted = 0; //Change to Prorperty or listen to agent
    private String abc;
    private int abcSize;
    private int numberOfRotorsInUse;
    private int numberOfRotorsAvailable;

    private int numOfPossibleInitialPos;
    List<List<Integer>> possibleRotorIdsList;
    private MachineState lastCreatedState;
    @Getter private BooleanProperty isWorkCompletedProperty = new SimpleBooleanProperty();
    @Getter private BooleanProperty isAllWorkAssignedProperty = new SimpleBooleanProperty();
   /*
   assignedWorkProgressProperty references the assigned work to the agents - and not the amount actually completed by the agents;
    */
    @Getter private ObjectProperty<MappingPair<Integer,Integer>> assignedWorkProgressProperty = new SimpleObjectProperty<>();
    @Getter private ObjectProperty<List<AgentDecryptionInfo>> decryptionCandidatesProperty = new SimpleObjectProperty<>();

    @Getter List<DecryptionAgent> decryptionAgentsList = new ArrayList();
    @Getter private IntegerProperty numberOfAgentsProperty = new SimpleIntegerProperty();

    static {
        try {
            Properties p = new Properties();
            p.load(XmlFileLoader.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
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
        this.numberOfReflectorsAvailable = machineHandler.getInventoryInfo().get().getNumOfAvailableReflectors();
        this.lastCreatedState = machineHandler.getInitialMachineState().get();
        isWorkCompletedProperty.setValue(false);
        isAllWorkAssignedProperty.setValue(false);
        decryptionCandidatesProperty.setValue(new ArrayList<>());

        //Calculate at the end
        totalWorkToDo = calcTotalWorkToDoByDifficulty();
        assignedWorkProgressProperty.set(new MappingPair<>(0, totalWorkToDo));
        this.possibleRotorIdsList = createIntermediateRotorIDsPlacementList(machineHandler.getMachineState().get().getRotorIds());
        this.lastPossibleInitialPos = createRotorLastStartingPosition();
        this.possibleRotorIdsCombinationList = createImpossibleRotorIdCombinationOptions();
    }

    private List<List<Integer>> createIntermediateRotorIDsPlacementList(List<Integer> rotorIds) {
//        List<Integer> rotorsInUse = machineHandler.getMachineState().get().getRotorIds();
        List<List<Integer>> indexesPermutationsList = MathService.createPermutationList(rotorIds.size());

        List<List<Integer>> allRotorIdsPlacement = new ArrayList<>();

        for (List<Integer> indexList : indexesPermutationsList) {
            List<Integer> singleRotorIdList = new ArrayList<>();
            for (Integer index : indexList) {
                singleRotorIdList.add(rotorIds.get(index));
            }
            allRotorIdsPlacement.add(singleRotorIdList);
        }
        return allRotorIdsPlacement;
    }

    private List<List<Integer>> createImpossibleRotorIdCombinationOptions(){
        List<int[]> indexesComboList = MathService.nChooseKOptions(this.numberOfRotorsAvailable, this.numberOfRotorsInUse);
        List<List<Integer>> allRotorIdsPlacement = new ArrayList<>();

        for (int[] indexList : indexesComboList) {
            List<Integer> singleRotorIdList = new ArrayList<>();
            for (Integer index : indexList) {
                singleRotorIdList.add(index+1);
            }
            allRotorIdsPlacement.add(singleRotorIdList);
        }
        return allRotorIdsPlacement;
    }

    private int calcTotalWorkToDoByDifficulty() {
        int result = 0;
        switch(difficultyLevel) {
            case INTERMEDIATE:
                result = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * numberOfReflectorsAvailable;
                break;
            case HARD:
                result = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * numberOfReflectorsAvailable * MathService.factorial(numberOfRotorsInUse);
                break;
            case IMPOSSIBLE:
                //todo- check
                result = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * numberOfReflectorsAvailable *  MathService.factorial(numberOfRotorsInUse) * MathService.nChooseKSize(numberOfRotorsInUse,numberOfRotorsAvailable);
                break;
            case EASY:
            default:
                result = (int) Math.pow(abcSize,numberOfRotorsInUse);
        }
        return result;
    }

    private List<String> createRotorInitialStartingPosition(){
        List<String> startingPos = new ArrayList<>();
        for (int i = 0; i < numberOfRotorsInUse; i++) {
            startingPos.add(abc.substring(0,1));
        }
        return startingPos;
    }

    private List<String> createRotorLastStartingPosition(){
        List<String> startingPos = new ArrayList<>();
        for (int i = 0; i < numberOfRotorsInUse; i++) {
            startingPos.add(abc.substring(abcSize-1,abcSize));
        }
        return startingPos;
    }

    private List<Integer> createStartingRotorIDsPlacement(){
        return possibleRotorIdsList.get(0);
    }

    private List<Integer> selectRotorIdsStartImpossible(){
        return possibleRotorIdsCombinationList.get(0);
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
                rotorsPlacement = createStartingRotorIDsPlacement();
                lastCreatedState.setRotorIds(rotorsPlacement);
                lastCreatedState.setReflectorId(reflectorsId);
                lastCreatedState.setRotorsHeadsInitialValues(initialPos);
                break;
            case IMPOSSIBLE:
                this.possibleRotorIdsList = createIntermediateRotorIDsPlacementList(selectRotorIdsStartImpossible());
                rotorsPlacement = createStartingRotorIDsPlacement();
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
            case HARD:
                return getNextHardWorkBatch();
            case IMPOSSIBLE:
                return getNextImpossibleWorkBatch();
            case EASY:
            default:
                return getNextEasyWorkBatch();
        }
    }

    private List<MachineState> getNextImpossibleWorkBatch() {
        List<MachineState> newWorkBatch = new ArrayList<>();
        List<Integer> lastPossibleRotorIdsCombination = possibleRotorIdsCombinationList.get(possibleRotorIdsCombinationList.size()-1);
        int currWorkDispatched = assignedWorkProgressProperty.get().getLeft();
        int maxWorkToDispatch = assignedWorkProgressProperty.get().getRight();
        int currTaskSize = Math.min((maxWorkToDispatch - currWorkDispatched), taskSize);
        List<Integer> lastPermutationOfIds = possibleRotorIdsList.get(possibleRotorIdsList.size() - 1);
        int amountOfWorkHard = ((int) Math.pow(abcSize,numberOfRotorsInUse)) * numberOfReflectorsAvailable * MathService.factorial(numberOfRotorsInUse);

        for (int i = 0; i < currTaskSize; i++) {
            MachineState newWorkState = lastCreatedState.getDeepClone();
            newWorkBatch.add(newWorkState);
            if (lastCreatedState.getRotorsHeadsInitialValues().equals(lastPossibleInitialPos)
                    && currWorkDispatched !=maxWorkToDispatch - currTaskSize){

                if(lastCreatedState.getRotorIds().equals(possibleRotorIdsList.get(possibleRotorIdsList.size()-1))
                        && lastCreatedState.getReflectorId().equals(ReflectorsId.getByNum(numberOfReflectorsAvailable)))
                {
                    lastCreatedState.setRotorIds(changeRotorIds(lastCreatedState.getRotorIds()));
                    lastCreatedState.setReflectorId(ReflectorsId.I);
                }
                else {
                    if(lastCreatedState.getReflectorId().equals(ReflectorsId.getByNum(numberOfReflectorsAvailable)))
                    {
                        lastCreatedState.setRotorIds(advanceRotorIds(lastCreatedState.getRotorIds()));
                        lastCreatedState.setReflectorId(ReflectorsId.I);
                    }
                    else{
                        lastCreatedState.setReflectorId(advanceReflector());
                    }
                }
            }
            lastCreatedState.setRotorsHeadsInitialValues(advanceRotorPositions(lastCreatedState.getRotorsHeadsInitialValues()));
        }
        currWorkDispatched += currTaskSize;
        System.out.println(currWorkDispatched);
        assignedWorkProgressProperty.set(new MappingPair<>(currWorkDispatched,maxWorkToDispatch));
        isAllWorkAssignedProperty.setValue(currWorkDispatched == maxWorkToDispatch);
        if(isAllWorkAssignedProperty.get()){
            System.out.println("all work assigned: " + currWorkDispatched + " / " +maxWorkToDispatch);
        }
        for (int i = 0; i < currTaskSize; i++) {
            System.out.println(newWorkBatch.get(i));
        }
        return newWorkBatch;
    }

    private List<Integer> changeRotorIds(List<Integer> rotorIds) {
        List<Integer> nextIds;
        /*List<Integer> sortedRotorIds = */rotorIds.sort(Comparator.naturalOrder());
        if(!rotorIds.equals(possibleRotorIdsCombinationList.get(possibleRotorIdsCombinationList.size()-1).stream().sorted())){
            nextIds = possibleRotorIdsCombinationList.get(possibleRotorIdsCombinationList.indexOf(rotorIds) + 1);
            this.possibleRotorIdsList = createIntermediateRotorIDsPlacementList(nextIds);
            return nextIds;
        }
        //todo - change to optional? deal with diff
        return null;
    }

    private List<MachineState> getNextHardWorkBatch() {
        List<MachineState> newWorkBatch = new ArrayList<>();

        int currWorkDispatched = assignedWorkProgressProperty.get().getLeft();
        int maxWorkToDispatch = assignedWorkProgressProperty.get().getRight();
        int currTaskSize = Math.min((maxWorkToDispatch - currWorkDispatched), taskSize);
        List<Integer> lastPermutationOfIds = possibleRotorIdsList.get(possibleRotorIdsList.size() - 1);
        for (int i = 0; i < currTaskSize; i++) {
            MachineState newWorkState = lastCreatedState.getDeepClone();
            newWorkBatch.add(newWorkState);
            //if zz - advance ref
            if (lastCreatedState.getRotorsHeadsInitialValues().equals(lastPossibleInitialPos)){
                //if last reflector - advance rotor, reset reflectorsId
                if(lastCreatedState.getReflectorId().equals(ReflectorsId.getByNum(numberOfReflectorsAvailable)))
                {
                    lastCreatedState.setRotorIds(advanceRotorIds(lastCreatedState.getRotorIds()));
                    lastCreatedState.setReflectorId(ReflectorsId.I);
                }
                else{
                    lastCreatedState.setReflectorId(advanceReflector());
                }
            }
                lastCreatedState.setRotorsHeadsInitialValues(advanceRotorPositions(lastCreatedState.getRotorsHeadsInitialValues()));
//        System.out.println(lastCreatedState);
        }
        currWorkDispatched += currTaskSize;
//        System.out.println(currWorkDispatched);
        assignedWorkProgressProperty.set(new MappingPair<>(currWorkDispatched,maxWorkToDispatch));
        isAllWorkAssignedProperty.setValue(currWorkDispatched == maxWorkToDispatch);
        if(isAllWorkAssignedProperty.get()){
            System.out.println("all work assigned: " + currWorkDispatched + " / " +maxWorkToDispatch);
        }
        return newWorkBatch;
    }

    private List<Integer> advanceRotorIds(List<Integer> ids) {
        List<Integer> nextIds;
        if(!ids.equals(possibleRotorIdsList.get(possibleRotorIdsList.size()-1))){
            nextIds = possibleRotorIdsList.get(possibleRotorIdsList.indexOf(ids) + 1);
            return nextIds;
        }
        //todo - change to optional? deal with diff
        return null;
    }

    private List<MachineState> getNextIntermediateWorkBatch() {
        List<MachineState> newWorkBatch = new ArrayList<>();

        int currWorkDispatched = assignedWorkProgressProperty.get().getLeft();
        int maxWorkToDispatch = assignedWorkProgressProperty.get().getRight();
        int currTaskSize = Math.min((maxWorkToDispatch - currWorkDispatched), taskSize);

        for (int i = 0; i < currTaskSize; i++) {
            MachineState newWorkState = lastCreatedState.getDeepClone();
            newWorkBatch.add(newWorkState);

            if (lastCreatedState.getRotorsHeadsInitialValues().equals(lastPossibleInitialPos)){
                lastCreatedState.setReflectorId(advanceReflector());
            }
            lastCreatedState.setRotorsHeadsInitialValues(advanceRotorPositions(lastCreatedState.getRotorsHeadsInitialValues()));
        }
        currWorkDispatched += currTaskSize;
        assignedWorkProgressProperty.set(new MappingPair<>(currWorkDispatched,maxWorkToDispatch));
        isAllWorkAssignedProperty.setValue(currWorkDispatched == maxWorkToDispatch);
        return newWorkBatch;
    }

    private ReflectorsId advanceReflector() {
        MachineState machineState = lastCreatedState.getDeepClone();
        ReflectorsId lastRefId = machineState.getReflectorId();
        if(lastRefId != ReflectorsId.getByNum(numberOfReflectorsAvailable)){
            return ReflectorsId.getByNum(lastRefId.getId() + 1);
        }
        //todo - change to optional? deal with diff
        return null;
    }

    private List<MachineState>  getNextEasyWorkBatch() {
        List<MachineState> newWorkBatch = new ArrayList<>();

        int currWorkDispatched = assignedWorkProgressProperty.get().getLeft();
        int maxWorkToDispatch = assignedWorkProgressProperty.get().getRight();
        int currTaskSize = Math.min((maxWorkToDispatch - currWorkDispatched), taskSize);

        for (int i = 0; i < currTaskSize; i++) {
            MachineState newWorkState = lastCreatedState.getDeepClone();
            newWorkBatch.add(newWorkState);

            lastCreatedState.setRotorsHeadsInitialValues(advanceRotorPositions(lastCreatedState.getRotorsHeadsInitialValues()));
        }
        currWorkDispatched += currTaskSize;
        assignedWorkProgressProperty.set(new MappingPair<>(currWorkDispatched,maxWorkToDispatch));
        isAllWorkAssignedProperty.setValue(currWorkDispatched == maxWorkToDispatch);
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
        EncryptionMachine handler = machineHandler.getEncryptionMachineClone();
        DecryptionAgent newAgent = new DecryptionAgentImpl(handler);
        newAgent.assignWork(workForAgent,this.inputToDecrypt);
        log.debug("Agent manager: assigned work to agent");

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
                    System.out.println("Agent completed work: "+ newAgent.getId());
                }
        }));
        return newAgent;
    }

    @Override
    public void run() {
        System.out.println("IN agentManager run!");
        this.initWorkBatchesByDifficulty();
        int i = 0;
        while(!isAllWorkAssignedProperty.get()){
            if(threadPoolExecutor.getQueue().remainingCapacity() > 0){
                log.debug("AgentWorkManager - threadPoolExecutor remainingCapacity of Queue = " + threadPoolExecutor.getQueue().remainingCapacity());
                DecryptionAgent agent = getNextAgent();
                decryptionAgentsList.add(agent);
                numberOfAgentsProperty.setValue(decryptionAgentsList.size());
                threadPoolExecutor.execute(agent);
//                log.debug(" Added agent to thread pool: "+ agent.getId());
//                System.out.println(++i + "Added agent to thread pool: "+ agent.getId());
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




