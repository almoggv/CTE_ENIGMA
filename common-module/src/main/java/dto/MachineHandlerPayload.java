package dto;

import component.MachineHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineHandlerPayload {
    String message;
    MachineHandler machineHandler;
}
