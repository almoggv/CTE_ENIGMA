package test.java.component;

import main.java.component.PlugBoard;
import main.java.component.impl.PlugBoardImpl;
import org.junit.Test;

public class PlugBoardTesting {

    @Test
    public void plugTestWithSpace(){
        PlugBoard board = new PlugBoardImpl("ABC )?");
        board.connect(" ", "B");
        board.connect("?", ")");

        PlugBoard cloned = board.getDeepClone();

    }
}
