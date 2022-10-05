package service;

import enums.XmlVerifierState;
import generated.CTEDecipher;
import generated.CTEEnigma;
import generated.CTEMachine;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface XmlSchemaVerifier extends Serializable {
    public boolean isFileInExistenceAndXML(String path) throws IOException;
    public XmlVerifierState isMachineConfigurationValid(CTEMachine machine);
    public XmlVerifierState isDecipherConfigurationValid(CTEDecipher cteDecipher);
    XmlVerifierState isXmlSchemaValid(CTEEnigma cteEnigma);
    XmlVerifierState isXmlSchemaValidEX2(CTEEnigma cteEnigma);
}
