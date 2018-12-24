package musta.belmo.javacodegenerator.gui.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.DefaultStringConverter;
import musta.belmo.javacodecore.gui.table.MustaTableColumn;
import musta.belmo.javacodegenerator.gui.CustomButton;
import musta.belmo.javacodegenerator.gui.beans.PropertiesHolder;
import musta.belmo.javacodegenerator.service.JavaDocGenerator;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesController {
    public TableView tableView;
    public HBox btnGroup;

    @FXML
    public void initialize() throws Exception {
        CustomButton customButton = new CustomButton("Save", "fa-save");
        btnGroup.getChildren().add(customButton);
        ObservableList<MustaTableColumn> columns = tableView.getColumns();
        columns.clear();
        MustaTableColumn<PropertiesHolder, String> keyColumn = new MustaTableColumn<>("Key", "key");
        MustaTableColumn<PropertiesHolder, String> valueColumn = new MustaTableColumn<>("Value", "value");


        keyColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        valueColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.7));
        columns.addAll(keyColumn, valueColumn);

        JavaDocGenerator javaDocGenerator = JavaDocGenerator.getInstance();
        String propertiesPath = URLDecoder.decode(javaDocGenerator.getPropertiesPath(), "UTF-8");
        java.io.File file = new java.io.File(propertiesPath);
        Properties properties = new Properties();
        properties.load(file.toURI().toURL().openStream());
        List<PropertiesHolder> listFromHolder = getListFromHolder(properties);

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setEditable(true);
        valueColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<PropertiesHolder, String> t) ->
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setKey(t.getNewValue()));
        tableView.getItems().addAll(listFromHolder);
    }

    private List<PropertiesHolder> getListFromHolder(Properties properties) {
        List<PropertiesHolder> propertiesHolders = new ArrayList<>();
        for (Map.Entry<Object, Object> next : properties.entrySet()) {
            propertiesHolders.add(new PropertiesHolder(next.getKey(), next.getValue()));
        }
        return propertiesHolders;
    }
}