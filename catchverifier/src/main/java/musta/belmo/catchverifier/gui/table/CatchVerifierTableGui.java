package musta.belmo.catchverifier.gui.table;

import musta.belmo.javacodeutils.gui.app.AbstractJavaFXApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


public class CatchVerifierTableGui extends AbstractJavaFXApplication {


    private static final Logger LOG = LoggerFactory.getLogger(CatchVerifierTableGui.class);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public URL loadFXMLFile() {
        return CatchVerifierTableGui.class.getClassLoader().getResource("catchVerifierTable-fx.fxml");
    }


}
