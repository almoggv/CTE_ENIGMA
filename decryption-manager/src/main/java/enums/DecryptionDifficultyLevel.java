package main.java.enums;

public enum DecryptionDifficultyLevel {
    EASY,
    INTERMEDIATE,
    HARD,
    IMPOSSIBLE

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
