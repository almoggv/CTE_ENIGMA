package src.main.java.ui;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.enums.ReflectorsId;
import src.main.java.enums.MenuOptions;

import java.util.Scanner;
import static java.lang.System.exit;
import static java.lang.System.lineSeparator;

public class Menu {

    private static MachineHandler machineHandler = new MachineHandlerImpl();

    public static String buildMainMenu(){
        String menuOptions = "What would you like to do?" + lineSeparator();
        int i = 1;
        for (MenuOptions option : MenuOptions.values()) {
            menuOptions = menuOptions.concat(i + ") " + option.getName() + System.lineSeparator());
            i++;
        }
        return menuOptions;
    }

    public static void showMenu(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to C.T.E!");
        int choice = -1;
        String menuOptions = Menu.buildMainMenu();
        while (choice != MenuOptions.Exit.getId()){
            System.out.println(menuOptions);
            try{
                choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        readSystemInfoFromFile();
                        break;
                    case 2:
                        showMachineDetails();
                        break;
                    case 3:
                        //todo - step by step ask for parts
                        assembleMachineFromInput();
                        break;
                    case 4:
                        assembleMachineRandomly();
                        break;
                    case 5:
                        encryptOrDecrypt();
                        break;
                    case 6:
                        seeMachineHistoryAndStatistics();
                        break;
                    case 7:
                        exitSystem();
                        break;
                    case 8:
                        saveOrLoadFromFile();
                        break;
                }
            }
            catch (Exception exception){
                System.out.println(exception.getMessage());
            }
        }
    }

    private static void readSystemInfoFromFile(){
//        boolean success = false;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the absolute path of xml schema to load:");
        String input = scanner.nextLine();

        //todo - should this return optional

        //todo- if bad load - keep last working

//            String relativePath = "enigma-machine/src/main/resources/ex1-sanity-small.xml";
        try {
            machineHandler.buildMachinePartsInventory(input);
//                machineHandler.buildMachinePartsInventory(relativePath);
//                success = true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private static void showMachineDetails(){
        System.out.println(machineHandler.getInventoryInfo());
        //todo = fix state problem
//        System.out.println(machineHandler.getMachineState());
    }


    private static void assembleMachineFromInput(){
        Scanner scanner = new Scanner(System.in);
        int rotorNumToAsk = machineHandler.getInventoryInfo().getNumOfRotorsInUse();
        int rotorRange = machineHandler.getInventoryInfo().getNumOfAvailableRotors();
        System.out.println("Please choose " + rotorNumToAsk + " rotors from 1 to " + rotorRange + " seperated by commas") ;
        System.out.println("e.g: 45,27,94") ;
        String input = scanner.next();

        String abc = machineHandler.getInventoryInfo().getABC();
        System.out.println("Please choose rotors starting positions (from machine ABC: " + abc +")");
        System.out.println("e.g: AO! ");
        input = scanner.next();

        System.out.println("Please choose a reflector from the following: ");
        String reflectorOptions = "Available reflectors:" + lineSeparator();

        int reflectorsRange = machineHandler.getInventoryInfo().getNumOfAvailableRotors();
        int i = 1;
        for (int j = 0; j < reflectorsRange; j++) {
            reflectorOptions = reflectorOptions.concat(i + ") " + ReflectorsId.getByNum(i).getName() + lineSeparator());
            i++;
        }
        System.out.println(reflectorOptions);
        input = scanner.next();

    }

    private static void  assembleMachineRandomly(){
        machineHandler.assembleMachine();
    }

    private static void encryptOrDecrypt() {
        System.out.println("please enter string to encrypt/decrypt:");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();

        String output = machineHandler.encrypt(input);
        System.out.println("Encrypted to: " + output);
    }

    private static void seeMachineHistoryAndStatistics(){
        System.out.println(machineHandler.getMachineStatisticsHistory());
    }
    private static void exitSystem(){
        exit(0);
    }
    private static void saveOrLoadFromFile(){}
}