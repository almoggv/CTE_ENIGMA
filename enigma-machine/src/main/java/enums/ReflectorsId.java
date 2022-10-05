package enums;

public enum ReflectorsId {
    I("I", 1),
    II("II", 2),
    III("III",3),
    IV("IV",4),
    V("V",5);

    private String name;
    private int id;

    ReflectorsId(String name, int id){
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

    public static ReflectorsId getByNum(int num){
        switch (num) {
            case (1):
                return I;
            case (2):
                return II;
            case (3):
                return III;
            case (4):
                return IV;
            case (5):
                return V;
            default:
                throw new IllegalArgumentException();
        }
    }
}
