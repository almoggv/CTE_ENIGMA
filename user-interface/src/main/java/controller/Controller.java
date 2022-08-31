package src.main.java.controller;

import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;

public interface Controller {
    public void setParentController(AppController parentController);

    void setMachineHandler(MachineHandler machineHandler);
}
