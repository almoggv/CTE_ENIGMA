package AgentClientDMTest;

import common.ListUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ARunnableClass implements Runnable{

    private final ThreadPoolExecutor threadPoolService;
    public List<String> aList = new ArrayList<>();
    public boolean isKilled = false;
    public ObjectProperty<List<String>> listObjectProperty = new SimpleObjectProperty<>(new ArrayList<>());

    public ARunnableClass() {
        this.threadPoolService = new ThreadPoolExecutor(2, 2, 5 , TimeUnit.MINUTES, new ArrayBlockingQueue(4));
    }

    public void kill(){
        isKilled = true;
        System.out.println("RunnableClass - i was killed");
    }


    public void addToList(List<String> list) throws Exception {
        if(!aList.isEmpty()){
            throw new Exception("List Not Empty Yet");
        }

        aList.addAll(list);
    }

    public void swapList(List<String> list){
        aList.clear();
        aList.addAll(list);
    }

    @Override
    public void run() {
        isKilled = false;
        while(!isKilled){
            if(!aList.isEmpty()){
                divideWork();
            }
        }
    }

    private void divideWork() {
        List<List<String>> batches = ListUtils.partition(aList,2);
        for (List<String> batch : batches) {
            SubRunnableClass newRunnable = new SubRunnableClass(batch);
            newRunnable.stringProperty.addListener((observable, oldValue, newValue) -> {
                synchronized (this){
                    List<String> newListToTrigger = new ArrayList<>(listObjectProperty.get());
                    newListToTrigger.add(newValue);
                    listObjectProperty.setValue(newListToTrigger);
                }
            });
            threadPoolService.execute(newRunnable);
        }
        aList.clear();
     }
}
