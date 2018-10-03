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

/**
 * TODO : Compléter la description de cette classe
 */
public class GUIController {

    /**
     * La constante {@link #LOG} de type {@link Logger} ayant la valeur LoggerFactory.getLogger(GUIController.class).
     */
    private static final Logger LOG = LoggerFactory.getLogger(GUIController.class);

    /**
     * L'attribut {@link #toZip}.
     */
    @FXML
    public CheckBox toZip;

    /**
     * L'attribut {@link #progressBar}.
     */
    @FXML
    public ProgressIndicator progressBar;

    /**
     * L'attribut {@link #deleteOldJavadoc}.
     */
    @FXML
    public CheckBox deleteOldJavadoc;

    /**
     * L'attribut {@link #src}.
     */
    File src;

    /**
     * L'attribut {@link #dest}.
     */
    File dest;

    /**
     * L'attribut {@link #sourceText}.
     */
    @FXML
    public TextField sourceText;

    /**
     * L'attribut {@link #destText}.
     */
    public TextField destText;

    /**
     * TODO: Compléter la description de cette méthode
     */
    @FXML
    public void initialize() {
        progressBar.setVisible(false);
    }

    /**
     * L'attribut {@link #mJavaDocGenerator}.
     */
    JavaDocGenerator mJavaDocGenerator;

    /**
     * Choose source directory
     *
     * @param actionEvent {@link ActionEvent}
     */
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

    /**
     * Choose destination directory
     *
     * @param actionEvent {@link ActionEvent}
     */
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

    /**
     * Generate doc
     *
     * @param actionEvent {@link ActionEvent}
     * @throws IOException Exception levée si erreur.
     * @throws InterruptedException Exception levée si erreur.
     */
    public void generateDoc(ActionEvent actionEvent) throws IOException, InterruptedException {
        LOG.info("generateDoc");
        mJavaDocGenerator = new JavaDocGenerator();
        Task<Void> task = new Task<Void>() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Void call() throws Exception {
                try {
                    progressBar.setVisible(true);
                    if (src == null || dest == null) {
                        mJavaDocGenerator.generateJavaDocForAllClasses(sourceText.getText(), destText.getText(), toZip.isSelected(), deleteOldJavadoc.isSelected());
                    } else {
                        mJavaDocGenerator.generateJavaDocForAllClasses(src, dest, toZip.isSelected(), deleteOldJavadoc.isSelected());
                    }
                } catch (Exception e) {
                    LOG.error("exception {}", e);
                    Platform.runLater(() -> {
                        // since JavaFX 8u40
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
                    // since JavaFX 8u40
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

    /**
     * Load properties
     *
     * @param actionEvent {@link ActionEvent}
     */
    public void loadProperties(ActionEvent actionEvent) {
        LOG.info("loadProperties");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(mJavaDocGenerator.getPropertiesPath());
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            mJavaDocGenerator.loadProperties(file.getAbsolutePath());
        }
        LOG.info("file {}", file);
    }
}
