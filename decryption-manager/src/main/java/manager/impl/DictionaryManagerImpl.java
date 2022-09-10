package main.java.manager.impl;

import lombok.Getter;
import lombok.Setter;
import main.java.enums.XmlVerifierState;
import main.java.service.XmlFileLoader;
import main.java.manager.DictionaryManager;
import main.java.service.XmlSchemaVerifier;
import main.java.service.impl.XmlSchemaVerifierImpl;
import main.resources.generated.CTEEnigma;

public class DictionaryManagerImpl implements DictionaryManager {
    @Getter @Setter private String Abc;

    public DictionaryManagerImpl(String absolutePath) throws Exception {
        loadDictionary(absolutePath);
    }

    private void loadDictionary(String absolutePath) throws Exception {
        XmlSchemaVerifier xmlSchemaVerifier = new XmlSchemaVerifierImpl();
        CTEEnigma cteEnigma = XmlFileLoader.fromXmlFileToCTE(absolutePath);
        if(cteEnigma == null){
            throw new Exception("Failed to generate JAXB CTE Enigma objects by schema");
        }
        XmlVerifierState xmlVerifierResponse = xmlSchemaVerifier.isXmlSchemaValid(cteEnigma);
        if(xmlVerifierResponse == XmlVerifierState.VALID){
            buildDictionary();
    }
}
    private void buildDictionary() {

    }
    }
