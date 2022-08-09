package src.main.java.enums;

public enum MenuOptions {
    ReadSystemInfoFromFile("Read system info from file", 1),
    ShowMachineDetails("Show machine details", 2),
    AssembleMachineFromInput("Assemble machine from input",3),
    AssembleMachineRandomly("Assemble machine randomly",4),
    EncryptOrDecrypt("Encrypt/Decrypt",5),
    SeeMachineHistoryAndStatistics("See machine history and statistics",6),
    Exit("Exit",7),
    SaveOrLoadFromFile("SaveOrLoadFromFile",8);

    private String name;
    private int id;

    MenuOptions(String name, int id){
        this.name = name;
        this.id = id;
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
