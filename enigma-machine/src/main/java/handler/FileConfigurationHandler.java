package main.java.handler;

import main.java.component.impl.EnigmaMachine;
import main.resources.generated.CTEEnigma;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileConfigurationHandler {
    private static final Logger log = Logger.getLogger(FileConfigurationHandler.class);

    static {
        String log4JPropertyFile = "./enigma-machine/src/main/resources/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instatiated for : " + FileConfigurationHandler.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    public static CTEEnigma fromXmlFileToCTE(String absolutePath) {
        try {
            File file = new File(absolutePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(CTEEnigma.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            CTEEnigma cte = (CTEEnigma) jaxbUnmarshaller.unmarshal(file);
            log.info("Generated a CTE Enigma Object from schema successfully from:" + absolutePath);
            return cte;

        } catch (JAXBException e) {
            log.error("Failed to generate JAXB CTE Enigma objects by schema : " + e.getMessage());
            return null;
        }
    }
}
