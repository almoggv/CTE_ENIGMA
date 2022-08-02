package main.java.component.impl;

import javafx.util.Pair;
import main.java.component.Reflector;

import java.util.List;
import java.util.Map;

public class ReflectorImpl implements Reflector {

    private List<Pair<Integer,Integer>> reflectionMapping;

    @Override
    public int getReflectedValue(int inValue) {
        return 0;
    }

    public ReflectorImpl() {
        //make sure map is good
    }
}
