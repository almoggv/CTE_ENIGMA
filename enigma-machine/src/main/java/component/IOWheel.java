package main.java.component;

import main.java.generictype.DeepCloneable;

public interface IOWheel extends DeepCloneable {

    public int handleInput(String input);

    public String handleInput(int input);

    public int getWheelSize();

    @Override
    public IOWheel getDeepClone();
}
