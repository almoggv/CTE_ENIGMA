package main.java.component;

import java.io.File;
import java.util.List;

public interface MachineHandler {

    public boolean loadMachineConfiguration(File file);

    //this overload loads the settings automatically
    public boolean setState();

    //bonus
    public boolean loadStateFromFile(String absolutePath);

    //TODO: arguments might change
    public boolean loadStateManually(PlugBoard plugBoard, Reflector reflector, List<Rotor> rotors);

    //bonus
    public boolean saveStateToFile(String fileName);

    public List<String> getMachineState();

    public boolean resetToLastSetState();

    public String sendInputToMachine(String input);

    public String getMachineHistory();

    public String getMachineStatistics();
 }
