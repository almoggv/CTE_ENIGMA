package src.main.java.enums;

public enum MenuOptions {
    ReadSystemInfoFromFile("Read system info from file", 1),
    ShowMachineDetails("Show machine details", 2),
    AssembleMachineFromInput("Assemble machine from input",3),
    AssembleMachineRandomly("Assemble machine randomly",4),
    Encrypt("Encrypt/Decrypt",5),
    ResetToLastSetting("Reset to last defined Settings",6),
    SeeMachineHistoryAndStatistics("See machine history and statistics",7),
    SaveToFile("Save to file",8),
    LoadFromFile("Load from file",9),
    Exit("Exit",10);

    private String name;
    private int id;

    MenuOptions(String name, int id){
        this.name = name;
        this.id = id;
    }
    public static MenuOptions getByNum(int num){
        switch (num) {
            case (1):
                return ReadSystemInfoFromFile;
            case (2):
                return ShowMachineDetails;
            case (3):
                return AssembleMachineFromInput;
            case (4):
                return AssembleMachineRandomly;
            case (5):
                return Encrypt;
            case(6):
                return ResetToLastSetting;
            case(7):
                return SeeMachineHistoryAndStatistics;
            case(8):
                return SaveToFile;
            case(9):
                return LoadFromFile;
            case(10):
                return Exit;
            default:
                throw new IllegalArgumentException("Please only choose option from the menu.");
        }
    }
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Integer getId(){
        return id;
    }
}
