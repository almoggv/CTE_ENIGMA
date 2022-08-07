package main.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionInfoHistory {
    private String input;
    private String output;
    private Duration timeToEncrypt;
    private MachineState machineStartingState;
}
