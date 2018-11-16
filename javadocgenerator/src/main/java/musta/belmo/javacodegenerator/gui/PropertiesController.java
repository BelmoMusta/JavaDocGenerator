package musta.belmo.javacodegenerator.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import musta.belmo.javacodecore.gui.table.MustaTableColumn;
import musta.belmo.javacodegenerator.service.JavaDocGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesController {
    public TableView tableView;
    public HBox btnGroup;

    @FXML
    public void initialize() throws Exception {
        CustomButton customButton = new CustomButton("Save","fa-save");
        btnGroup.getChildren().add(customButton);
        ObservableList<MustaTableColumn> columns = tableView.getColumns();
        columns.clear();
        MustaTableColumn keyColumn = new MustaTableColumn("Key", "key");
        MustaTableColumn valueColumn = new MustaTableColumn("Value", "value");


        keyColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        valueColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.7));
        columns.addAll(keyColumn,valueColumn);

        JavaDocGenerator javaDocGenerator = new JavaDocGenerator();
        String propertiesPath = javaDocGenerator.getPropertiesPath();
        java.io.File file = new java.io.File(propertiesPath);

        Properties properties = new Properties();
        properties.load(file.toURI().toURL().openStream());
        List<PropertiesHolder> listFromHolder = getListFromHolder(properties);
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