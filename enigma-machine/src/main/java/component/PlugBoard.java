package main.java.component;


import main.java.generictype.DeepCloneable;
import main.java.generictype.MappingPair;

import java.io.Serializable;
import java.util.List;

public interface PlugBoard extends DeepCloneable , Serializable {

    /**
     * maps the left list to the right list on the plug board by indexes : first <-> first , second <-> second..
     * @param leftList
     * @param rightList
     * @return
     */
    public boolean connectMultiple(List<String> leftList, List<String> rightList);

    public boolean connectMultiple(List<MappingPair<String, String>> connections);

    public boolean connect(String endPoint1, String endPoint2);

    public boolean disconnect(String endPoint);

    public boolean clearAllPlugs();

    public String getMappedValue(String inValue);

    public List<MappingPair<String,String>> getCurrentMapping();

    @Override
    public PlugBoard getDeepClone();
}
