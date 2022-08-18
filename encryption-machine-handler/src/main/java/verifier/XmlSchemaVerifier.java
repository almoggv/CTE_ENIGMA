package main.java.verifier;

import main.java.enums.XmlVerifierState;
import main.resources.generated.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface XmlSchemaVerifier extends Serializable {

    public XmlVerifierState isMachineConfigurationValid(CTEEnigma cteEnigma);

    public boolean isFileInExistenceAndXML(String path) throws IOException;

    public boolean isABCCountEven(String ABC);

    public boolean isRotorCountGood(int declaredRotorsCount, int numOfRotors);

    public boolean isRotorsDefinitionGood(CTERotors rotors, String ABC);

    public boolean isRotorsIdsLegal(List<CTERotor> rotors);

    public boolean isRotorsMappingLegal(List<CTERotor> rotors);

    public boolean isRotorMappingLegal(CTERotor rotor);

    public boolean isRotorsNotchLegal(List<CTERotor> rotors, String ABC);

    public boolean isReflectorsIdsLegal(List<CTEReflector> reflectors);

    public boolean isReflectorsMappingLegal(List<CTEReflector> reflectors);

    public boolean isReflectorMappingLegal(CTEReflector reflector);


}
