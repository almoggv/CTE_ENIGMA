package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContestAllyTeamsPayload {
    String message;
    List<AllyTeamData> allyTeamsData;
}
