package service;

import dto.InventoryInfo;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import enums.ReflectorsId;
import manager.impl.AllyClientDMImpl;
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


    public static List<MachineState> getworkBatch(MachineState startingState, DecryptionDifficultyLevel difficultyLevel, int batchSize){
        List<MachineState> newBatch = null;
        if(difficultyLevel.equals(DecryptionDifficultyLevel.EASY)){
            newBatch = WorkDispatcher.getEasyWorkBatch(startingState,batchSize);
        }
        else if(difficultyLevel.equals(DecryptionDifficultyLevel.INTERMEDIATE)){
            newBatch = WorkDispatcher.getIntermediateWorkBatch(startingState,batchSize);
        }
        else if(difficultyLevel.equals(DecryptionDifficultyLevel.HARD)){
            newBatch = WorkDispatcher.getHardWorkBatch(startingState,batchSize);
        }
        else if(difficultyLevel.equals(DecryptionDifficultyLevel.IMPOSSIBLE)){
            newBatch = WorkDispatcher.getImpossibleWorkBatch(startingState,batchSize);
        }
        return newBatch;
    }

    private static List<MachineState> getEasyWorkBatch(MachineState startingState, int batchSize) {
        List<MachineState> newWorkBatch = new ArrayList<>();
        throw new UnsupportedOperationException();

    }

    private static List<MachineState> getIntermediateWorkBatch(MachineState startingState, int batchSize) {
        throw new UnsupportedOperationException();
    }

    private static List<MachineState> getHardWorkBatch(MachineState startingState, int batchSize) {
        throw new UnsupportedOperationException();
    }

    private static List<MachineState> getImpossibleWorkBatch(MachineState startingState, int batchSize) {
        throw new UnsupportedOperationException();
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
        int numberOfRotorsInUse = prevRotorsPositions.size();
        int positionAsDecimal = MathService.fromLettersToBase10(prevRotorsPositions, abc.length(), abc);
        positionAsDecimal += 1;
        List<String> newAdvancedPos = MathService.fromBase10ToBaseN(positionAsDecimal, abc.length(), abc, numberOfRotorsInUse);
        return newAdvancedPos;
    }

    private static ReflectorsId advanceReflector(ReflectorsId previousRefId) {
        if(previousRefId == null ){
            log.error("Failed to Advance Reflector's Id - rotor Id is null");
            return null;
        }
        int rotorIdAsNum = previousRefId.getId();
        //Advance:
        rotorIdAsNum = (rotorIdAsNum + 1) % ReflectorsId.values().length;
        return ReflectorsId.getByNum(rotorIdAsNum);
    }

    /**
     * IMPOSSIBLE - all rotors ids, all positions
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
     *HARD - only switches the location of the given rotors
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
}
