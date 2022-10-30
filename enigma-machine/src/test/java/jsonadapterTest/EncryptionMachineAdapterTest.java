package jsonadapterTest;

import com.google.gson.Gson;
import component.*;
import component.impl.*;
import generated.CTEEnigma;
import jsonadapter.EncryptionMachineJsonAdapter;
import org.junit.Assert;
import org.junit.Test;
import service.XmlFileLoader;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EncryptionMachineAdapterTest {

    public final static String SCHEMA_FILE = "\\machine-inventory-schema-Ex3\\ex3-basic.xml";

    @Test
    public void serializationTest(){
        EncryptionMachine encryptionMachine = createMachine();
        Gson gson = EncryptionMachineJsonAdapter.buildGsonLoginPayloadAdapter();
        String serializedMachine = gson.toJson(encryptionMachine);
        EncryptionMachine newMachine = gson.fromJson(serializedMachine,EncryptionMachine.class);
        Assert.assertTrue(newMachine!=null || newMachine.getABC()!=null || !newMachine.getABC().equals(""));
    }

    private EncryptionMachine createMachine(){
        //Create the machine
        File resourcesDirectory = new File("src/main/resources");
        String schemaFileName = resourcesDirectory.getAbsolutePath() + SCHEMA_FILE;
        CTEEnigma cteEnigma = XmlFileLoader.fromXmlFileToCTE(schemaFileName);
        PlugBoard plugBoard = new PlugBoardImpl(cteEnigma.getCTEMachine().getABC());
        Reflector reflector = new ReflectorImpl(cteEnigma.getCTEMachine().getCTEReflectors().getCTEReflector().get(0));
        Rotor rotor1 = new RotorImpl(cteEnigma.getCTEMachine().getCTERotors().getCTERotor().get(0),cteEnigma.getCTEMachine().getABC());
        Rotor rotor2 = new RotorImpl(cteEnigma.getCTEMachine().getCTERotors().getCTERotor().get(1),cteEnigma.getCTEMachine().getABC());
        IOWheel wheel = new IOWheelImpl(cteEnigma.getCTEMachine().getABC());
        List<Rotor> rotors = new ArrayList<>(Arrays.asList(rotor1,rotor2)) ;
        EncryptionMachine encryptionMachine = new EnigmaMachine();
        encryptionMachine.buildMachine(plugBoard,reflector,rotors,wheel);
        return encryptionMachine;
    }

}
