package musta.belmo.javacodegenerator.gui;

import musta.belmo.javacodecore.gui.app.AbstractJavaFXApplication;

import java.net.URL;

public class PropertiesGUI extends AbstractJavaFXApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public URL loadFXMLFile() {
        return GeneratorGUI.class.getClassLoader().getResource("properties-window-fx.fxml");
    }
}
