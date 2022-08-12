package main.java.verifier;

import main.java.enums.ReflectorsId;
import main.java.enums.XmlVerifierState;
import main.resources.generated.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface XmlSchemaVerifier {

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
