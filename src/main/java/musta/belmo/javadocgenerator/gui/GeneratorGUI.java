package musta.belmo.javadocgenerator.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import musta.belmo.javadocgenerator.JavaDocGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;

/**
 * TODO : Compléter la description de cette classe
 */
public class GeneratorGUI extends Application {

    /**
     * La constante {@link #LOG} de type {@link Logger} ayant la valeur LoggerFactory.getLogger(GeneratorGUI.class).
     */
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorGUI.class);

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
        URL resourceAsStream = GeneratorGUI.class.getClassLoader().getResource("window-fx.fxml");
        LOG.info("root set ");
        Parent root = FXMLLoader.load(resourceAsStream);
        primaryStage.setTitle("JavaDoc generator");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }
}
