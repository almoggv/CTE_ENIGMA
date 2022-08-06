package main.java.component;

import lombok.Getter;
import main.java.enums.ReflectorsId;
import main.java.generictype.DeepCloneable;

public interface Reflector extends DeepCloneable {

    public int getReflectedValue(int inValue);

    public ReflectorsId getId();

    public Reflector getDeepClone();

}
