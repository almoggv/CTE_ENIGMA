package pausePlayThreadTesting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

public class PausableRunnable implements Runnable {

    private final Object lockContext = new Object();
    @Getter private final BooleanProperty isRunningProperty = new SimpleBooleanProperty();
    @Getter private final BooleanProperty isStoppedProperty = new SimpleBooleanProperty();


    public PausableRunnable() {
        isRunningProperty.setValue(true);
        isStoppedProperty.setValue(false);
    }

    @Override
    public void run() {
        while (!isStoppedProperty.get()) {
            synchronized (lockContext) {
                if (isRunningProperty.get() == false) {
                    try {
                        System.out.println("pausableThread - im waiting..");
                        lockContext.wait();
                    } catch (InterruptedException ignore) {}
                }
            }
            System.out.println("PausableThread - Im running");
            try {
                Thread.sleep(750);
            } catch (InterruptedException ignore) {
                System.out.println("inside pThread - caught exception after sleep ");
            }
            // Your code here
        }
    }

    public void stop(){
        System.out.println("PausableThread - STOPPED");
        isStoppedProperty.setValue(true);
    }

    public void pause() {
        // you may want to throw an IllegalStateException if !running
        System.out.println("PausableThread - i was paused");
        isRunningProperty.setValue(false);
    }

    public void resume() {
        synchronized (lockContext) {
            System.out.println("PausableThread - i was Resumed");
            isRunningProperty.setValue(true);
            lockContext.notifyAll(); // Unblocks thread
        }
    }
}
