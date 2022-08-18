package main.java.component.impl;

import main.java.component.MachineHandler;
import main.java.component.SerializationHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sun.misc.ClassLoaderUtil;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

public class SerializationHandlerImpl implements SerializationHandler {

    private static final Logger log = Logger.getLogger(SerializationHandlerImpl.class);

    static {
        try {
//        String log4JPropertyFile = ("../log4j.properties");
          String log4JPropertyFile = "./src/main/resources/log4j.properties";
          Properties p = new Properties();
          p.load(new FileInputStream(log4JPropertyFile));
          PropertyConfigurator.configure(p);      //Dont forget here
          log.debug("Logger Instatiated for : " + SerializationHandlerImpl.class.getSimpleName());
        } catch (IOException e) {
            //TODO: ?
        }
    }

    @Override
    public MachineHandler loadMachineHandlerFromFile(String absolutePath) throws Exception {
        if(absolutePath == null){
            log.error("Failed to load state from file , given file name is null");
            throw new NullPointerException("Failed to load from file, given file name is null");
        }
        if(!isPathAbsolute(absolutePath)){
            log.error("Failed to load state from file , path is not absolute");
            throw new IllegalArgumentException("Given path is not absolute :" + absolutePath);
        }
        if(!isPathValid(absolutePath)){
            log.error("Failed to save to file, file doesnt exist :" + absolutePath);
            throw new FileNotFoundException("file name doesnt exist");
        }
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        MachineHandler resultHandler;
        try{
            fileInputStream = new FileInputStream(absolutePath);
            objectInputStream = new ObjectInputStream(fileInputStream);
            resultHandler = (MachineHandler)objectInputStream.readObject();
            objectInputStream.close();
            return resultHandler;
        }
        catch (Exception e){
            log.error("Failed to load state from \"" + absolutePath +"\" , something went wrong" + e.getMessage());
            throw new Exception("failed to load state from: \"" + absolutePath + "\" ," + e.getMessage());
        }
    }

    @Override
    public void saveMachineHandlerToFile(String fileName, MachineHandler machineHandlerToSave) throws Exception {
        if(!isPathAbsolute(fileName)){
            fileName = "./"+fileName;
        }
        if(!machineHandlerToSave.getInventoryInfo().isPresent()){
            log.error("Failed to save state to file , no schema lodaded yet");
            throw new Exception("Cannot save to file, no schema loaded yet");
        }
        if(!machineHandlerToSave.getMachineState().isPresent()){
            log.error("Failed to save state to file , no machine setup yet");
            throw new Exception("Cannot save to file, machine not assembled yet");
        }
        if(fileName == null){
            log.error("Failed to save state to file , given file name is null");
            throw new NullPointerException("Failed to save to file, given file name is null");
        }
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(machineHandlerToSave);
            objectOutputStream.close();
        }
        catch(Exception e){
            log.error("Failed to save state to file, something went wrong :" + e.getMessage());
            throw e;
        }
    }

    private boolean isPathValid(String path){
        try{
            Paths.get(path);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    private boolean isPathAbsolute(String path){
        File file = new File(path);
        if (file.isAbsolute()) {
            return true;
        }
        return false;
    }

}
