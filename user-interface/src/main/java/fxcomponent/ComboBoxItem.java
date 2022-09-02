package src.main.java.fxcomponent;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

import javafx.beans.Observable;

public class ComboBoxItem<T> {
    @Getter private final T value;
    private final BooleanProperty chosen = new SimpleBooleanProperty();

    public ComboBoxItem(T value) {
        this.value = value;
    }

    public boolean isChosen() {
        return chosen.get();
    }

    public void setChosen(boolean isChosen) {
        this.chosen.set(isChosen);
    }

    public Observable isChosenProperty() {
        return chosen;
    }
}
