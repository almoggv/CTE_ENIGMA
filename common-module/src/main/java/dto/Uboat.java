package dto;

import component.MachineHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Uboat {
    String name;
    MachineHandler machineHandler;
    String originalWord;
}
