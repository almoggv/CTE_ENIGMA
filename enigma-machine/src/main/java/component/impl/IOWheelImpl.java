package main.java.component.impl;

import main.java.component.IOWheel;
import main.java.generictype.MappingPair;
import main.java.generictype.MappingPairListUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class IOWheelImpl implements IOWheel {

    private static final Logger log = Logger.getLogger(IOWheelImpl.class);
    private final List<MappingPair<Integer,String>> numToletterMap = new ArrayList<>();
    private final String ABC;

    static {
        try {
            URL log4JPropertyUrl = IOWheelImpl.class.getResource("/main/resources/log4j.properties");
            String log4JPropertyFile = log4JPropertyUrl.getFile();
            Properties p = new Properties();
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + IOWheelImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    public IOWheelImpl(String ABC) {
        for (int i = 0; i < ABC.length(); i++) {
            MappingPair<Integer,String> newPair = new MappingPair<Integer,String>(i, ABC.substring(i,i+1));
            numToletterMap.add(newPair);
        }
        this.ABC = ABC;
    }
    @Override
    public int handleInput(String input) {
        try{
            int result = MappingPairListUtils.<Integer,String>getLeftByRight(numToletterMap,input);
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
            String result =  MappingPairListUtils.getRightByLeft(numToletterMap,input);
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
        return numToletterMap.size();
    }

    @Override
    public IOWheel getDeepClone() {
        String recreatedABC = new String();
        for (MappingPair<Integer,String> pair : this.numToletterMap) {
            recreatedABC = recreatedABC.concat(pair.getRight());
        }
        return new IOWheelImpl(recreatedABC);
    }

    @Override
    public String getABC() {
        return this.ABC;
    }
}
