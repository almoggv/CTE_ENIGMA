package main.java.component.impl;

import lombok.Getter;
import main.java.component.Reflector;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import main.java.generictype.MappingPairListUtils;
import main.resources.generated.CTEReflect;
import main.resources.generated.CTEReflector;
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
    @Getter private final ReflectorsId id;

    static {
        String log4JPropertyFile = "./enigma-machine/src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + ReflectorImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    public ReflectorImpl(CTEReflector cteReflector) {
        List<CTEReflect> reflects = cteReflector.getCTEReflect();
        List<MappingPair<Integer,Integer>> rawReflectionMapping = new ArrayList<>();
        for(CTEReflect cteReflect : reflects) {
            int in = cteReflect.getInput() -1;
            int out = cteReflect.getOutput() -1;
            MappingPair<Integer,Integer> pairInOut = new MappingPair<Integer,Integer>(in, out);
            MappingPair<Integer,Integer> pairOutIn = new MappingPair<Integer,Integer>(out, in);
            rawReflectionMapping.add(pairInOut);
            rawReflectionMapping.add(pairOutIn);
        }
        rawReflectionMapping = MappingPairListUtils.sortByLeft(rawReflectionMapping);
        for (MappingPair<Integer,Integer> pair: rawReflectionMapping) {
            this.reflectionMapping.add(pair);
        }
        this.id = ReflectorsId.valueOf(cteReflector.getId());
    }

    protected ReflectorImpl(ReflectorsId id, List<MappingPair<Integer,Integer>> reflectionMapping){
        this.id = id;
        for (MappingPair<Integer,Integer> pair : reflectionMapping ) {
            this.reflectionMapping.add(pair);
        }
        //used for deepCloning
    };

    @Override
    public int getReflectedValue(int inValue) {
        //return MappingPairListUtils.getRightByLeft(reflectionMapping, inValue);
        //return reflectionMapping.get(inValue).getRight();
        MappingPair<Integer,Integer> pairOfInValue = MappingPairListUtils.findPairByLeft(reflectionMapping,inValue);
        int rightOfPairOfInValue = pairOfInValue.getRight();
        MappingPair<Integer,Integer>  thePairThatTheRightIsItsLeft = MappingPairListUtils.findPairByLeft(reflectionMapping,rightOfPairOfInValue);
        return reflectionMapping.indexOf(thePairThatTheRightIsItsLeft);
    }

    @Override
    public Reflector getDeepClone() {
        List<MappingPair<Integer,Integer>> clonedReflectionMapping = new ArrayList<>();
        for (MappingPair<Integer,Integer> pair: reflectionMapping ) {
            MappingPair<Integer,Integer> newClonedPair = new MappingPair<>(pair.getLeft(), pair.getRight());
            clonedReflectionMapping.add(newClonedPair);
        }
        return new ReflectorImpl(this.getId(),clonedReflectionMapping);
    }


}
