package main.java.agent;

import main.java.dto.MachineState;

import java.util.List;

public interface DecryptionAgent extends Runnable {

    void assignWork(List<MachineState> startingConfigurations, String inputToDecrypt);
}
