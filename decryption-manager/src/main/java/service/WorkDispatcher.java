package service;

import dto.InventoryInfo;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import enums.ReflectorsId;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.*;

public class WorkDispatcher {
    private static final Logger log = Logger.getLogger(WorkDispatcher.class);
    static {
        try {
            Properties p = new Properties();
            p.load(WorkDispatcher.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + WorkDispatcher.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + WorkDispatcher.class.getSimpleName() ) ;
        }
    }

    public static List<MachineState> getWorkBatch(MachineState startingState, DecryptionDifficultyLevel difficultyLevel, int batchSize, InventoryInfo inventoryInfo){
        if(startingState == null){
            log.error("Failed to create a workBatch - startingState = null");
            return null;
        }
        if(difficultyLevel == null){
            log.error("Failed to create a workBatch - difficultyLevel = null");
            return null;
        }
        if(batchSize < 0){
            log.error("Failed to create a workBatch - batchSize cannot be negative, value=" + batchSize);
            return null;
        }
        if(inventoryInfo == null){
            log.error("Failed to create a workBatch - inventoryInfo = null");
            return null;
        }
        List<MachineState> newBatch = null;
        if(difficultyLevel.equals(DecryptionDifficultyLevel.EASY)){
            newBatch = WorkDispatcher.getEasyWorkBatch(startingState,batchSize,inventoryInfo);
        }
        else if(difficultyLevel.equals(DecryptionDifficultyLevel.INTERMEDIATE)){
            newBatch = WorkDispatcher.getIntermediateWorkBatch(startingState,batchSize,inventoryInfo);
        }
        else if(difficultyLevel.equals(DecryptionDifficultyLevel.HARD)){
            newBatch = WorkDispatcher.getHardWorkBatch(startingState,batchSize,inventoryInfo);
        }
        else if(difficultyLevel.equals(DecryptionDifficultyLevel.IMPOSSIBLE)){
            newBatch = WorkDispatcher.getImpossibleWorkBatch(startingState,batchSize,inventoryInfo);
        }
        return newBatch;
    }

