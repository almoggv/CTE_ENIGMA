package agent.impl;

//import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.property.*;
import javafx.util.Pair;
import lombok.Getter;
import agent.DecryptionAgent;
import component.EncryptionMachine;
import dto.AgentDecryptionInfo;
import dto.MachineState;
import generictype.MappingPair;
import manager.DictionaryManager;
import service.PropertiesService;
import service.XmlFileLoader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.*;

public class DecryptionAgentImpl implements DecryptionAgent {
    private static final Logger log = Logger.getLogger(DecryptionAgentImpl.class);

    @Getter UUID id = UUID.randomUUID();
    private final EncryptionMachine encryptionMachine;
    private List<MachineState> startingConfigurations;
    private String inputToDecrypt;
    /**
     * Is updated through out the run, each decryption that yields a potentail candidate, the property is updated
     */
    @Getter private final ObjectProperty<AgentDecryptionInfo> decryptionInfoProperty = new SimpleObjectProperty<>();
    @Getter private final ObjectProperty<MappingPair<Integer,Integer>> progressProperty = new SimpleObjectProperty<>();
    @Getter private final BooleanProperty isFinishedProperty = new SimpleBooleanProperty(false);
    /**
     * Is updated once ALL the work is done. if no potential candidates were found, wont update.
     */
    @Getter private final ObjectProperty<List<AgentDecryptionInfo>> potentialCandidatesListProperty = new SimpleObjectProperty<>(); //Is triggered only when the worker finishes its job, after fininding all potential
    private long startEncryptionTime = -1;
    private long endEncryptionTime = -1;

    private final Object lockContext = new Object();
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty(true);
    @Getter private final BooleanProperty isStoppedProperty = new SimpleBooleanProperty(false);
    private int lastStateTestedIndex = 0;

    static {
        try {
            Properties p = new Properties();
            p.load(DecryptionAgentImpl.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DecryptionAgentImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DecryptionAgentImpl.class.getSimpleName() ) ;
        }
    }

    public DecryptionAgentImpl(EncryptionMachine encryptionMachine) {
        this.encryptionMachine = encryptionMachine;
        progressProperty.addListener(((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.getLeft() >= newValue.getRight()){
                isFinishedProperty.setValue(true);
            }
        }));
        isFinishedProperty.addListener(((observable, oldValue, newValue) -> {
            if(newValue == true){
                isStoppedProperty.setValue(true);
                isRunningProperty.setValue(false);
            }
        }));
        isStoppedProperty.addListener(((observable, oldValue, newValue) -> {
            if(newValue == true){
                isRunningProperty.setValue(false);
            }
        }));


        log.debug("newly created agent: "+ this.id);
    }

    public void assignWork(List<MachineState> startingConfigurations, String inputToDecrypt){
//        if(startingConfigurations != null){
//            this.startingConfigurations.clear();
//        }
        this.startingConfigurations = startingConfigurations;
        this.inputToDecrypt = inputToDecrypt;
        potentialCandidatesListProperty.setValue(null);
        decryptionInfoProperty.setValue(null);
        progressProperty.setValue(new MappingPair<Integer,Integer>(0,startingConfigurations.size()));
        isFinishedProperty.setValue(false);
        log.debug("agent " + id + " got work");
        //Connect inner Run Properties:
    }

    @Override
    public long getTimeTookToCompleteWork() {
        if(isFinishedProperty.get() == false || isStoppedProperty.get() == false || endEncryptionTime == -1){
            return 0;
        }
        return endEncryptionTime - startEncryptionTime;
    }

    @Override
    public void run() {
        log.debug("Agent " + this.id + " started running decryption");
        List<AgentDecryptionInfo> potentialCandidates = new ArrayList<>();
        Optional<String> decryptionCandidate;
        int PROGRESS_UPDATE_INTERVAL = (int)(Math.ceil((0.1)*(startingConfigurations.size())));
        if(startingConfigurations == null){
            throw new RuntimeException("MachineStates given to agent is null");
        }
        startEncryptionTime = System.nanoTime();
        while (!isStoppedProperty.get()) {
            synchronized (lockContext) {
                if (isRunningProperty.get() == false) {
                    try {
                        lockContext.wait();
                    } catch (InterruptedException ignore) {}
                }
            }
            //Actual Run Logic:
            for (; this.lastStateTestedIndex < startingConfigurations.size();  lastStateTestedIndex++) {
                if(isRunningProperty.get() == false || isStoppedProperty.get() == true){
                    break;
                }
                long startEncryptionTime = System.nanoTime();
                MachineState initialState = startingConfigurations.get(lastStateTestedIndex);
                MachineState stateBeforeEncryption = initialState.getDeepClone();
                decryptionCandidate = runSingleDecryption(initialState,this.inputToDecrypt);
                long encryptionTime = System.nanoTime() - startEncryptionTime;
                if(decryptionCandidate.isPresent()){
//                    AgentDecryptionInfo decryptionInfo = new AgentDecryptionInfo(this.id,stateBeforeEncryption,inputToDecrypt,decryptionCandidate.get(),encryptionTime);
//                    decryptionInfoProperty.setValue(decryptionInfo);
//                    potentialCandidates.add(decryptionInfo);
                    synchronized (this){
                        log.info("Agent ["+id+"] found a candidate: " + inputToDecrypt + "-->"+ decryptionCandidate.get() + " ---- reflector: " + stateBeforeEncryption.getReflectorId() + " rotors: "+ stateBeforeEncryption.getRotorIds() +" ,init pos: "+ stateBeforeEncryption.getRotorsHeadsInitialValues()) ;
                    }
                }
//                if((lastStateTestedIndex+1) % PROGRESS_UPDATE_INTERVAL == 0){
                    progressProperty.setValue(new MappingPair<>((lastStateTestedIndex+1),startingConfigurations.size()));
//                }
                endEncryptionTime = System.nanoTime();
            }
            //if finished work
            if(progressProperty.get().getLeft().equals(progressProperty.get().getRight())){
                if(potentialCandidates.size() > 0){
                    potentialCandidatesListProperty.setValue(potentialCandidates);
                }
                isFinishedProperty.setValue(true);
                isStoppedProperty.setValue((true));
                log.debug("agent "+this.id + "finished work");
            }
        }
    }

    private Optional<String> runSingleDecryption(MachineState machineInitialState, String inputToDecrypt){
        encryptionMachine.setMachineState(machineInitialState);
        String decryptionResult;
        decryptionResult = encryptionMachine.decrypt(inputToDecrypt);
        if(checkIfInDictionary(decryptionResult)) {
            return Optional.of(decryptionResult);
        }
        return Optional.empty();
    }

    private boolean checkIfInDictionary(String decryptionResult) {

        String[] decryptedWords = decryptionResult.split(DictionaryManager.getDELIMITER());
        for (String word :decryptedWords ) {
            if(!DictionaryManager.getDictionary().containsKey(word)){
                return false;
            }
        }
        return true;
    }

    public void stop(){
        isStoppedProperty.setValue(true);
        isRunningProperty.setValue(false);
    }

    public void pause() {
        isRunningProperty.setValue(false);
    }

    public void resume() {
        synchronized (lockContext) {
            isRunningProperty.setValue(true);
            lockContext.notifyAll(); // Unblocks thread
        }
    }
}
