package musta.belmo.javadocgenerator.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import musta.belmo.javadocgenerator.JavaDocGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GUIController {
    private static final Logger LOG = LoggerFactory.getLogger(GUIController.class);
    @FXML
    public CheckBox toZip;
    @FXML
    public ProgressIndicator progressBar;
    @FXML
    public CheckBox deleteOldJavadoc;

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

        LOG.info("chooseSourceDirectory ");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select sources folder ");
        src = directoryChooser.showDialog(null);
        if (src != null) {
            sourceText.setText(src.getAbsolutePath());
        }
        LOG.info("src :{} ", src);
        LOG.info("sourceText :{} ", sourceText);
    }

    public void chooseDestinationDirectory(ActionEvent actionEvent) {
        LOG.info("chooseDestinationDirectory ");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select destination folder ");
        dest = directoryChooser.showDialog(null);
        if (dest != null) {
            destText.setText(dest.getAbsolutePath());
        }
        LOG.info("dest :{} ", dest);
        LOG.info("destText :{} ", destText);
    }

    public void generateDoc(ActionEvent actionEvent) throws IOException, InterruptedException {
        LOG.info("generateDoc");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    progressBar.setVisible(true);
                    if (src == null || dest == null) {
                        JavaDocGenerator.generateJavaDocForAllClasses(sourceText.getText(),
                                destText.getText(),
                                toZip.isSelected(),
                                deleteOldJavadoc.isSelected());
                    } else {
                        JavaDocGenerator.generateJavaDocForAllClasses(src, dest,
                                toZip.isSelected(),
                                deleteOldJavadoc.isSelected());
                    }

                } catch (Exception e) {
                    LOG.error("exception {}", e);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("an error occured while generating javadoc");
                        alert.setContentText(String.format("Details :%n%s", e));
                        alert.showAndWait();
                    });
                }

                Platform.runLater(() -> {
                    LOG.info("Success");
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

    public void loadProperties(ActionEvent actionEvent) {
        LOG.info("loadProperties");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(JavaDocGenerator.PROPERTIES_PATH);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            JavaDocGenerator.loadProperties(file.getAbsolutePath());
        }
        LOG.info("file {}", file);
    }
}
