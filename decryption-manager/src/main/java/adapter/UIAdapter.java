package adapter;

import generictype.MappingPair;
import javafx.application.Platform;
import dto.AgentDecryptionInfo;



import java.util.function.Consumer;

public class UIAdapter {
    private Consumer<AgentDecryptionInfo> updateCandidates;
    private Consumer<MappingPair<Integer,Integer>> updateProgress;
    private Consumer<Long> updateMeanDecryptionTime;


    public UIAdapter(Consumer<AgentDecryptionInfo> updateCandidates, Consumer<MappingPair<Integer,Integer>> updateProgress, Consumer<Long> updateMeanDecryptionTime) {
        this.updateCandidates = updateCandidates;
        this.updateProgress = updateProgress;
        this.updateMeanDecryptionTime = updateMeanDecryptionTime;
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

    public void sendMeanTimeOfDecryption(Long meanDecryptionTime) {
        Platform.runLater(
                ()->{
                    updateMeanDecryptionTime.accept(meanDecryptionTime);
                }
        );
    }
}
