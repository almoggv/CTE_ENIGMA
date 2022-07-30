package main.java.component.impl;

import javafx.util.Pair;
import main.java.component.PlugBoard;

import java.util.List;
import java.util.Map;

public class PlugBoardImpl implements PlugBoard {

    private Map<Integer,Integer> boardMapping;

    @Override
    public boolean connectMultiple(List<Pair<Integer, Integer>> connectionPairs) {
        return false;
    }

    @Override
    public boolean connect(int endPoint1, int endPoint2) {
        return false;
    }

    @Override
    public boolean disconnect(int endPoint1, int endPoint2) {
        return false;
    }

    @Override
    public boolean clearAllPlugs() {
        return false;
    }

    @Override
    public int getMappedValue(int inValue) {
        return 0;
    }
}
