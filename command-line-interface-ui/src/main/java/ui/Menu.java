package src.main.java.ui;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import src.main.java.enums.MenuOptions;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.*;

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
            System.out.println(lineSeparator() + menuOptions);
            try{
                choice = Integer.parseInt(scanner.nextLine());
                MenuOptions option = MenuOptions.getByNum(choice);
                switch (option){
                    case ReadSystemInfoFromFile:
                        readSystemInfoFromFile();
                        break;
                    case ShowMachineDetails:
                        showMachineDetails();
                        break;
                    case AssembleMachineFromInput:
                        //todo - step by step ask for parts
                        assembleMachineFromInput();
                        break;
                    case AssembleMachineRandomly:
                        assembleMachineRandomly();
                        break;
                    case Encrypt:
                        encryptOrDecrypt();
                        break;
                    case SeeMachineHistoryAndStatistics:
                        seeMachineHistoryAndStatistics();
                        break;
                    case ResetToLastSetting:
                        resetToLastSetting();
                        break;
                    case SaveToFile:
                        saveToFile();
                        break;
                    case LoadFromFile:
                        loadFromFile();
                        break;
                    case Exit:
                        exitSystem();
                        break;
                    default:
                        out.println("Not an option. please choose from menu");
                }
            }
            catch (IllegalArgumentException e){
                out.println("Please only choose option from the menu.");
            }
            catch (Exception exception){
                System.out.println(exception.getMessage());
            }
        }
    }

    private static void resetToLastSetting() {
        try{
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            Optional<MachineState> machineState = machineHandler.getMachineState();
            if(!inventoryInfo.isPresent()){
                System.out.println("No Parts in inventory to display, please " + MenuOptions.ReadSystemInfoFromFile.toString() + " first.");
                return;
            }
            if(!machineState.isPresent()){
                System.out.println("No machine state to display, please assemble the machine first");
                return;
            }
            machineHandler.resetToLastSetState();
        }
        catch (Exception e){
            out.println(e.getMessage());
        }
    }

    private static void readSystemInfoFromFile(){
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
            //todo fix - prints <> with issues occasionally, : <2,3><FB><II>B|D> or ><II>>
            // aviad changed how he wants to print : <45(2),27(5),94(20)><AO!><III><A|Z,D|E>
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            Optional<MachineState> machineState = machineHandler.getMachineState();
            if(!inventoryInfo.isPresent()){
                System.out.println("No Parts in inventory to display, please " + MenuOptions.ReadSystemInfoFromFile.toString() + " first.");
                return;
            }
            if(!machineState.isPresent()){
                System.out.println("No machine state to display, please assemble the machine first");
                return;
            }
            String printedMessage = "Machine details:";
            //2.1
            //1.	כמות גלגלים אפשרית, כמות גלגלים בשימוש (למשל 3/5)
            String rotorsPossibleInUseMsg = "Number of rotors in use: [" + inventoryInfo.get().getNumOfRotorsInUse() +"/"+ inventoryInfo.get().getNumOfAvailableRotors() + "].";
            //2.2
            String NotchLocationsMsg = "Notches locations by rotor id:" + inventoryInfo.get().getRotorIdToNotchLocation();
            //2.3
            String reflectorsAmountMsg = "Number of available reflectors: " + inventoryInfo.get().getNumOfAvailableReflectors();
            //2.4
            String numberOfEncryptedMessagesMsg = "Number of messages encrypted: " + Menu.countNumberOfMessagesInHistory();
            //2.5
            String machineStateMsg = "current Machine State: ";
                //2.5.a
            String rotorsInMachineMsg = Menu.buildRotorsInMachineMsg(machineState.get(),inventoryInfo.get());
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
            System.out.println("Please load a machine from file first. " + e.getMessage());
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
            outMsg = outMsg.concat(tempMsg);
        }
        outMsg = outMsg.concat(">");
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

    private static String buildRotorsInMachineMsg(MachineState machineState,InventoryInfo inventoryInfo){
        String outmsg = "<";
        String currRotorMsg = "";
        boolean isfirst = true;
        for (Integer id : machineState.getRotorIds() ) {
            currRotorMsg = id.toString() + "(" + inventoryInfo.getRotorIdToNotchLocation().get(id) + ")";
            if(isfirst){
                isfirst = false;
            }
            else{
                currRotorMsg = "," + currRotorMsg;
            }
            outmsg = outmsg.concat(currRotorMsg);
        }
        outmsg = outmsg.concat(">");
        return outmsg;
    }

    private static void assembleMachineFromInput(){
        try {
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            if(!inventoryInfo.isPresent()){
                System.out.println("No machine loaded, please read system info from file first (option 1)");
            }
            else {
               //rotor choice
//                List<Integer> rotorIdsList = getRotorChoice(inventoryInfo.get());
//                out.println(rotorIdsList);

//              //rotor start pos
//                String rotorStartingPos = getRotorStartingPositions(inventoryInfo.get());
//                out.println(rotorStartingPos);

//              //reflector
//                ReflectorsId reflectorId = getReflectorChoice(inventoryInfo.get());
//                out.println(reflectorId.getName());
                //plugs
                List<MappingPair<String,String>> plugMapping = getPlugMapping(inventoryInfo.get());

//                machineHandler.assembleMachine(reflectorId, rotorIdsList, rotorStartingPos,plugMapping) ;
            }
        }
        catch (Exception e) {
            System.out.println("Problem assembling the machine: " + e.getMessage());
        }

    }

    private static List<MappingPair<String,String>> getPlugMapping(InventoryInfo inventoryInfo) {
        List<MappingPair<String,String>> plugMapping = new ArrayList<>();
        //todo
        return plugMapping;
    }

    private static ReflectorsId getReflectorChoice(InventoryInfo inventoryInfo) {
        Scanner scanner = new Scanner(System.in);
        boolean isChoiceValid = false;
        String input= "";
        String toPrint = "Please choose a reflector from the following reflectors: (by entering the corresponding number) " + lineSeparator();
        int reflectorsRange = inventoryInfo.getNumOfAvailableReflectors();
        int i = 1; int reflectorId = 0;
        for (int j = 1; j <= reflectorsRange; j++) {
            toPrint = toPrint.concat(i++ + ") " + ReflectorsId.getByNum(j).getName() + lineSeparator());
        }
        while (!isChoiceValid) {
            System.out.println(toPrint);
            input = scanner.nextLine();
            try {
                reflectorId = Integer.parseInt(input);
                if (reflectorsRange < reflectorId || reflectorId <= 0) {
                    out.println("Reflector is not available.");
                } else isChoiceValid = true;
            } catch (NumberFormatException e) {
                out.println("Please enter a number as requested.");
            }
        }
        return ReflectorsId.getByNum(reflectorId);
    }
    private static String getRotorStartingPositions(InventoryInfo inventoryInfo) {
        boolean isRotorStartingPosValid = false;
        String rotorStartingPos = "";
        String abc = inventoryInfo.getABC();
        Scanner scanner = new Scanner(System.in);
        int rotorNumToAsk = inventoryInfo.getNumOfRotorsInUse();
        String input ="";
        Optional<String> verified = null;
        while (! isRotorStartingPosValid) {
            System.out.println("Please choose " + rotorNumToAsk + " rotors starting positions (from machine ABC: " + abc +")");
            System.out.println("e.g: AO! ");
            input = scanner.nextLine();
            if(input.length() != rotorNumToAsk){
                out.println("Number of entered positions is not "+ rotorNumToAsk);
            }

            verified = machineHandler.verifyInputInAbcAndFix(input);
            if(!verified.isPresent()){
                out.println("Entered position not in abc ("+ abc +")");
            }
            isRotorStartingPosValid = input.length() == rotorNumToAsk && verified.isPresent();
        }

        for (int j = 0; j < verified.get().length(); j++) {
            rotorStartingPos =  verified.get().substring(j,j+1) + rotorStartingPos;
        }

        return rotorStartingPos;
    }

    private static List<Integer> getRotorChoice(InventoryInfo inventoryInfo) {
        boolean isRotorChoiceValid = false;
        List<Integer> rotorIdList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int rotorNumToAsk = inventoryInfo.getNumOfRotorsInUse();
        int rotorRange = inventoryInfo.getNumOfAvailableRotors();
        while (!isRotorChoiceValid) {
            System.out.println("Please choose " + rotorNumToAsk + " rotors from 1 to " + rotorRange + " seperated by commas");
            System.out.println("e.g: 45,27,94");
            String input = scanner.nextLine();
            String[] rotors = input.split(",");
            //at the moment - if problem - dont accept any - can change but needs more logic if so
            rotorIdList.clear();
            for (String rotor : rotors) {
                try {
                    int rotorId = Integer.parseInt(rotor);
                    if (rotorRange < rotorId || rotorId <= 0){
                        out.println("The rotor: " + rotorId + " is not in expected range (1-" +rotorRange +")"  );
                        break;
                    }
                    if (rotorIdList.contains(rotorId)) {
                        out.println("The rotor: " + rotorId + " was already chosen.");
                        break;
                    }
                    rotorIdList.add(rotorId);
                }
                catch (NumberFormatException e) {
                    out.println("Please enter rotors in requested format");
                }
            }
            isRotorChoiceValid = rotorIdList.size() == rotorNumToAsk;
        }
        for (int i = 0, j = rotorIdList.size() - 1; i < j; i++) {
            rotorIdList.add(i, rotorIdList.remove(j));
        }
        return rotorIdList;
    }

    private static void  assembleMachineRandomly(){
        try {
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            if(!inventoryInfo.isPresent()){
                System.out.println("No machine loaded, please read system info from file first (option 1)");
                return;
            }
            machineHandler.assembleMachine();
            System.out.println("Machine assembled successfully.");
        }
        catch (Exception e){
            System.out.println("Problem assembling the machine: " + e.getMessage());
        }
    }

    private static void encryptOrDecrypt() {
        try {
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            Optional<MachineState> machineState = machineHandler.getMachineState();
            if(!inventoryInfo.isPresent()){
                System.out.println("No machine loaded, please read system info from file first (option 1)");
            }
            if(!machineState.isPresent()){
                System.out.println("No machine state, please assemble the machine first (options 3/4)");
            }
            else {
                try{
                System.out.println("Please enter string to encrypt/decrypt:");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                String output = machineHandler.encrypt(input);
                System.out.println("The input: " + input + " was encrypted to: " + output);
                }
                catch (Exception e){
                    out.println(e.getMessage());
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void seeMachineHistoryAndStatistics(){
        try {
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            Optional<MachineState> machineState = machineHandler.getMachineState();
            if(!inventoryInfo.isPresent()){
                System.out.println("No machine loaded, please read system info from file first (option 1)");
            }
            if(!machineState.isPresent()){
                System.out.println("No machine state, please assemble the machine first (options 3/4)");
            }
            else {
                out.println("Machine history:");
                Map<MachineState, List<EncryptionInfoHistory>> history = machineHandler.getMachineStatisticsHistory();

                String machineStateMsg = "";
                for (MachineState machineStateHistory : history.keySet()) {
                    out.println(lineSeparator() + "Machine state:");
                    String rotorsInMachineMsg = Menu.buildRotorsInMachineMsg(machineStateHistory,inventoryInfo.get());
                    String rotorsHeadLocationInMachineMsg = Menu.buildRotorHeadLocationInMachineMsg(machineStateHistory);
                    String reflectorIdMsg = "<" + machineStateHistory.getReflectorId().getName() + ">";
                    String plugsMappedMsg = Menu.buildPlugMappingInMachineMsg(machineStateHistory);

                    machineStateMsg = rotorsInMachineMsg + rotorsHeadLocationInMachineMsg + reflectorIdMsg + plugsMappedMsg;
                    out.println(machineStateMsg);
                    int i = 1;
                    List<EncryptionInfoHistory> encryptionInfoHistoryList = history.get(machineStateHistory);
                    out.println("Machine encryption history:");
                    for (EncryptionInfoHistory encryptionHistory : encryptionInfoHistoryList) {
                        out.println(i++ + ". <" + encryptionHistory.getInput() + "> --> <" + encryptionHistory.getOutput() + "> ( " + encryptionHistory.getTimeToEncrypt().toNanos() + " nano-seconds)");
                    }
                }
            }
        }
        catch (Exception e){
            out.println("Problem showing history: "+ e.getMessage());
        }
    }
    private static void exitSystem(){
        out.println("BYE-BYE :)");
        exit(0);
    }
    private static void saveToFile(){

    }
    private static void loadFromFile(){

    }
}