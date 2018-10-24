package musta.belmo.returncounter.gui.table;

import musta.belmo.javacodeutils.gui.app.AbstractJavaFXApplication;

import java.net.URL;

/**
 * Table counter graphical user interface.
 */
public class TableCounterGUI extends AbstractJavaFXApplication {
    /**
     * main method
     *
     * @param args {@link String}
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public URL loadFXMLFile() {
        return TableCounterGUI.class.getClassLoader()
                .getResource("returnCounterTable-fx.fxml");
    }

}
