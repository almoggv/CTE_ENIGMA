package main.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInfo {
    private int numOfRotorsInUse;
    private int numOfAvailableRotors;
    private Map<Integer, Integer> rotorIdToNotchLocation;
    private int numOfAvailableReflectors;
    private String ABC;
}
