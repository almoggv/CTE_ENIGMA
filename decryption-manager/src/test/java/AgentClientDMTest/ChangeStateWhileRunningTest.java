package AgentClientDMTest;

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
        runnableClass.addToList(list1);
        System.out.println("MainThread - assigned work, GOING TO SLEEP - (1)");
        Thread.sleep(5000);
        List<String> list2 = Arrays.asList("bla1","bla2","bla3","bla4");
        runnableClass.addToList(list2);
        System.out.println("MainThread - assigned more work, GOING TO SLEEP - (2)");
        Thread.sleep(5000);
        List<String> list3 = Arrays.asList("avrum","avrum2","shaka1","shakalaka");
        runnableClass.swapList(list3);
        System.out.println("MainThread - swapped work, GOING TO SLEEP - (3)");
        Thread.sleep(1500);
        runnableClass.kill();
    }

}
