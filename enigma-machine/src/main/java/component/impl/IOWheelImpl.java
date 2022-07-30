package main.java.component.impl;

import main.java.component.IOWheel;

public class IOWheelImpl implements IOWheel {

    @Override
    public String handleInput(String input) {
        return input;
    }

    @Override
    public int handleInput(int input) {
        return input;
    }

    @Override
    public String handleOutput(String output) {
        return output;
    }

    @Override
    public int handleOutput(int output) {
        return output;
    }
}
