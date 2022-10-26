package RunnableClassTesting;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ChangeStateWhileRunningTest {


    @Test
    public void TestRunnableClassTateChangeMidRun() throws InterruptedException {
        ARunnableClass runnableClass = new ARunnableClass();
        runnableClass.listObjectProperty.addListener((observable, oldValue, newValue) -> {
            System.out.println("Listener triggered: new value=" + newValue);
        });
        Thread thread = new Thread(runnableClass);
        thread.start();
        List<String> list1 = Arrays.asList("Buenos Aires", "Córdoba", "La Plata", "Beer Sheva");
        try {
            runnableClass.addToList(list1);
        } catch (Exception e) {
            System.out.println("Failed to addToList - list1 - " + e.getMessage());
        }
        System.out.println("MainThread - assigned work, GOING TO SLEEP - (1)");
        Thread.sleep(5000);
        List<String> list2 = Arrays.asList("bla1","bla2","bla3","bla4");
        try {
            runnableClass.addToList(list2);
        } catch (Exception e) {
            System.out.println("Failed to addToList - list2 - " + e.getMessage());
        }
        System.out.println("MainThread - assigned more work, GOING TO SLEEP - (2)");
        Thread.sleep(5000);
        List<String> list3 = Arrays.asList("avrum","avrum2","shaka1","shakalaka");
        runnableClass.swapList(list3);
        System.out.println("MainThread - swapped work, GOING TO SLEEP - (3)");
        Thread.sleep(1500);
        runnableClass.kill();
        Thread.sleep(10000);
    }

}
