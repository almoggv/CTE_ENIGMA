package main.java.fx.component;

import javafx.scene.control.ListCell;

public class ComboBoxCell<T> extends ListCell<ComboBoxItem<T>> {

    public ComboBoxCell() {
        disabledProperty().addListener((observable, oldValue, newValue) ->
                setOpacity(newValue != null && newValue ? .6 : 1)
        );
    }

    @Override
    protected void updateItem(ComboBoxItem<T> item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
            setDisable(false);
            return;
        }

        setText(item.getValue().toString());
        setDisable(item.isChosen());
    }
}
