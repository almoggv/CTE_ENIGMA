package pausePlayThreadTesting;

import org.junit.Test;

public class PausePlayThreadTest {

    @Test
    public void pauseAndResumeThread() throws InterruptedException {
        PausableRunnable pthread = new PausableRunnable();
        Thread normalThread = new Thread(pthread);

        normalThread.start();
        System.out.println("Main going to sleep..");
        Thread.sleep(4000);
        System.out.println("Main Woke Up..\nMain pausing the Thread");
        pthread.pause();
        for (int i = 0; i < 5; i++) {
            System.out.println("Main running..");
            Thread.sleep(1000);
        }
        System.out.println("Main resuming the Thread");
        pthread.resume();
        System.out.println("Main going to sleep [2]..");
        Thread.sleep(5000);
        System.out.println("Killing the thread");
        pthread.stop();

        try{
            normalThread.interrupt();
        }
        catch (Exception ignore){
            System.out.println("Interrupt raised an exception");
        }

        Thread.currentThread().join();
    }


}
