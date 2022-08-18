package main.java.component;

import main.java.generictype.DeepCloneable;

import java.io.Serializable;

public interface Rotor extends DeepCloneable , Serializable {

    public int getValueInHead();

    public int getHeadLocation();

    public int getId();

    public int getNotchLocation();

    public int howCloseNotchToHead();

    public int fromInputWheelToReflector(int inValue);

    public int fromReflectorToInputWheel(int inValue);

    public boolean doesNotchAllowRotation();

    public boolean setRotorStartingPosition(int valueOfHeadInRight);

    public void rotate();

    @Override
    public Rotor getDeepClone();
}
