package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import component.impl.IOWheelImpl;
import generictype.DeepCloneable;
import generictype.MappingPair;
import enums.ReflectorsId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineState implements DeepCloneable , Serializable {
    private ReflectorsId reflectorId;
    private List<Integer> rotorIds;
    private List<String> rotorsHeadsInitialValues;
    private List<MappingPair<String,String>> plugMapping;
    private List<Integer> notchDistancesFromHead;

    @Override
    public MachineState getDeepClone() {
        ReflectorsId recreatedReflectorsId = this.reflectorId;
        List<Integer> recreatedRotorIds = new ArrayList<>(rotorIds);
        List<String> recreatedRotorsHeadsInitialValues = new ArrayList<>(rotorsHeadsInitialValues);;
        List<MappingPair<String,String>> recreatedPlugMapping = new ArrayList<>(plugMapping);
        List<Integer> recreatedNotchDistancesFromHead = new ArrayList<>(notchDistancesFromHead);

        return new MachineState(recreatedReflectorsId,recreatedRotorIds,recreatedRotorsHeadsInitialValues,recreatedPlugMapping,recreatedNotchDistancesFromHead);
    }

}
