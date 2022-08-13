package main.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.java.component.impl.IOWheelImpl;
import main.java.generictype.DeepCloneable;
import main.java.generictype.MappingPair;
import main.java.enums.ReflectorsId;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineState implements DeepCloneable {
    private ReflectorsId reflectorId;
    private List<Integer> rotorIds;
    private List<String> rotorsHeadsInitialValues;
    private List<MappingPair<String,String>> plugMapping;
    @Override
    public DeepCloneable getDeepClone() {

        ReflectorsId recreatedReflectorsId = this.reflectorId;
        List<Integer> recreatedRotorIds = new ArrayList<>(rotorIds);
        List<String> recreatedRotorsHeadsInitialValues = new ArrayList<>(rotorsHeadsInitialValues);;
        List<MappingPair<String,String>> recreatedPlugMapping = new ArrayList<>(plugMapping);;

        return new MachineState(recreatedReflectorsId,recreatedRotorIds,recreatedRotorsHeadsInitialValues,recreatedPlugMapping);
    }
}
