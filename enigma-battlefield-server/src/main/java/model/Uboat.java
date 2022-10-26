package model;

import component.MachineHandler;
import lombok.*;


public class Uboat {
    @Getter @Setter
    String name;
    @Getter @Setter
    MachineHandler machineHandler;
    @Getter @Setter
    String originalWord;
}
