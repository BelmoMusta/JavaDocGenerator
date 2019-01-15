package musta.belmo.enumhandler.scratch;

import musta.belmo.javacodecore.gui.app.AbstractJavaFXApplication;

import java.net.URL;

public class JavaMethodTester extends AbstractJavaFXApplication {
    @Override
    public URL loadFXMLFile() {

        return JavaMethodTester.class.getClassLoader().getResource("scratch-window-fx.fxml");

    }
}
