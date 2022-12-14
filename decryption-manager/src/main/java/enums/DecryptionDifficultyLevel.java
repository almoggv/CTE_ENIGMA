package enums;

public enum DecryptionDifficultyLevel {
    EASY("EASY"),
    INTERMEDIATE("INTERMEDIATE"),
    HARD("HARD"),
    IMPOSSIBLE("IMPOSSIBLE");

    private final String name;

    DecryptionDifficultyLevel(String name) {
        this.name = name;
    }

    public static DecryptionDifficultyLevel getByName(String name) {
        name = name.toUpperCase();
        switch (name) {
            case ("EASY"):
                return EASY;
            case ("INTERMEDIATE"):
            case ("MEDIUM"):
                return INTERMEDIATE;
            case ("HARD"):
                return HARD;
            case ("IMPOSSIBLE"):
            case ("INSANE"):
                return IMPOSSIBLE;
            default:
                throw new IllegalArgumentException();
        }
    }

    /*
    EASY =
        Known:
         - Reflector
         - Rotor IDs in use
         - Rotors orientation (which rotor in which location)
        To Find:
         - Rotor Starting Position

    INTERMEDIATE =
         Known:
         - Rotor IDs in use
         - Rotors orientation (which rotor in which location)
        To Find:
         - Reflector
         - Rotor Starting Position

    HARD =
        Known:
         - Rotor IDs in use
        To Find:
         - Rotors orientation (which rotor in which location)
         - Reflector
         - Rotor Starting Position

    IMPOSSIBLE =
        Known:
         - Nothing
        To Find:
         - Rotor IDs in use
         - Rotors orientation (which rotor in which location)
         - Reflector
         - Rotor Starting Position
     */
}
