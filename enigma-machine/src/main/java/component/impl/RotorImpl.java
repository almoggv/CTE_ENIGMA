package main.java.component.impl;

import lombok.Getter;
import main.java.component.Rotor;
import main.java.generictype.MappingPair;
import main.java.generictype.MappingPairListUtils;
import main.resources.generated.CTEPositioning;
import main.resources.generated.CTERotor;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Current Mapping:
 *  is the same as the model in the video [Reflector] <-> [Rotors] <-> [IOWheel]
 *  meaning:
 *      Left To Right : reflector to wheel
 *      Right To Left : wheel to reflector
 */
public class RotorImpl implements Rotor {

    private static final Logger log = Logger.getLogger(RotorImpl.class);

    private final LinkedList<MappingPair<Integer,Integer>> rotorMapping = new LinkedList<>();
    @Getter private final int id;
    private int headPosition = 0;
    @Getter private final int notchLocation;

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

    protected RotorImpl(int id, int notchLocation, int headPosition,LinkedList<MappingPair<Integer,Integer>> rotorMapping ){
        this.id = id;
        this.notchLocation = notchLocation;
        this.headPosition = headPosition;
        for (MappingPair<Integer,Integer> pair : rotorMapping) {
            this.rotorMapping.add(pair);
        }
    }

    @Override
    public int getHeadLocation() {
        return headPosition;
    }

    @Override
    public int howCloseNotchToHead() {
        return Math.abs(notchLocation - headPosition);
    }

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
    public boolean setRotorStartingPosition(int valueOfHeadInRight) {
        while(this.headPosition != 0){
            rotate();
        }
        while(rotorMapping.getFirst().getRight() != valueOfHeadInRight){
            rotate();
        }
        return true;
    }

    @Override
    public void rotate() {
        headPosition = (headPosition + 1) % rotorMapping.size();
        rotorMapping.addLast(rotorMapping.pop());
    }

    @Override
    public Rotor getDeepClone() {
        LinkedList<MappingPair<Integer,Integer>> clonedRotorMapping = new LinkedList<>();
        for (MappingPair<Integer,Integer> pair: this.rotorMapping ) {
            MappingPair<Integer,Integer> newClonedPair = new MappingPair<>(pair.getLeft(), pair.getRight());
            clonedRotorMapping.add(newClonedPair);
        }
        return new RotorImpl(this.id,this.notchLocation,this.headPosition,clonedRotorMapping);
    }
}
