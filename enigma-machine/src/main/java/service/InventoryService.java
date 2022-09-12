package main.java.service;

import lombok.Getter;
import lombok.Setter;
import main.java.component.Reflector;

import java.util.List;

public class InventoryService {
    @Getter @Setter
    private static List<Reflector> reflectorsInventory;
}
