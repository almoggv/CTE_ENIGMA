package main.java.common;

import javafx.beans.property.BooleanProperty;

public interface Pausable extends Runnable{

    BooleanProperty getIsRunningProperty();
    BooleanProperty getIsStoppedProperty();

    void stop();
    void pause();
    void resume();

}
