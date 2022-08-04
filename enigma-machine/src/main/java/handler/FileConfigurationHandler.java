package main.java.handler;

import main.resources.generated.CTEEnigma;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class FileConfigurationHandler {

//    public static final String path;
//    public static void main(String[] args) {
//        fromXmlFileToObject();
//    }

    public static CTEEnigma fromXmlFileToCTE(String absolutePath) {
        try {
            File file = new File(absolutePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(CTEEnigma.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            CTEEnigma cte = (CTEEnigma) jaxbUnmarshaller.unmarshal(file);
            return cte;

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
