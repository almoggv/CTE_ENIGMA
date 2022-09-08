package main.java.handler;

import main.java.service.PropertiesService;
import main.resources.generated.CTEEnigma;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Properties;

public class FileConfigurationHandler {
    private static final Logger log = Logger.getLogger(FileConfigurationHandler.class);

    static {
        try {
            Properties p = new Properties();
            p.load(FileConfigurationHandler.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + FileConfigurationHandler.class.getSimpleName());
        } catch (Exception e) {
            System.out.println("Failed to configure logger of -" + FileConfigurationHandler.class.getSimpleName() ) ;
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
