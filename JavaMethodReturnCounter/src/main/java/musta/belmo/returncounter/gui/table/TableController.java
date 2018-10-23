package musta.belmo.returncounter.gui.table;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import musta.belmo.returncounter.service.ReturnCounter;
import musta.belmo.returncounter.beans.MethodDescriber;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class TableController {
    @FXML
    public Button chooseDire;

    @FXML
    public TextField sourceText;

    @FXML
    TableView tableView;
    private File src;


    @FXML
    public void initialize() {
        ObservableList<MustaTableColumn> columns = tableView.getColumns();
        columns.clear();
        columns.add(new MustaTableColumn("Emplacement", "emplacement"));
        columns.add(new MustaTableColumn("Ligne", "ligne"));
        columns.add(new MustaTableColumn("Méthode", "name"));
        columns.add(new MustaTableColumn("Nombre de return", "nbReturns"));

        int u = 0;
        for (MustaTableColumn column : columns) {
            if (u == 0) {
                column.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
            } else if (u == 1) {
                column.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));
            } else if (u == 2) {
                column.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
            } else {
                column.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));
            }
            u++;
        }

        tableView.setFixedCellSize(35);

        tableView.minHeightProperty().bind(tableView.prefHeightProperty().multiply(2));
        tableView.maxHeightProperty().bind(tableView.prefHeightProperty().multiply(2));
    }

    public void chooseSourceDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select sources folder ");
        src = directoryChooser.showDialog(null);
        if (src != null) {
            sourceText.setText(src.getAbsolutePath());
        }
    }

    public void loadReturn(ActionEvent actionEvent) throws IOException {
        tableView.getItems().clear();
        ReturnCounter returnCounter = new ReturnCounter();

        Set<MethodDescriber> methodDescribers = returnCounter.countReturnStatements(src);

        for (MethodDescriber methodDescriber : methodDescribers) {
            tableView.getItems().add(methodDescriber);
        }
    }
}
