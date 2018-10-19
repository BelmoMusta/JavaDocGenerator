package musta.belmo.catchverifier.gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class MustaTableColumn<S, T> extends TableColumn<S, T> {

    MustaTableColumn() {
        super();
    }

    MustaTableColumn(String text, String propoerty) {
        this(text);

        setCellValueFactory(new PropertyValueFactory<>(propoerty));

    }

    public MustaTableColumn(String text) {
        this();
        setText(text);
    }
}
