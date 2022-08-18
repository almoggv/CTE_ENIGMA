package main.java.component;

import lombok.Getter;
import main.java.enums.ReflectorsId;
import main.java.generictype.DeepCloneable;

import java.io.Serializable;

public interface Reflector extends DeepCloneable, Serializable {

    public int getReflectedValue(int inValue);

    public ReflectorsId getId();

    @Override
    public Reflector getDeepClone();

}
