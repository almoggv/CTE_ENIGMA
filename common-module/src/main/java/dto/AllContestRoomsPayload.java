package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllContestRoomsPayload {
    private String message;
    private Set<ContestRoomData> contestRooms;
}
