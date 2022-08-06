package main.java.component;

import main.java.generictype.DeepCloneable;

public interface Rotor extends DeepCloneable {

    public int getHeadLocation();

    public int getId();

    public int fromInputWheelToReflector(int inValue);

    public int fromReflectorToInputWheel(int inValue);

    public boolean doesNotchAllowRotation();

    public boolean setRotorStartingPosition(int headPosition);

    public void rotate();

    @Override
    public Rotor getDeepClone();
}
