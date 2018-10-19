package musta.belmo.catchverifier.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;


public class CatchVerifierTableGui extends Application {


    private static final Logger LOG = LoggerFactory.getLogger(CatchVerifierTableGui.class);


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LOG.info("start ");
        URL resourceAsStream = CatchVerifierTableGui.class.getClassLoader().getResource("catchVerifierTable-fx.fxml");
        LOG.info("root set ");
        Parent root = FXMLLoader.load(resourceAsStream);
        primaryStage.setTitle("Return counter table");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }
}
