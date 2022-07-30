package main.java.component;

import java.io.File;
import java.util.List;

public interface MachineHandler {

    public boolean loadMachineConfiguration(File file);

    //this overload loads the settings automatically
    public boolean setState();

    //bonus
    public boolean setState(File file);

    public boolean setState(Object... args);

    //bonus
    public boolean saveState(File file);

    public List<String> getMachineState();

    public boolean resetToLastSetState();

    public String sendInputToMachine(String input);

    public String getMachineHistory();

    public String getMachineStatistics();
 }
