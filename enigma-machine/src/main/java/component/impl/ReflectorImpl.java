package main.java.component.impl;

import main.java.component.Reflector;

import java.util.Map;

public class ReflectorImpl implements Reflector {

    private Map<Integer,Integer> reflectionMapping;

    @Override
    public int getReflectedValue(int inValue) {
        return 0;
    }

    public ReflectorImpl() {
        //make sure map is good
    }
}
