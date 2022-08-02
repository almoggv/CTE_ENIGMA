package main.java.component;

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

}
