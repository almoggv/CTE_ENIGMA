package main.java.component;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface PlugBoard {

    public boolean connectMultiple(List<Pair<Integer,Integer>> connectionPairs);

    public boolean connect(int endPoint1, int endPoint2);

    public boolean disconnect(int endPoint1, int endPoint2);

    public boolean clearAllPlugs();

    public int getMappedValue(int inValue);
}
