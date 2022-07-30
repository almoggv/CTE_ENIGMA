package main.java.component.impl;

import main.java.component.Rotor;

import java.util.Map;

public class RotorImpl implements Rotor {

    private Map<Integer,Integer> rotorMapping;

    private int headPosition;

    private int notchLocation;

    @Override
    public int getValue(int inValue) {
        return 0;
    }

    @Override
    public boolean doesNotchAllowRotation() {
        return false;
    }

    @Override
    public boolean setRotorPosition(int headPosition) {
        return false;
    }

    @Override
    public void rotate() {

    }
}
