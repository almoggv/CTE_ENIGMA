package AgentClientDMTest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

public class SubRunnableClass implements Runnable{

    public StringProperty stringProperty = new SimpleStringProperty();
    public List<String> workToDo;

    public SubRunnableClass(List<String> workToDo) {
        this.workToDo = workToDo;
    }

    @Override
    public void run() {
        for (String str : workToDo ) {
            stringProperty.setValue(str);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
