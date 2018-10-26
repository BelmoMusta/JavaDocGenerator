package musta.belmo.javacodegenerator.gui;

import musta.belmo.javacodecore.gui.app.AbstractJavaFXApplication;

import java.net.URL;

/**
 * TODO : Compléter la description de cette classe
 */
public class GeneratorGUI extends AbstractJavaFXApplication {
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
        return GeneratorGUI.class.getClassLoader().getResource("window-fx.fxml");
    }
}
