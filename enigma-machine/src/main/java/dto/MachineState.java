package main.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.java.generictype.MappingPair;
import main.java.enums.ReflectorsId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineState {
    private ReflectorsId reflectorId;
    private List<Integer> rotorIds;
    private List<Integer> rotorsStartingPositions;
    private List<MappingPair<String,String>> plugMapping;
}
