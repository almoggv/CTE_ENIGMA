package dto;

import enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStatePayload {
    String message;
    GameStatus gameState;
    String winner;
}
