package component;

import generictype.DeepCloneable;

import java.io.Serializable;

public interface IOWheel extends DeepCloneable , Serializable {

    public int handleInput(String input);

    public String handleInput(int input);

    public int getWheelSize();

    @Override
    public IOWheel getDeepClone();

    String getABC();
}
