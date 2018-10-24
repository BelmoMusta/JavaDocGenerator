package musta.belmo.returncounter.gui.excel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;

/**
 * TODO : Compléter la description de cette classe
 *
 * @since lot 1.2
 */
public class ReturnCounterGUI extends Application {

    /**
     * La constante {@link #LOG} de type {@link Logger} ayant la valeur LoggerFactory.getLogger(ReturnCounterGUI.class).
     */
    private static final Logger LOG = LoggerFactory.getLogger(ReturnCounterGUI.class);

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param args {@link String}
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LOG.info("start ");
        URL resourceAsStream = ReturnCounterGUI.class.getClassLoader().getResource("returnCounterGui-fx.fxml");
        LOG.info("root set ");
        Parent root = FXMLLoader.load(resourceAsStream);
        primaryStage.setTitle("Return counter");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }
}
