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
import java.util.LinkedList;
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
    //private final List<MappingPair<Integer,Integer>> rotorMapping = new ArrayList<>();
    final LinkedList<MappingPair<Integer,Integer>> rotorMapping = new LinkedList<>(); //TODO: change impl

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
            rotorMapping.addLast(pair);
        }
        this.id = cteRotor.getId();
        this.notchLocation = (cteRotor.getNotch() -1) % rotorMapping.size();
    }

    static {
        String log4JPropertyFile = "./enigma-machine/src/main/resources/log4j.properties";
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

    // inValue represents the index counted from the head position
    // if inValue = 5
    // head = 3
    //      means we start counting 5 from 3 => 3+5 = 8
    //      this we module by size to simulate a circular structure
    // realIndex = 8 % 6 = 2
    // from here we take the left value of that index's mapping
    // given the this rotor:
    // (5,0) - i=0
    // (4,1) - i=1
    // (3,2) - i=2
    // (2,3) - i=3
    // (4,1) - i=4
    // (5,0) - i=5
    // given InValue = 5 => realIndex = 2 (i=2) => mappedValue = 3
    @Override
    public int fromInputWheelToReflector(int inValue) {
        int leftToFind = rotorMapping.get(inValue).getRight();
        MappingPair<Integer,Integer> foundMappingOfLeft = MappingPairListUtils.findPairByLeft(rotorMapping,leftToFind);
        return rotorMapping.indexOf(foundMappingOfLeft);
    }

    @Override
    public int fromReflectorToInputWheel(int inValue) {
        int rightToFind = rotorMapping.get(inValue).getLeft();
        MappingPair<Integer,Integer> foundMappingOfRight = MappingPairListUtils.findPairByRight(rotorMapping,rightToFind);
        return rotorMapping.indexOf(foundMappingOfRight);
    }

    @Override
    public boolean doesNotchAllowRotation() {
        return notchLocation == headPosition;
    }

    @Override
    public boolean setRotorStartingPosition(int numberOfRotations) {
        while(this.headPosition != 0){
            rotate();
        }
        for (int i = 0; i < numberOfRotations; i++) {
            rotate();
        }
        return true;
    }

    @Override
    public void rotate() {
        headPosition = (headPosition + 1) % rotorMapping.size();
        rotorMapping.addLast(rotorMapping.pop());
    }
}
