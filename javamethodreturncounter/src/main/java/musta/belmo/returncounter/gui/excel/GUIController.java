package musta.belmo.returncounter.gui.excel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import musta.belmo.returncounter.service.ReturnCounter;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GUIController {

    /**
     * La constante {@link #LOG} de type {@link Logger} ayant la valeur LoggerFactory.getLogger(GUIController.class).
     */
    private static final Logger LOG = LoggerFactory.getLogger(GUIController.class);


    /**
     * L'attribut {@link #progressBar}.
     */
    @FXML
    public ProgressIndicator progressBar;
    @FXML
    public Button chooseDest;
    @FXML
    public Button chooseDire;


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
        chooseDest.setGraphic(FontIcon.of(FontAwesome.findByDescription("fa-save")));
        chooseDire.setGraphic(FontIcon.of(FontAwesome.findByDescription("fa-folder-open")));

        progressBar.setVisible(false);
    }

    /**
     * L'attribut {@link #returnCounter}.
     */
    ReturnCounter returnCounter;

    /**
     * Choose source directory
     *
     * @param actionEvent {@link ActionEvent}
     */
    public void chooseSourceDirectory(ActionEvent actionEvent) {
        LOG.info("create a file to save ");
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
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Excel files(*.xls, *.xlsx)",
                "*.xls", "*.xslx");
        fileChooser.getExtensionFilters().add(filter);

        fileChooser.setTitle("Save as ...");

        fileChooser.setInitialFileName("ReturnCount.xls");
        dest = fileChooser.showSaveDialog(null);

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
     * @throws IOException          Exception levée si erreur.
     * @throws InterruptedException Exception levée si erreur.
     */
    public void countReturns(ActionEvent actionEvent) throws IOException, InterruptedException {
        LOG.info("countReturns");
        returnCounter = new ReturnCounter();
        Task<Void> task = new Task<Void>() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Void call() throws Exception {
                try {
                    progressBar.setVisible(true);
                    if (src == null || dest == null) {
                        returnCounter.countReturnStatements(sourceText.getText(), destText.getText());
                    } else {
                        returnCounter.countReturnStatements(src, dest);
                    }
                } catch (Exception e) {
                    LOG.error("exception {}", e);
                    Platform.runLater(() -> {
                        // since JavaFX 8u40
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("an error occurred while counting returns ");
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
                    alert.setHeaderText("Counts generated successfully");
                    alert.setContentText("");
                    alert.showAndWait();
                });
                return null;
            }
        };
        new Thread(task).start();
    }

}
