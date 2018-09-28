package musta.belmo.javadocgenerator.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GeneratorGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL resourceAsStream = GeneratorGUI.class.getClassLoader().getResource("window-fx.fxml");
        Parent root = FXMLLoader.load(resourceAsStream);
        primaryStage.setTitle("JavaDoc generator");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();

    }
}
