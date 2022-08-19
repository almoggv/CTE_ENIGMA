package src.main.java.ui;
import main.java.component.MachineHandler;
import main.java.component.SerializationHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.component.impl.SerializationHandlerImpl;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import src.main.java.enums.MenuOptions;

import java.io.File;
import java.util.*;

import static java.lang.System.*;

public class Menu {

    private static MachineHandler machineHandler = new MachineHandlerImpl();
    private static SerializationHandler serializationHandler = new SerializationHandlerImpl();

    private  static List<String> QUIT_OPTION_VALUES = Arrays.asList("q", "back" , "quit" );
    private  static String QUIT_OPTION_MESSAGE = "Enter " + QUIT_OPTION_VALUES + " to quit current stage";

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
            out.println("Successfully set back to original state");
        }
        catch (Exception e){
            out.println("Something went wrong, : " + e.getMessage());
        }
    }

    private static void readSystemInfoFromFile(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the absolute path of xml schema to load:");
        String input = scanner.nextLine();;
        while(input.trim().isEmpty()){
            input = scanner.nextLine();;
        }
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
//            String rotorsPossibleInUseMsg = "Number of rotors in use: [" + inventoryInfo.get().getNumOfRotorsInUse() +"/"+ inventoryInfo.get().getNumOfAvailableRotors() + "].";
            String rotorsPossibleInUseMsg = "Number of rotors in use: " + inventoryInfo.get().getNumOfRotorsInUse() +", out of: "+ inventoryInfo.get().getNumOfAvailableRotors() + " available.";
            //2.2
            String reflectorsAmountMsg = "Number of available reflectors: " + inventoryInfo.get().getNumOfAvailableReflectors();
            //2.3
            String numberOfEncryptedMessagesMsg = "Number of messages encrypted: " + Menu.countNumberOfMessagesInHistory();
            //2.4
            Optional<MachineState> originalMachineState = Optional.of(machineHandler.getInitialMachineState().get());
            String originalMachineStateMsgHeader = "Original Machine State: ";
            String originalRotorsInMachineMsg = Menu.buildRotorsInMachineMsg(originalMachineState.get(),inventoryInfo.get());
            String originalRotorsHeadLocationInMachineMsg = Menu.buildRotorHeadLocationInMachineMsg(originalMachineState.get());
            String originalReflectorIdMsg = "<" + originalMachineState.get().getReflectorId().getName() + ">";
            String originalPlugsMappedMsg = Menu.buildPlugMappingInMachineMsg(originalMachineState.get());
            String originalMachineStateMsg = originalMachineStateMsgHeader + lineSeparator() + originalRotorsInMachineMsg + originalRotorsHeadLocationInMachineMsg + originalReflectorIdMsg+ originalPlugsMappedMsg;
            //2.5
            String machineStateMsgHeader = "Current Machine State: ";
                //2.5.a
            String rotorsInMachineMsg = Menu.buildRotorsInMachineMsg(machineState.get(),inventoryInfo.get());
                //2.5.b
            String rotorsHeadLocationInMachineMsg = Menu.buildRotorHeadLocationInMachineMsg(machineState.get());
                //2.5.c
            String reflectorIdMsg = "<" + machineState.get().getReflectorId().getName() + ">";
                //2.5.d
            String plugsMappedMsg = Menu.buildPlugMappingInMachineMsg(machineState.get());
            String machineStateMsg = machineStateMsgHeader + lineSeparator() +rotorsInMachineMsg+rotorsHeadLocationInMachineMsg+reflectorIdMsg+plugsMappedMsg;

            printedMessage = rotorsPossibleInUseMsg + lineSeparator()
                    + reflectorsAmountMsg + lineSeparator() + numberOfEncryptedMessagesMsg + lineSeparator()
                    + originalMachineStateMsg + lineSeparator() + machineStateMsg ;
            out.println(printedMessage);
        }
        catch(Exception e){
            System.out.println("Something went wrong, Error:" + e.getMessage());
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
        for (int i = machineState.getRotorsHeadsInitialValues().size()-1 ; i >= 0; i--) {
            String headlocation = machineState.getRotorsHeadsInitialValues().get(i);
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
        int id, notchDistance;
        for (int i = machineState.getRotorIds().size() - 1 ; i >= 0; i--) {
            id = machineState.getRotorIds().get(i);
            notchDistance = machineState.getNotchDistancesFromHead().get(i);
            currRotorMsg = id + "(" + notchDistance + ")";
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
                List<Integer> rotorIdsList = getRotorChoice(inventoryInfo.get());
                if(rotorIdsList == null) return;
            // success message - moved inside getRotorChoice function

            //rotor start pos
                String rotorStartingPos = getRotorStartingPositions(inventoryInfo.get());
                if(rotorStartingPos == null) return;
            // success message -  moved inside getRotorStartingPositions function

            //reflector
                ReflectorsId reflectorId = getReflectorChoice(inventoryInfo.get());
                if(reflectorId == null) return;
                out.println("Successfully Chose: "+ reflectorId.getName());

            //plugs
                List<MappingPair<String,String>> plugMapping = getPlugMapping(inventoryInfo.get());
                if(plugMapping == null) return;
                out.println("Successfully Chose: "+ plugMapping);
                try {
                    machineHandler.assembleMachine(reflectorId, rotorIdsList, rotorStartingPos, plugMapping);
                    System.out.println("Assembled the machine successfully");
                }
                catch (Exception e){
                    System.out.println("Failed to assemble the machine - " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            System.out.println("Problem assembling the machine: " + e.getMessage());
        }
    }

    private static List<MappingPair<String,String>> getPlugMapping(InventoryInfo inventoryInfo) {
        Scanner scanner = new Scanner(System.in);
        List<MappingPair<String,String>> plugMapping = new ArrayList<>();
        boolean isChoiceValid = false;
        String input = "";
        Optional<String> verifiedInput;
        String abc = inventoryInfo.getABC();
        int maxNumberOfPlugs = (int) Math.floor(abc.length()/2);
        while(!isChoiceValid){
            System.out.println("Please choose the plug connections, at most " + maxNumberOfPlugs + " pairs from :\"" + abc + "\"");
            System.out.println("e.g: \"adf!\" - meaning <A|D> and <F|!>");
            System.out.println(QUIT_OPTION_MESSAGE);
            input = scanner.nextLine();
            if(isUserRequestingToQuit(input)) return null;
            if(input.trim().length() == 0) return plugMapping;
            if(input.length()%2 != 0){
                out.println("An Odd number of connections was given");
                continue;
            }
            if( maxNumberOfPlugs < (int) Math.floor(input.length()/2)){
                out.println("Too many connections were given");
                continue;
            }
            verifiedInput = machineHandler.verifyInputInAbcAndFix(input);
            if(!verifiedInput.isPresent()){
                out.println("Not all given connections are in the ABC");
                continue;
            }
            input = verifiedInput.get();
            if(isALetterRepeatingItsSelf(input) != -1){
                int letterIndex = isALetterRepeatingItsSelf(input);
                out.println("The letter \"" + input.substring(letterIndex,letterIndex+1) + "\" is appearing more than once");
                continue;
            }
            for (int i = 0; i < input.length(); i = i + 2) {
                String leftLetter = input.substring(i,i+1),
                        rightLetter = input.substring(i+1,i+2);
                plugMapping.add(new MappingPair<>(leftLetter,rightLetter));
            }
            isChoiceValid = plugMapping.size() <= maxNumberOfPlugs && plugMapping.size() > 0;
        }
        return plugMapping;
    }

    private static int isALetterRepeatingItsSelf(String source){
        String currLetter;
        int foundIndex;
        for (int i = 0; i < source.length() ; i++) {
            currLetter = source.substring(i,i+1);
            foundIndex = source.indexOf(currLetter,source.indexOf(currLetter)+1);
            if(foundIndex > -1){
                return foundIndex;
            }
        }
        return -1;
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
        toPrint = toPrint.concat(QUIT_OPTION_MESSAGE);
        while (!isChoiceValid) {
            System.out.println(toPrint);
            input = scanner.nextLine();
            if(isUserRequestingToQuit(input)){
                return null;
            }
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
            System.out.println("e.g: AO! - meaning \"A\" is the starting position of the first rotor from the right");
            System.out.println(QUIT_OPTION_MESSAGE);
            input = scanner.nextLine();
            if(isUserRequestingToQuit(input)) return null;
            if(input.length() != rotorNumToAsk){
                out.println("You entered - "+ input.length() + " positions, Required - "+ rotorNumToAsk);
                continue;
            }
            verified = machineHandler.verifyInputInAbcAndFix(input);
            if(!verified.isPresent()){
                out.println("Not all given positions are in the ABC");
                continue;
            }
            isRotorStartingPosValid = input.length() == rotorNumToAsk && verified.isPresent();
        }
        out.println("Successfully Chose: "+ rotorStartingPos);
        //reverse string
        for (int j = 0; j < verified.get().length(); j++) {
            rotorStartingPos =  verified.get().substring(j,j+1) + rotorStartingPos;
        }

        return rotorStartingPos;
    }

    private static boolean isUserRequestingToQuit(String userInput){
        String input = userInput.trim().toLowerCase();
        for (String option : QUIT_OPTION_VALUES ) {
            if(option.equals(input)) return true;
        }
        return false;
    }

    private static List<Integer> getRotorChoice(InventoryInfo inventoryInfo) {
        boolean isRotorChoiceValid = false;
        List<Integer> rotorIdList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int rotorNumToAsk = inventoryInfo.getNumOfRotorsInUse();
        int rotorRange = inventoryInfo.getNumOfAvailableRotors();
        while (!isRotorChoiceValid) {
            System.out.println("Please choose " + rotorNumToAsk + " rotors from 1 to " + rotorRange + " seperated by commas");
            System.out.println("e.g: 45,27,94 - meaning \"45\" is the first rotor from the right");
            out.println(QUIT_OPTION_MESSAGE);
            String input = scanner.nextLine();
            if(isUserRequestingToQuit(input)){
                return null;
            }
            if(input.matches("[^0-9,]+")){
                System.out.println("Your input contains something other than numbers and commas");
                continue;
            }
            String[] rotors = input.split(",");
            if(rotors.length != rotorNumToAsk){
                System.out.println("You entered - " + rotors.length + " rotor IDs, Required - " + rotorNumToAsk);
                continue;
            }
            rotorIdList.clear();
            for (String scannedRotorId : rotors) {
                try {
                    int rotorId = Integer.parseInt(scannedRotorId);
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
        out.println("Successfully Chose: "+ rotorIdList + "\n");
        //reverse list
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter absolute path to save to:");
        String input = scanner.nextLine();;
        while(input.trim().isEmpty()){
            input = scanner.nextLine();;
        }
        try{
            serializationHandler.saveMachineHandlerToFile(input,machineHandler);
            System.out.println("Successfully saved complete state.");
        }
        catch (Exception e){
            System.out.println("Failed to save complete state to file, " + e.getMessage());
        }
    }

    private static void loadFromFile(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter absolute path to save to:");
        String input = scanner.nextLine();;
        while(input.trim().isEmpty()){
            input = scanner.nextLine();;
        }
        try{
            machineHandler = serializationHandler.loadMachineHandlerFromFile(input);
            System.out.println("Successfully loaded complete state.");
        }
        catch (Exception e){
            System.out.println("Failed to load complete state from file, " + e.getMessage());
        }

    }
}