package main.java.component;

import lombok.Getter;
import main.java.enums.ReflectorsId;

public interface Reflector {

    public int getReflectedValue(int inValue);

    public ReflectorsId getId();

}
