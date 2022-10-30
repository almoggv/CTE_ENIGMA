package agent.impl;

import agent.DecryptionWorker;
import component.EncryptionMachine;
import dto.DecryptionCandidateInfo;
import dto.MachineState;
import generictype.MappingPair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import manager.DictionaryManagerStatic;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DecryptionWorkerImpl implements DecryptionWorker {
    private static final Logger log = Logger.getLogger(DecryptionWorkerImpl.class);
    static {
        try {
            Properties p = new Properties();
            p.load(DecryptionWorkerImpl.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DecryptionWorkerImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DecryptionWorkerImpl.class.getSimpleName() ) ;
        }
    }

    @Getter private final UUID id = UUID.randomUUID();
    private final EncryptionMachine encryptionMachine;
    private List<MachineState> workToDo;
    private String inputToDecrypt;

    @Getter private final ObjectProperty<DecryptionCandidateInfo> lastFoundCandidateProperty = new SimpleObjectProperty<>();
    @Getter private final ObjectProperty<List<DecryptionCandidateInfo>> allFoundDecryptionCandidatesProperty = new SimpleObjectProperty<>(new ArrayList<>());
    @Getter private final ObjectProperty<MappingPair<Integer,Integer>> progressProperty = new SimpleObjectProperty<>();
    @Getter private final BooleanProperty isWorkerFinishedWorkProperty = new SimpleBooleanProperty(false);

    public DecryptionWorkerImpl(EncryptionMachine encryptionMachine) {
        this.encryptionMachine = encryptionMachine;
        this.lastFoundCandidateProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                log.debug("Triggered inside Worker - Worker with id=[" + id + "] - FOUND CANDIDATE = " + newValue);
            }
        });
    }

    @Override
    public void assignWork(List<MachineState> workToDo, String inputToDecrypt) {
        if(workToDo == null || inputToDecrypt == null){
            log.error("Failed to assign work to DecryptionWorker with ID=" + id + "workToDo or input are null");
            return;
        }
        this.workToDo = workToDo;
        this.inputToDecrypt = inputToDecrypt;
        progressProperty.setValue(new MappingPair<>(0,workToDo.size()));
        log.debug("Worker with id=[" + id + "] was assigned work successfully, workSize=" + workToDo.size() + ", Input=" + inputToDecrypt);
    }

    /**
     * Assign Work before running
     * if no work were assigned, will do nothing
     */
    @Override
    public void run() {
        if(workToDo == null || workToDo.isEmpty() || inputToDecrypt == null){
            updateWorkerPropertiesToFinished();
            return;
        }
        if(DictionaryManagerStatic.getDictionary().isEmpty()){
            log.warn("Dictionary is empty");
        }
        log.info("Worker [" + id + "] - started running on input=" + inputToDecrypt);
        for (MachineState state : workToDo ) {
            encryptionMachine.setMachineState(state);
            String decryptedWord = encryptionMachine.decrypt(inputToDecrypt);
            if(DictionaryManagerStatic.isInDictionary(decryptedWord)){
                DecryptionCandidateInfo newCandidate = new DecryptionCandidateInfo();
                newCandidate.setInput(inputToDecrypt);
                newCandidate.setOutput(decryptedWord);
                newCandidate.setInitialState(state);
                newCandidate.setResponsibleAgentId(this.id);
                //Updating Properties
                lastFoundCandidateProperty.setValue(newCandidate);
                List<DecryptionCandidateInfo> updatedList = new ArrayList<>(allFoundDecryptionCandidatesProperty.get());
                updatedList.add(newCandidate);
                allFoundDecryptionCandidatesProperty.setValue(updatedList);
            }
            //Updating progress
            MappingPair<Integer,Integer> updatedProgress = new MappingPair<>(progressProperty.get().getLeft(),progressProperty.get().getRight());
            progressProperty.setValue(updatedProgress);
        }
        isWorkerFinishedWorkProperty.setValue(true);
    }

    private void updateWorkerPropertiesToFinished(){
        if(workToDo == null || workToDo.isEmpty()){
            progressProperty.setValue(new MappingPair<>(0,0));
        }
        else{
            progressProperty.setValue(new MappingPair<>(workToDo.size(), workToDo.size()));
        }
        isWorkerFinishedWorkProperty.setValue(true);
    }
}
