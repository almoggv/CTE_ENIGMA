package main.java.component;

public interface Rotor {

    public int getHeadLocation();

    public int getId();

    public int fromInputWheelToReflector(int inValue);

    public int fromReflectorToInputWheel(int inValue);

    public boolean doesNotchAllowRotation();

    public boolean setRotorPosition(int headPosition);

    public void rotate();
}
