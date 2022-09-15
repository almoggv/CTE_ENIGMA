package main.java.manager;

public interface DecryptionManager {

    void bruteForceDecryption(String sourceInput) throws InterruptedException;

    public void pauseWork();
    public void resumeWork();
}