    private static List<MachineState> getEasyWorkBatch(MachineState startingState, int batchSize, InventoryInfo inventoryInfo) {
        List<MachineState> newWorkBatch = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            newWorkBatch.add(startingState.getDeepClone());
            startingState.setRotorsHeadsInitialValues(advanceRotorPositions(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC()));
        }
        return newWorkBatch;
    }

    private static List<MachineState> getIntermediateWorkBatch(MachineState startingState, int batchSize, InventoryInfo inventoryInfo) {
        List<MachineState> newWorkBatch = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            newWorkBatch.add(startingState.getDeepClone());
            List<String> advancedPositions = advanceRotorPositions(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC());
            startingState.setRotorsHeadsInitialValues(advancedPositions);
            if(reachedInitialState(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC())){
                ReflectorsId advancedReflector = advanceReflector(startingState.getReflectorId(), inventoryInfo.getNumOfAvailableReflectors());
                startingState.setReflectorId(advancedReflector);
            }
        }
        return newWorkBatch;
    }

    private static List<MachineState> getHardWorkBatch(MachineState startingState, int batchSize, InventoryInfo inventoryInfo) {
        List<MachineState> newWorkBatch = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            newWorkBatch.add(startingState.getDeepClone());
            List<String> advancedPositions = advanceRotorPositions(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC());
            startingState.setRotorsHeadsInitialValues(advancedPositions);
            if(reachedInitialState(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC())){
                ReflectorsId advancedReflector = advanceReflector(startingState.getReflectorId(), inventoryInfo.getNumOfAvailableReflectors());
                startingState.setReflectorId(advancedReflector);
            }
            if(reachedInitialState(startingState.getReflectorId()) && reachedInitialState(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC())){
                List<Integer> advancedRotorIds = advanceRotorsIds(startingState.getRotorIds());
                startingState.setRotorIds(advancedRotorIds);
            }
        }
        return newWorkBatch;
    }

    private static List<MachineState> getImpossibleWorkBatch(MachineState startingState, int batchSize, InventoryInfo inventoryInfo) {
        List<MachineState> newWorkBatch = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            newWorkBatch.add(startingState.getDeepClone());
            List<String> advancedPositions = advanceRotorPositions(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC());
            startingState.setRotorsHeadsInitialValues(advancedPositions);
            if(reachedInitialState(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC())){
                ReflectorsId advancedReflector = advanceReflector(startingState.getReflectorId(), inventoryInfo.getNumOfAvailableReflectors());
                startingState.setReflectorId(advancedReflector);
            }
            if(reachedInitialState(startingState.getReflectorId()) && reachedInitialState(startingState.getRotorsHeadsInitialValues(), inventoryInfo.getABC())){
                List<Integer> advancedRotorIds = advanceRotorsIds(startingState.getRotorIds(),inventoryInfo.getNumOfAvailableRotors());
                startingState.setRotorIds(advancedRotorIds);
            }
        }
        return newWorkBatch;
    }

    private static List<String> advanceRotorPositions(List<String> prevRotorsPositions, String abc){
        if(prevRotorsPositions == null || prevRotorsPositions.isEmpty()){
            log.error("Failed to advcane rotors - starting rotors positions given is null or empty, Value=" + prevRotorsPositions);
            return null;
        }
        if(abc == null || abc.trim().equals("")){
            log.error("Failed to advcane rotors - ABC given is null or empty, Value=" + abc);
            return null;
        }
        long maxValue = (long) Math.pow(prevRotorsPositions.size(),abc.length());
        int numberOfRotorsInUse = prevRotorsPositions.size();
        long positionAsDecimal = MathService.fromLettersToBase10(prevRotorsPositions, abc.length(), abc);
        positionAsDecimal =  (positionAsDecimal + 1) % maxValue;
        List<String> newAdvancedPos = MathService.fromBase10ToBaseN(positionAsDecimal, abc.length(), abc, numberOfRotorsInUse);
        return newAdvancedPos;
    }

    private static ReflectorsId advanceReflector(ReflectorsId previousRefId, int numberOfAvailableReflectors) {
        if(previousRefId == null ){
            log.error("Failed to Advance Reflector's Id - rotor Id is null");
            return null;
        }
        int rotorIdAsNum = previousRefId.getId();
        //Advance:
        rotorIdAsNum = ((rotorIdAsNum + 1) % (numberOfAvailableReflectors+1));
        rotorIdAsNum = (rotorIdAsNum == 0) ? rotorIdAsNum+1 : rotorIdAsNum; //skip id=0
        return ReflectorsId.getByNum(rotorIdAsNum);
    }

    /**
     * FOR IMPOSSIBLE - all rotors ids, all positions
     */
    private static List<Integer> advanceRotorsIds(List<Integer> previousRotorIds, int numberOfAvailableRotors){
        // advancing the rotorIds is the same as advancing rotors positions except we need the values to be different from one another
        // and the abc is numbers
        // i.e. 1,1,1 -> 1,1,2 -> ... -> 1,2,2 -> (1,2,3) -> (1,2,4) -> ...
        // such that the ones in parentheses are the good values
        List<Integer> nextIds;
        List<String> previousRotorsIdsStrings = new ArrayList<>();
        if(previousRotorIds==null || previousRotorIds.isEmpty()){
            log.error("Failed to get rotors ids- given previous rotor ids is null or empty");
            return null;
        }
        //Prepare variables for MathService
        int numberOfRotorsInUse = previousRotorIds.size();
        String abc = "";
        for (int i = 1; i <= numberOfAvailableRotors ; i++) {
            abc = abc + (String.valueOf(i));
        }
        for (Integer id : previousRotorIds ) {
            previousRotorsIdsStrings.add(String.valueOf(id));
        }
        //Actual Work
        Set<String> sizeComparisonSet = new HashSet<>(); //this set allows us to check if all values are different
        List<String> newAdvancedPos = new ArrayList<>(previousRotorsIdsStrings);
        while(newAdvancedPos.size() != sizeComparisonSet.size()){
            int positionAsDecimal = MathService.fromLettersToBase10(newAdvancedPos, abc.length(), abc);
            positionAsDecimal += 1;
            newAdvancedPos = MathService.fromBase10ToBaseN(positionAsDecimal, abc.length(), abc, numberOfRotorsInUse);
            sizeComparisonSet = new HashSet<>(newAdvancedPos);
        }
        //Return to integers
        nextIds = new ArrayList<>();
        for (String idString : newAdvancedPos ) {
            nextIds.add(Integer.valueOf(idString));
        }
        return nextIds;
    }

    /**
     * FOR HARD - only switches the location of the given rotors
     */
    private static List<Integer> advanceRotorsIds(List<Integer> previousRotorIds){
        List<Integer> nextIds;
        List<String> previousRotorsIdsStrings = new ArrayList<>();
        List<Integer> sortedRotorsIds = new ArrayList<>(previousRotorIds);
        sortedRotorsIds.sort(Comparator.naturalOrder());
        if(previousRotorIds==null || previousRotorIds.isEmpty()){
            log.error("Failed to get rotors ids- given previous rotor ids is null or empty");
            return null;
        }
        //Prepare variables for MathService
        int numberOfRotorsInUse = previousRotorIds.size();
        String abc = "";
        for (Integer id : sortedRotorsIds ) {
            abc = abc + (String.valueOf(id));
        }
        for (Integer id : previousRotorIds ) {
            previousRotorsIdsStrings.add(String.valueOf(id));
        }
        //Actual Work
        Set<String> newAdvancedIdsSet = new HashSet<>(); //this set allows us to check if all values are different AND that all the needed rotors exist
        List<String> newAdvancedPos = new ArrayList<>(previousRotorsIdsStrings);
        while(newAdvancedPos.size() != newAdvancedIdsSet.size() || !newAdvancedIdsSet.containsAll(newAdvancedPos)){
            int positionAsDecimal = MathService.fromLettersToBase10(newAdvancedPos, abc.length(), abc);
            positionAsDecimal += 1;
            newAdvancedPos = MathService.fromBase10ToBaseN(positionAsDecimal, abc.length(), abc, numberOfRotorsInUse);
            newAdvancedIdsSet = new HashSet<>(newAdvancedPos);
        }
        //Return to integers
        nextIds = new ArrayList<>();
        for (String idString : newAdvancedPos ) {
            nextIds.add(Integer.valueOf(idString));
        }
        return nextIds;
    }

    private static boolean reachedInitialState(List<String> rotorsPositions, String abc) {
        boolean didReachInitialValue = true;
        String firstChar = abc.substring(0,1);
        for ( String position : rotorsPositions ) {
            didReachInitialValue = didReachInitialValue && position.equals(firstChar);
        }
        return didReachInitialValue;
    }

    private static boolean reachedInitialState(ReflectorsId reflectorId) {
        return reflectorId.getId().equals(ReflectorsId.I.getId());
    }
}
