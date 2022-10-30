package dto;

import component.IOWheel;
import component.PlugBoard;
import component.Reflector;
import component.Rotor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryComponents {
    private PlugBoard plugBoardInventory;
    private List<Rotor> rotorsInventory;
    private IOWheel ioWheelInventory;
    private List<Reflector> reflectorsInventory;
    private int expectedNumOfRotors;

}
