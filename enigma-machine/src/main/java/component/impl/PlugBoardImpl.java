package component.impl;


import component.PlugBoard;
import generictype.MappingPair;
import generictype.MappingPairListUtils;
import service.XmlFileLoader;
import service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class PlugBoardImpl implements PlugBoard {

    private static final Logger log = Logger.getLogger(PlugBoardImpl.class);

    private List<MappingPair<String, String>> plugBoardMapping = new ArrayList<>();

    static {
        try {
            Properties p = new Properties();
            p.load(XmlFileLoader.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + PlugBoardImpl.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + PlugBoardImpl.class.getSimpleName() ) ;
        }
    }

    public PlugBoardImpl(String ABC) {
        for (int i = 0; i < ABC.length(); i++) {
            MappingPair<String,String> newPair = new MappingPair<String,String>( ABC.substring(i,i+1), ABC.substring(i,i+1));
            plugBoardMapping.add(newPair);
        }
    }

    @Override
    public boolean connectMultiple(List<String> leftList, List<String> rightList ) {
        if(leftList.size() != rightList.size()){
            return false;
        }
        boolean isOperationSuccessful = true;
        Iterator<String> leftIter = leftList.iterator();
        Iterator<String> rightIter = rightList.iterator();
        while(leftIter.hasNext() && rightIter.hasNext()){
            isOperationSuccessful = isOperationSuccessful || connect(leftIter.next(),rightIter.next());
        }
        return isOperationSuccessful;
    }

    @Override
    public boolean connectMultiple(List<MappingPair<String, String>> connections) {
        boolean isOperationSuccessful = true;
        for (MappingPair<String, String> pair : connections) {
            isOperationSuccessful = isOperationSuccessful && connect(pair.getLeft(), pair.getRight());
        }
        return isOperationSuccessful;
    }

    @Override
    public boolean connect(String endPoint1, String endPoint2) {
        boolean isEndPoint1Free = false, isEndPoint2Free = false;
        if(isEndPointConnected(endPoint1)){
           log.warn("Plugboard : Failed to connect endpoints -" + endPoint1 + "is already connected");
            return false;
        }
        if(isEndPointConnected(endPoint2)){
            log.warn("Plugboard : Failed to connect endpoints -" + endPoint2 + "is already connected");
            return false;
        }
        MappingPair<String, String> endPoint1Pair = MappingPairListUtils.findPairByLeft(plugBoardMapping, endPoint1);
        MappingPair<String, String> endPoint2Pair = MappingPairListUtils.findPairByLeft(plugBoardMapping, endPoint2);
        int indexEndPoint1 = plugBoardMapping.indexOf(endPoint1Pair);
        int indexEndPoint2 = plugBoardMapping.indexOf(endPoint2Pair);
        endPoint1Pair.setRight(endPoint2);
        endPoint2Pair.setRight(endPoint1);
        plugBoardMapping.set(indexEndPoint1,endPoint1Pair);
        plugBoardMapping.set(indexEndPoint2,endPoint2Pair);
        log.debug("Plugboard made new connection: \"" +  endPoint1 + "\"<->\"" + endPoint2+"\"");
        return true;
    }

    @Override
    public boolean disconnect(String endPoint) {
        MappingPair<String,String> endPointPair = MappingPairListUtils.findPairByLeft(plugBoardMapping, endPoint);
        MappingPair<String,String> endPointMappedTo = MappingPairListUtils.findPairByRight(plugBoardMapping, endPoint);
        int indexEndPoint = plugBoardMapping.indexOf(endPointPair);
        int indexEndPointMappedTo = plugBoardMapping.indexOf(endPointMappedTo);
        endPointPair.setRight(endPointPair.getLeft());
        endPointMappedTo.setRight(endPointMappedTo.getLeft());
        plugBoardMapping.set(indexEndPoint,endPointPair);
        plugBoardMapping.set(indexEndPointMappedTo,endPointMappedTo);
        log.debug("Plugboard disconnected :" + endPoint + " and " + endPointMappedTo.getLeft());
        return true;
    }

    @Override
    public boolean clearAllPlugs() {
        for ( MappingPair<String,String> pair : plugBoardMapping ) {
            pair.setRight(pair.getLeft());
        }
        return true;
    }

    @Override
    public String getMappedValue(String inValue) {
        return MappingPairListUtils.getRightByLeft(plugBoardMapping, inValue);
    }

    /**
     * created an returns a minmalistic mapping
     * @return
     */
    @Override
    public List<MappingPair<String,String>> getCurrentMapping() {
        List<MappingPair<String,String>> currentMapping = new ArrayList<>();
        boolean isEndPointAlreadyFound = false;
        for (MappingPair<String,String> pair : this.plugBoardMapping ) {
            isEndPointAlreadyFound = false;
            isEndPointAlreadyFound = currentMapping.contains(MappingPairListUtils.findPairByLeft(plugBoardMapping,pair.getLeft()))
                    || currentMapping.contains(MappingPairListUtils.findPairByRight(plugBoardMapping,pair.getLeft()));
            if(isEndPointConnected(pair.getLeft()) && !isEndPointAlreadyFound){
                MappingPair<String,String> newPair = new MappingPair<String,String>(pair.getLeft(), pair.getRight());
                currentMapping.add(newPair);
            }
        }
        return currentMapping;
    }

    @Override
    public PlugBoard getDeepClone() {
        String recreatedABC = new String();
        for (MappingPair<String,String> pair : this.plugBoardMapping) {
            recreatedABC = recreatedABC.concat(pair.getLeft());
        }
        PlugBoardImpl clonedPlugBoard = new PlugBoardImpl(recreatedABC);
        List<MappingPair<String, String>> currMapping = this.getCurrentMapping();
        clonedPlugBoard.connectMultiple(currMapping);
        return clonedPlugBoard;
    }

    protected boolean isEndPointConnected(String endPoint){
        MappingPair<String,String> currentMappingOfEPoint = MappingPairListUtils.findPairByLeft(plugBoardMapping, endPoint);
        if(currentMappingOfEPoint == null || currentMappingOfEPoint.getLeft().equals(currentMappingOfEPoint.getRight())){
            return false;
        }
        return true;
    }
}
