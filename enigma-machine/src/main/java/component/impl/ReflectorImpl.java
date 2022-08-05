package main.java.component.impl;

import lombok.Getter;
import main.java.component.Reflector;
import main.java.enums.ReflectorsId;
import main.resources.generated.CTEReflect;
import main.resources.generated.CTEReflector;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.persistence.criteria.CriteriaBuilder;
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
        for(CTEReflect cteReflect : reflects) {
            int in = cteReflect.getInput() -1;
            int out = cteReflect.getOutput() -1;
            MappingPair<Integer,Integer> pairInOut = new MappingPair<Integer,Integer>(in, out);
            MappingPair<Integer,Integer> pairOutIn = new MappingPair<Integer,Integer>(out, in);
            reflectionMapping.add(pairInOut);
            reflectionMapping.add(pairOutIn);
        }
        this.id = ReflectorsId.valueOf(cteReflector.getId());
    }

    @Override
    public int getReflectedValue(int inValue) {
        return MappingPairListUtils.getRightByLeft(reflectionMapping, inValue);
    }


}
