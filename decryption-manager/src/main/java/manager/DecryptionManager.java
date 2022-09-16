package main.java.manager;

import main.java.enums.DecryptionDifficultyLevel;

public interface DecryptionManager {

    void bruteForceDecryption(String sourceInput) throws InterruptedException;

//    public void setUserInput(int numberOfAgents, DecryptionDifficultyLevel difficultyLevel, int taskSize);

    void pauseWork();
    void resumeWork();
    void stopWork();
    void awaitWork();


    int getNumberOfAgents();

    void setNumberOfAgents(int value);

    int getTaskSize();

    void setTaskSize(int value);

    DecryptionDifficultyLevel getDifficultyLevel();

    void setDifficultyLevel(DecryptionDifficultyLevel level);

    int getAmountOfTasks();

}
