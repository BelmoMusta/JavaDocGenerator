package musta.belmo.mappinggenerator.service;

import musta.belmo.javacodecore.gui.app.AbstractJavaFXApplication;

import java.net.URL;

public class MappingGeneratorGUI extends AbstractJavaFXApplication {

    @Override
    public URL loadFXMLFile() {
        return MappingGeneratorGUI.class.getClassLoader().getResource("window-fx.fxml");
    }



}
