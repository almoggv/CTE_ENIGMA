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

    @Override
    public String toString(){
        String result = "";
        if(this.rotorIds == null || rotorIds.isEmpty()
                || rotorsHeadsInitialValues == null || rotorsHeadsInitialValues.isEmpty()
                || notchDistancesFromHead == null || notchDistancesFromHead.isEmpty()){
            return super.toString();
        }
        // Rotors Ids
        String rotorsIdsSegment ="";
        for ( Integer id: rotorIds) {
            rotorsIdsSegment += ( String.valueOf(id) + ",");
        }
        rotorsIdsSegment = "<" + rotorsIdsSegment.substring(0,rotorsIdsSegment.length()-1) + ">";
        // Rotors Positions:
        String rotorsPositionsSegment = "";
        for (int i = 0; i < rotorsHeadsInitialValues.size(); i++) {
            rotorsPositionsSegment += rotorsHeadsInitialValues.get(i) + "(" + notchDistancesFromHead.get(i) + ")," ;
        }
        rotorsPositionsSegment = "<" + rotorsPositionsSegment.substring(0,rotorsPositionsSegment.length()-1) + ">";
        //Reflector
        String reflectorSegment = "<" + reflectorId.name() + ">";
        //Plugboard
        String plugboardSegment = "";
        for (MappingPair<String,String> connection : plugMapping ) {
            plugboardSegment += connection.getLeft() + "|" + connection.getRight() + ",";
        }
        if(!plugboardSegment.isEmpty()){
            plugboardSegment += "<" + plugboardSegment.substring(0,plugboardSegment.length()-1) + ">";
        }
        result = rotorsIdsSegment + rotorsPositionsSegment + reflectorSegment + plugboardSegment;
        return result;
    }

}















