package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentData {
    String name;
    Integer numberOfThreads;
    Integer numberOfTasksThatTakes;
    String allyName;
//    private ContestRoom contestRoom;
}
