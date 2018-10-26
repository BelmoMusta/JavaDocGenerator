package musta.belmo.catchverifier.gui.excel;

import musta.belmo.javacodecore.gui.app.AbstractJavaFXApplication;

import java.net.URL;

/**
 * TODO : Compléter la description de cette classe
 */
public class CatchVerifierGUI extends AbstractJavaFXApplication {
    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param args {@link String}
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public URL loadFXMLFile() {
        return CatchVerifierGUI.class.getClassLoader().getResource("catchVerifierGui-fx.fxml");
    }


}
