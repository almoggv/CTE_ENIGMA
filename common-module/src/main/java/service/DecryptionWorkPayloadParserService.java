package service;

import dto.DecryptionWorkPayload;
import dto.InventoryInfo;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DecryptionWorkPayloadParserService {
    private static final Logger log = Logger.getLogger(DecryptionWorkPayloadParserService.class);
    static {
        try {
            Properties p = new Properties();
            p.load(DecryptionWorkPayloadParserService.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DecryptionWorkPayloadParserService.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DecryptionWorkPayloadParserService.class.getSimpleName() ) ;
        }
    }

    /**
     * @return if one of the variables received isn't valid, returns null
     */
    public static List<MachineState> unzip(DecryptionWorkPayload payload, InventoryInfo inventoryInfo){
        if(payload == null){
            log.error("Failed to unzip payload - payload=null");
            return null;
        }
        if(payload.getFirstState() == null){
            log.error("Failed to unzip payload - missing first state=null");
            return null;
        }
        if(payload.getDifficultyLevel() == null){
            log.error("Failed to unzip payload - DifficultyLevel=null");
            return null;
        }
        List<MachineState> unzippedWork = new ArrayList<>();
        unzippedWork = WorkDispatcher.getWorkBatch(payload.getFirstState(),payload.getDifficultyLevel(),payload.getAmountOfStates(),inventoryInfo);
        return unzippedWork;
    }

    /**
     * @return if one of the variables received isn't valid, returns null
     */
    public static DecryptionWorkPayload zip(List<MachineState> workToDo, String inputToDecrypt, DecryptionDifficultyLevel difficultyLevel){
        if(workToDo == null || workToDo.isEmpty()){
            log.error("Failed to zip workToDo - workToDo is null or empty, Value=" + workToDo);
            return null;
        }
        DecryptionWorkPayload zippedWork = new DecryptionWorkPayload();
        zippedWork.setDifficultyLevel(difficultyLevel);
        zippedWork.setAmountOfStates(workToDo.size());
        zippedWork.setFirstState(workToDo.get(0));
        zippedWork.setLastState(workToDo.get(workToDo.size()-1));
        zippedWork.setInputToDecrypt(inputToDecrypt);
        return zippedWork;
    }

}
