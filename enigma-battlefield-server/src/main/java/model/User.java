package model;

import enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private String token;
    private UserType type;
    private boolean isInARoom;
    private ContestRoom contestRoom;
    private boolean isReady;
    private boolean sentGotWin;

}
