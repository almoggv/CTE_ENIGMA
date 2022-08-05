package main.java.component.impl;

import main.java.component.IOWheel;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class IOWheelImpl implements IOWheel {

    private static final Logger log = Logger.getLogger(IOWheelImpl.class);

    private final List<MappingPair<Integer,String>> letterToNumberMap = new ArrayList<>();

    static {
        String log4JPropertyFile = "./enigma-machine/src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + IOWheelImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    public IOWheelImpl(String ABC) {
        for (int i = 0; i < ABC.length(); i++) {
            MappingPair<Integer,String> newPair = new MappingPair<Integer,String>(i, ABC.substring(i,i+1));
            letterToNumberMap.add(newPair);
        }
    }
    @Override
    public int handleInput(String input) {
        try{
            int result = MappingPairListUtils.<Integer,String>getLeftByRight(letterToNumberMap,input);
            log.debug("IOWHeel input:" + input + "Output:" + result );
            return result;
        }
        catch(Exception e){
            log.warn("IOWHeel failed to find mapping of input: " + input);
            return -1;
        }
    }

    @Override
    public String handleInput(int input) {
        try{
            String result =  MappingPairListUtils.getRightByLeft(letterToNumberMap,input);
            log.debug("IOWHeel input:" + input + "Output:" + result );
            return result;
        }
        catch(Exception e) {
            log.warn("IOWHeel failed to find mapping of input: " + input);
            return null;
        }
    }

    @Override
    public int getWheelSize() {
        return letterToNumberMap.size();
    }
}
