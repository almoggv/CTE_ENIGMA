package main.java.component.impl;

import lombok.Getter;
import main.java.component.Rotor;
import main.java.generictype.MappingPair;
import main.java.generictype.MappingPairListUtils;
import main.java.handler.FileConfigurationHandler;
import main.java.service.PropertiesService;
import main.resources.generated.CTEPositioning;
import main.resources.generated.CTERotor;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
        try {
            Properties p = new Properties();
            p.load(FileConfigurationHandler.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + RotorImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + RotorImpl.class.getSimpleName()) ;
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
    public int getValueInHead() {
        return rotorMapping.getFirst().getRight();
    }

    @Override
    public int getHeadLocation() {
        return headPosition;
    }

    @Override
    public int howCloseNotchToHead() {
//        return (rotorMapping.size() - Math.abs(notchLocation - headPosition)) % rotorMapping.size();
        int result = 0;
        if(headPosition <= notchLocation) {
            return Math.abs(notchLocation - headPosition);
        }
        else{
            return (rotorMapping.size() - Math.abs(notchLocation - headPosition)) % rotorMapping.size();
        }
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
        while(rotorMapping.getFirst().getRight() != valueOfHeadInRight){
            rotate();
        }
        return true;
    }

    @Override
    public void rotate() {
        headPosition = (headPosition + 1) % rotorMapping.size();
//        headPosition = headPosition - 1;
//        if(headPosition < 0) headPosition += rotorMapping.size();
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
