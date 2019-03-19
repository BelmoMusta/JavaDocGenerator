package belmo.javacodegeneratorgui.gui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public abstract class AbstractJavaFXApplication extends Application {
    public abstract URL loadFXMLFile();
    /**
     * La constante {@link #LOG} de type {@link Logger} ayant la valeur LoggerFactory.getLogger(TableCounterGUI.class).
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJavaFXApplication.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LOG.info("start ");
        FXMLLoader fxmlLoader = new FXMLLoader(loadFXMLFile());
        primaryStage.setScene(new Scene(fxmlLoader.load(), 800, 500));
        LOG.info("root set ");
        primaryStage.show();
    }
}
