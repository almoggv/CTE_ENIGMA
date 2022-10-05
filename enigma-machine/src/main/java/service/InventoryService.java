package service;

import lombok.Getter;
import lombok.Setter;
import component.Reflector;

import java.util.List;

public class InventoryService {
    @Getter @Setter
    private static List<Reflector> reflectorsInventory;
    @Getter @Setter private static Integer agentsInventory;
}
