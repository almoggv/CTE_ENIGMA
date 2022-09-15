package main.java.manager;

import main.java.enums.DecryptionDifficultyLevel;
import src.main.java.adapter.UIAdapter;

public interface DecryptionManager {

    void bruteForceDecryption(String sourceInput) throws InterruptedException;
    public void setUserInput(int numberOfAgents, DecryptionDifficultyLevel difficultyLevel, int taskSize);
    public void pauseWork();
    public void resumeWork();
    int getNumberOfAgents();
    void setNumberOfAgents(int value);

    int getTaskSize();
    void setTaskSize(int value);

    DecryptionDifficultyLevel getDifficultyLevel();
    void setDifficultyLevel(DecryptionDifficultyLevel level);
    int getAmountOfTasks();

    void setUIAdapter(UIAdapter uiAdapter);
}
