package model;

import component.MachineHandler;
import dto.EncryptionCandidate;
import lombok.*;

import java.util.List;


public class Uboat {
    @Getter @Setter String name;
    @Getter @Setter MachineHandler machineHandler;
    @Getter @Setter String originalWord;

}
