package musta.belmo.javadocgenerator.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import musta.belmo.javadocgenerator.JavaDocGenerator;

import java.io.IOException;

public class GUIController {
    @FXML
    public CheckBox toZip;
    @FXML
    public ProgressIndicator progressBar;
    java.io.File src;
    java.io.File dest;


    @FXML
    public TextField sourceText;
    public TextField destText;

    @FXML
    public void initialize() {
        progressBar.setVisible(false);
    }

    public void chooseSourceDirectory(ActionEvent actionEvent) {


        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select sources folder ");
        src = directoryChooser.showDialog(null);
        if (src != null) {
            sourceText.setText(src.getAbsolutePath());
        }
    }

    public void chooseDestinationDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select destination folder ");
        dest = directoryChooser.showDialog(null);
        if (dest != null) {
            destText.setText(dest.getAbsolutePath());
        }
    }

    public void generateDoc(ActionEvent actionEvent) throws IOException, InterruptedException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    progressBar.setVisible(true);
                    if (src == null || dest == null){
                        JavaDocGenerator.generateJavaDocForAllClasses(sourceText.getText(), destText.getText(), toZip.isSelected());
                    } else {
                        JavaDocGenerator.generateJavaDocForAllClasses(src, dest, toZip.isSelected());
                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("an error occured while generating javadoc");
                        alert.setContentText(String.format("Details :%n%s", e));
                        alert.showAndWait();
                    });
                }

                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Javadoc generated successfully");
                    alert.setContentText("");
                    alert.showAndWait();
                });

                return null;
            }
        };

        new Thread(task).start();

    }
}
