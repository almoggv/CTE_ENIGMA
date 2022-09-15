package src.main.java.adapter;

import javafx.application.Platform;
import main.java.dto.AgentDecryptionInfo;

import java.util.function.Consumer;

public class UIAdapter {
    private Consumer<AgentDecryptionInfo> updateCandidates;

//    private Runnable updateDistinct;


    public UIAdapter(Consumer<AgentDecryptionInfo> updateCandidates) {
        this.updateCandidates = updateCandidates;
    }

    public void addNewWord(AgentDecryptionInfo agentDecryptionInfo) {
        Platform.runLater(
                () -> {
                    updateCandidates.accept(agentDecryptionInfo);
//                    updateCandidates.run();
                }
        );
    }
}
