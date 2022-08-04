package main.java;

import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;

public class Main {
    public static void main(String[] args) {
        MachineHandler machineHandler =new MachineHandlerImpl();
        String abolutePath = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\ex1-sanity-small.xml";
        machineHandler.loadMachineConfiguration(abolutePath);
//        imp.loadMachineConfiguration("C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\ex1-error-3.xml");
//        imp.loadMachineConfiguration("C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\ex1-sanity-paper-enigma.xml");


    }
}
