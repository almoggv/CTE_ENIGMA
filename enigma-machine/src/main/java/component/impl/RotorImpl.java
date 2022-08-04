package main.java.component.impl;

import main.java.component.Rotor;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class RotorImpl implements Rotor {

    private static final Logger log = Logger.getLogger(RotorImpl.class);

    /**
     * Curent Mapping:
     *  is the same as the model in the video [Reflector] <-> [Rotors] <-> [IOWheel]
     *  meaning:
     *      Left To Right : reflector to wheel
     *      Right To Left : wheel to reflector
     */
    private List<MappingPair<Integer,Integer>> rotorMapping = new ArrayList<>();

    private int headPosition = 0;

    private final int notchLocation;

    public RotorImpl(int notchLocation) {
        this.notchLocation = notchLocation % rotorMapping.size();
    }

    static {
        String log4JPropertyFile = "./src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + RotorImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    @Override
    public int getHeadLocation() {
        return headPosition;
    }

    @Override
    public int fromInputWheelToReflector(int inValue) {
        int realIndex = (inValue + headPosition) % rotorMapping.size();
        try{
         return MappingPairListUtils.getLeftByRight(rotorMapping, realIndex);
        }
        catch (Exception e){
            log.warn("Failed to get mapping of :" + realIndex);
            return -1;
        }
    }

    @Override
    public int fromReflectorToInputWheel(int inValue) {
        int realIndex = (inValue + headPosition) % rotorMapping.size();
        try{
            return MappingPairListUtils.getRightByLeft(rotorMapping, realIndex);
        }
        catch (Exception e){
            log.warn("Failed to get mapping of :" + realIndex);
            return -1;
        }
    }

    @Override
    public boolean doesNotchAllowRotation() {
        return notchLocation == headPosition;
    }

    @Override
    public boolean setRotorPosition(int headPosition) {
        this.headPosition = headPosition % rotorMapping.size();
        return true;
    }

    @Override
    public void rotate() {
        this.headPosition = (headPosition + 1) % rotorMapping.size();
    }
}
