package component;

import java.io.FileNotFoundException;

public interface SerializationHandler {


    public MachineHandler loadMachineHandlerFromFile(String absolutePath) throws FileNotFoundException, Exception;

    public void saveMachineHandlerToFile(String fileName, MachineHandler machineHandlerToSave) throws Exception;

}
