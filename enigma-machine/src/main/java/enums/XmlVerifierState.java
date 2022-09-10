package main.java.enums;

public enum XmlVerifierState {
    VALID("Valid"),
    ERROR_ABC_COUNT_ODD("The number of letters in the ABC is odd"),
    ERROR_BAD_ROTOR_COUNT("The Rotor Count is Illegal"),
    ERROR_ILLEGAL_ROTOR_ID("The Rotor ids are not sequential"),
    ERROR_ILLEGAL_ROTOR_MAPPING("The mapping of one of the Rotors in the schema is illegal"),
    ERROR_ILLEGAL_NOTCH_LOCATION("One of the Rotor notches is out of the ABC's bounds"),
    ERROR_ILLEGAL_REFLECTOR_ID("The Reflector ids are not sequential or too many"),
    ERROR_ILLEGAL_REFLECTOR_MAPPING("The mapping of one of the Reflectors in the schema is illegal"),

    ERROR_ILLEGAL_AGENTS_NUMBER("The agent's number in the schema is illegal");

    private String msg;

    XmlVerifierState(String msg) {
        this.msg = msg;
    }

    public String toString() {
        return msg;
    }

    public String getMessage(){return msg;}
}
