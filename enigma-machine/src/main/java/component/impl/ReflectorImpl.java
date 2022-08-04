package main.java.component.impl;

import main.java.component.Reflector;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReflectorImpl implements Reflector {

    private static final Logger log = Logger.getLogger(ReflectorImpl.class);

    private final List<MappingPair<Integer,Integer>> reflectionMapping = new ArrayList<>();

    static {
        String log4JPropertyFile = "./src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + ReflectorImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    //TODO: add ctor from CTE-Reflector class

    @Override
    public int getReflectedValue(int inValue) {
        return MappingPairListUtils.getRightByLeft(reflectionMapping,inValue);
    }


}
