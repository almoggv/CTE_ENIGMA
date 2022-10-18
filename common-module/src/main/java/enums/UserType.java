package enums;

public enum UserType {
    UBOAT("uboat"),
    ALLY("ally"),
    AGENT("agent");

    private final String name;
    UserType(String name) {
        this.name = name;
    }

    public static UserType getByName(String name) {
        name = name.toUpperCase();
        switch (name) {
            case ("UBOAT"):
                return UBOAT;
            case ("ALLY"):
                return ALLY;
            case ("AGENT"):
                return AGENT;
            default:
                throw new IllegalArgumentException();
        }
    }
}
