package musta.belmo.javacodeutils.gui.table;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * TODO : Compléter la description de cette classe
 */
public class MustaTableColumn<S, T> extends TableColumn<S, T> {

    /**
     * Constructeur par défaut
     */
    public MustaTableColumn() {
        super();
        // Constructeur par défaut
    }

    /**
     * Constructeur de la classe MustaTableColumn
     *
     * @param text{@link      String}
     * @param propoerty{@link String}
     */
    public MustaTableColumn(String text, String propoerty) {
        this(text);
        setCellValueFactory(new PropertyValueFactory<>(propoerty));
    }

    /**
     * Constructeur de la classe MustaTableColumn
     *
     * @param text{@link String}
     */
    public MustaTableColumn(String text) {
        this();
        setText(text);
    }
}
