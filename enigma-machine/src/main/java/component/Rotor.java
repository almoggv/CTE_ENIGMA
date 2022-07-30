package main.java.component;

public interface Rotor {

    public int getValue(int inValue);

    public boolean doesNotchAllowRotation();

    public boolean setRotorPosition(int headPosition);

    public void rotate();
}
