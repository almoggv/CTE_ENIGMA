package src.main.java.ui;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import src.main.java.enums.MenuOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static java.lang.System.*;

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

        try {
            machineHandler.buildMachinePartsInventory(input);
            System.out.println("File Loaded Successfully");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private static void showMachineDetails(){
        try{
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            Optional<MachineState> machineState = machineHandler.getMachineState();
            if(!inventoryInfo.isPresent()){
                System.out.println("No Parts in inventory to display, please " + MenuOptions.ReadSystemInfoFromFile.toString() + " first.");
            }
            if(!machineState.isPresent()){
                System.out.println("No machine state to display, please assemble the machine first");
            }
            String printedMessage = "";
            //2.1
            String rotorsPossibleInUseMsg = "Rotors: [" + inventoryInfo.get().getNumOfRotorsInUse()+inventoryInfo.get().getNumOfAvailableRotors()+"]";
            //2.2
            String NotchLocationsMsg = "Notches locations by rotor id:" + inventoryInfo.get().getRotorIdToNotchLocation();
            //2.3
            String reflectorsAmountMsg = "Number of reflectors: " + inventoryInfo.get().getNumOfAvailableReflectors();
            //2.4
            String numberOfEncryptedMessagesMsg = "Number of messages encrypted: " + Menu.countNumberOfMessagesInHistory();
            //2.5
            String machineStateMsg = "current Machine State: ";
                //2.5.a
            String rotorsInMachineMsg = Menu.buildRotorsInMachineMsg(machineState.get());
                //2.5.b
            String rotorsHeadLocationInMachineMsg = Menu.buildRotorHeadLocationInMachineMsg(machineState.get());
                //2.5.c
            String reflectorIdMsg = "<" + machineState.get().getReflectorId().getName() + ">";
                //2.5.d
            String plugsMappedMsg = Menu.buildPlugMappingInMachineMsg(machineState.get());

            printedMessage = rotorsPossibleInUseMsg + "\n" + NotchLocationsMsg + "\n"
                    + reflectorsAmountMsg + "\n" + numberOfEncryptedMessagesMsg + "\n"
                    + machineStateMsg + "\n" + rotorsInMachineMsg+rotorsHeadLocationInMachineMsg+reflectorIdMsg+plugsMappedMsg;
            out.println(printedMessage);
        }
        catch(Exception e){
            System.out.println("Please load a machine from file first" + e.getMessage());
        }
    }

    private static String buildPlugMappingInMachineMsg(MachineState machineState) {
        String outMsg = "<";
        String tempMsg = "";
        boolean isFirst = true;
        for (MappingPair<String,String> mappingPair : machineState.getPlugMapping()) {
            tempMsg = mappingPair.getLeft() + "|" + mappingPair.getRight();
            if(isFirst){
                isFirst = false;
            }
            else{
                tempMsg = "," + tempMsg;
            }
            outMsg = tempMsg.concat(tempMsg);
        }
        outMsg = tempMsg.concat(">");
        return outMsg;
    }

    private static String buildRotorHeadLocationInMachineMsg(MachineState machineState) {
        String outMsg = "<";
        for (String headlocation: machineState.getRotorsHeadsInitialValues()) {
            outMsg = outMsg.concat(headlocation);
        }
        outMsg = outMsg.concat(">");
        return outMsg;
    }

    private static int countNumberOfMessagesInHistory() {
        if(machineHandler.getMachineStatisticsHistory().size() <= 0){
            return 0;
        }
        int resultCount = 0;
        for (MachineState key : machineHandler.getMachineStatisticsHistory().keySet()) {
            resultCount += machineHandler.getMachineStatisticsHistory().get(key).size();
        }
        return resultCount;
    }

    private static String buildRotorsInMachineMsg(MachineState machineState){
        String outmsg = "<";
        boolean isfirst = true;
        for (Integer currId : machineState.getRotorIds()) {
            if(isfirst){
                outmsg = outmsg.concat(currId.toString());
                isfirst = false;
            }
            else{
                outmsg = outmsg.concat("," + currId.toString());
            }
        }
        outmsg = outmsg.concat(">");
        return outmsg;
    }

    private static void assembleMachineFromInput(){
        Scanner scanner = new Scanner(System.in);
//        int rotorNumToAsk = machineHandler.getInventoryInfo().getNumOfRotorsInUse();
//        int rotorRange = machineHandler.getInventoryInfo().getNumOfAvailableRotors();
//        System.out.println("Please choose " + rotorNumToAsk + " rotors from 1 to " + rotorRange + " seperated by commas") ;
//        System.out.println("e.g: 45,27,94") ;
//        String input = scanner.nextLine();
//
//        String abc = machineHandler.getInventoryInfo().getABC();
//        System.out.println("Please choose rotors starting positions (from machine ABC: " + abc +")");
//        System.out.println("e.g: AO! ");
//        input = scanner.nextLine();
//
//        System.out.println("Please choose a reflector from the following: ");
//        String reflectorOptions = "Available reflectors:" + lineSeparator();
//
//        int reflectorsRange = machineHandler.getInventoryInfo().getNumOfAvailableRotors();
//        int i = 1;
//        for (int j = 0; j < reflectorsRange; j++) {
//            reflectorOptions = reflectorOptions.concat(i + ") " + ReflectorsId.getByNum(i).getName() + lineSeparator());
//            i++;
//        }
//        System.out.println(reflectorOptions);
//        input = scanner.nextLine();

    }

    private static void  assembleMachineRandomly(){
        machineHandler.assembleMachine();
    }

    private static void encryptOrDecrypt() {
        System.out.println("please enter string to encrypt/decrypt:");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

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