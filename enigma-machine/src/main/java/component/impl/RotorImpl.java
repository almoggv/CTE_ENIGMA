package main.java.component.impl;

import javafx.util.Pair;
import main.java.component.Rotor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RotorImpl implements Rotor {

//    private List<Pair<Integer,Integer>> rotorMapping = new ArrayList<>();
//    private Map<Integer,Integer> rotorMapping;
    private List<String> leftList = new ArrayList<>();  //ABCDEF
    private List<String> rightList = new ArrayList<>(); //BCDEFA

    private int headPosition;

    private int notchLocation;

    @Override
//    public int getLeftValue(String inValue) {
////        int out = rightList.indexOf(inValue);
////        return out;
////        return rotorMapping.contains();
//        return 0;
//    }


    public int getValue(int inValue) { //get 3 is c - return 2
//        int out = outList.indexOf(outList.get(inValue));
        return inValue;

    }

    @Override
    public boolean doesNotchAllowRotation() {

        return notchLocation == headPosition;
    }

    @Override
    public boolean setRotorPosition(int headPosition) {
        this.headPosition = headPosition;
        return false;
    }

    @Override
    public void rotate() {
        notchLocation++;
    }
}
