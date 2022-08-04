package main.java.component.impl;

import lombok.Getter;
import main.java.component.Rotor;
import main.resources.generated.CTEPositioning;
import main.resources.generated.CTERotor;
import main.resources.generated.CTERotors;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;
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
    private final List<MappingPair<Integer,Integer>> rotorMapping = new ArrayList<>();

    @Getter private final int id;
    private int headPosition = 0;

    private final int notchLocation;
    public RotorImpl(CTERotor cteRotor, String ABC) {
        List<CTEPositioning> positioning = cteRotor.getCTEPositioning();
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();

        for(CTEPositioning position: positioning){
            String l = position.getLeft();
            String r = position.getRight();
            MappingPair<Integer,Integer> pair = new MappingPair<Integer,Integer>(ABC.indexOf(l),ABC.indexOf(r));
            rotorMapping.add(pair);
        }

        this.id = cteRotor.getId();
        this.notchLocation = (cteRotor.getNotch() -1) % rotorMapping.size();
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
