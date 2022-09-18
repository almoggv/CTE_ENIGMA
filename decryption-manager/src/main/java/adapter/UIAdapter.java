package main.java.adapter;

import javafx.application.Platform;
import main.java.dto.AgentDecryptionInfo;
import main.java.generictype.MappingPair;

import java.util.function.Consumer;

public class UIAdapter {
    private Consumer<AgentDecryptionInfo> updateCandidates;
    private Consumer<MappingPair<Integer,Integer>> updateProgress;

//    private Runnable updateDistinct;


    public UIAdapter(Consumer<AgentDecryptionInfo> updateCandidates, Consumer<MappingPair<Integer,Integer>> updateProgress) {
        this.updateCandidates = updateCandidates;
        this.updateProgress = updateProgress;
    }

    public void addNewCandidate(AgentDecryptionInfo agentDecryptionInfo) {
        Platform.runLater(
                () -> {
                    updateCandidates.accept(agentDecryptionInfo);
                }
        );
    }

    public void updateProgress(MappingPair<Integer,Integer> progress){
        Platform.runLater(
                ()->{
                    updateProgress.accept(progress);
                }
        );
    }
}
