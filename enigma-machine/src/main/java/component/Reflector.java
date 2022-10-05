package component;

import lombok.Getter;
import enums.ReflectorsId;
import generictype.DeepCloneable;

import java.io.Serializable;

public interface Reflector extends DeepCloneable, Serializable {

    public int getReflectedValue(int inValue);

    public ReflectorsId getId();

    @Override
    public Reflector getDeepClone();

}
