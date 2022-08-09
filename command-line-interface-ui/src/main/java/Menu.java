package src.main.java;
import src.main.java.enums.MenuOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static java.lang.System.exit;
import static java.lang.System.lineSeparator;

public class Menu {
    public static String buildMenu(){
        String menuOptions = "What would you like to do?" + lineSeparator();
        int i = 1;
        for (MenuOptions option : MenuOptions.values()) {
            menuOptions = menuOptions.concat(i + ") " + option.getName() + System.lineSeparator());
            i++;
        }
        return menuOptions;
    }
}