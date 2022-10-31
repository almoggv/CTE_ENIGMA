package dto;

import generictype.MappingPair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllyWorkProgressPayload {
    String message;
    MappingPair<Long,Long> progress;
}
