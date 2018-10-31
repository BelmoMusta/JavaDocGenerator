package musta.belmo.javacodegenerator.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import musta.belmo.javacodecore.Utils;
import musta.belmo.javacodegenerator.service.JavaDocGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static javafx.scene.input.KeyCode.ENTER;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @since 1.0.0.SNAPSHOT
 */
public class TreeViewController implements ControllerConstants {

    /**
     * L'attribut {@link #generator}.
     */
    private JavaDocGenerator generator;

    /**
     * L'attribut {@link #root}.
     */
    public BorderPane root;

    /**
     * L'attribut {@link #markedFiles}.
     */
    private Map<String, Boolean> markedFiles = new LinkedHashMap<>();

    /**
     * L'attribut {@link #tree}.
     */
    public TreeView<File> tree;

    /**
     * L'attribut {@link #tabPane}.
     */
    public TabPane tabPane;

    /**
     * L'attribut {@link #untitledCounter}.
     */
    private int untitledCounter = 0;

    /**
     * TODO: Compléter la description de cette méthode
     */
    @FXML
    public void initialize() {
        setupTop();
        generator = new JavaDocGenerator();
        tree.setVisible(false);
        setupMenuBar();
        setUpIconsBar();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                loadFile(tree.getSelectionModel().getSelectedItem().getValue(), null);
            }
        });
        tree.setOnKeyPressed(eventHandler -> {
            if (ENTER.equals(eventHandler.getCode())) {
                loadFile(tree.getSelectionModel().getSelectedItem().getValue(),
                        null);
            }
        });
        tree.setCellFactory((callback) -> new TreeCell<File>() {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                setText("");
                setGraphic(null);
                Optional.ofNullable(item).ifPresent(it -> {
                    setText(item.getName());
                    setGraphic(getTreeItem().getGraphic());
                });
            }
        });
    }

    private void setUpIconsBar() {
        HBox box = new HBox();
        Button generateJavaDoc = new Button();
        Button deleteJavaDoc = new Button();
        deleteJavaDoc.disableProperty().bind(Bindings.size(tabPane.getTabs()).isEqualTo(0));
        generateJavaDoc.disableProperty().bind(Bindings.size(tabPane.getTabs()).isEqualTo(0));
        generateJavaDoc.setGraphic(FontIcon.of(FontAwesome.findByDescription("fa-comments")));
        generateJavaDoc.setOnAction(this::addJavaDoc);
        generateJavaDoc.setTooltip(new Tooltip("Generate javadoc"));
        deleteJavaDoc.setGraphic(FontIcon.of(FontAwesome.findByDescription("fa-remove")));
        deleteJavaDoc.setTooltip(new Tooltip("Delete javadoc"));
        deleteJavaDoc.setOnAction(this::deleteJavaDoc);
        box.getChildren().addAll(generateJavaDoc, deleteJavaDoc);
        VBox vBox = Utils.castTo(root.getTop());
        vBox.getChildren().add(box);
    }

    /**
     * Add folder to tree view
     *
     * @param path {@link String}
     */
    private void addFolderToTreeView(String path) {
        tree.setRoot(createTree(new File(path)));
    }

    /**
     */

    private void setupTop() {
        VBox vBox = new VBox();
        root.setTop(vBox);
    }

    private void setupMenuBar() {
        VBox vBox = (VBox) root.getTop();
        MenuBar menuBar = new MenuBar();
        setupFileMenuItem(menuBar);
        setupToolsMenuItem(menuBar);
        vBox.getChildren().add(menuBar);
    }

    /**
     * @param menuBar {@link MenuBar}
     */
    private void setupFileMenuItem(MenuBar menuBar) {
        Menu menu = new Menu("File ");

        MenuItem newFile = new MenuItemWithIcon("New file", "fa-file");
        MenuItem openFolder = new MenuItemWithIcon("Open folder", "fa-folder-open");
        MenuItem saveAllFilesInFolder = new MenuItemWithIcon("Save all files in folder", "fa-save");
        MenuItem saveFolderFilesAs = new MenuItemWithIcon("Save all files in folder as ...", "fa-save");

        MenuItem openFile = new MenuItemWithIcon("Open file", "fa-file");
        MenuItem saveFile = new MenuItemWithIcon("Save File", "fa-save");
        MenuItem saveFileAs = new MenuItemWithIcon("Save File As ...", "fa-save");

        setupMenuItemAction(openFolder, MenuAction.OPEN_FOLDER);
        setupMenuItemAction(saveFile, MenuAction.SAVE_FILE);
        setupMenuItemAction(saveFileAs, MenuAction.SAVE_FILE_AS);
        setupMenuItemAction(newFile, MenuAction.NEW_FILE);
        setupMenuItemAction(openFile, MenuAction.OPEN_FILE);


        saveFile.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        saveFileAs.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));

        saveFolderFilesAs.disableProperty().bind(Bindings.not(tree.visibleProperty()));
        saveAllFilesInFolder.disableProperty().bind(Bindings.not(tree.visibleProperty()));

        menu.getItems().add(newFile);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(openFolder);
        menu.getItems().add(saveAllFilesInFolder);
        menu.getItems().add(saveFolderFilesAs);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(openFile);
        menu.getItems().add(saveFile);
        menu.getItems().add(saveFileAs);
        menuBar.getMenus().add(menu);
    }

    /**
     * @param menuBar {@link MenuBar}
     */
    private void setupToolsMenuItem(MenuBar menuBar) {
        Menu menu = new Menu("Tools ");
        MenuItem setupProperties = new MenuItemWithIcon("setup properties",
                "fa-th-list");
        setupMenuItemAction(setupProperties, MenuAction.LOAD_PROPERTIES);
        menu.getItems().add(setupProperties);
        menu.getItems().add(new SeparatorMenuItem());
        menuBar.getMenus().add(menu);
    }

    /**
     * @param menuItem   {@link MenuItem}
     * @param menuAction {@link MenuAction}
     */
    private void setupMenuItemAction(MenuItem menuItem, MenuAction menuAction) {
        menuItem.setOnAction(event -> {
            switch (menuAction) {
                case OPEN_FOLDER:
                    openFolder();
                    break;
                case OPEN_FILE:
                    openFile();
                    break;
                case SAVE_FILE_AS:
                    saveFileAs();
                    break;
                case NEW_FILE:
                    newFile();
                    break;
                case LOAD_PROPERTIES:
                    loadProperties();
                    break;
                case SAVE_FILE:
                    saveFile();
                    break;
            }
        });
    }

    /**
     * Open folder
     */
    private void openFolder() {
        tree.setVisible(true);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Optional<File> file = Optional.ofNullable(directoryChooser
                .showDialog(null));
        file.ifPresent(f -> addFolderToTreeView(f.getAbsolutePath()));
    }

    /**
     * Open file
     */
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        loadFile(file, null);
    }

    /**
     * New file
     */
    private void newFile() {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("New file");
        textInputDialog.setHeaderText("Enter file name");
        Optional<String> name = textInputDialog.showAndWait();
        if (!name.isPresent()) {
            untitledCounter++;
        }
        loadFile(null, name.orElse(String.format("Untitled_%d", untitledCounter)));
    }

    /**
     * Load properties
     */
    private void loadProperties() {
        FileChooser propertiesFileChooser = new FileChooser();
        propertiesFileChooser.setInitialFileName(generator.getPropertiesPath());
        Optional<File> propertiesFile = Optional.ofNullable(propertiesFileChooser
                .showOpenDialog(null));
        propertiesFile.ifPresent(properties ->
                generator.loadProperties(properties.getAbsolutePath()));
    }

    /**
     * Save file
     */
    private void saveFile() {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        String path = selectedItem.getId();
        CodeArea codeArea = (CodeArea) selectedItem.getContent();
        try {
            Utils.saveToFile(codeArea.getText().getBytes(), path);
            markedFiles.put(path, false);
            selectedItem.setStyle("-fx-background-color: green;");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save file as
     */
    private void saveFileAs() {
        FileChooser fileChooser;
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        Node content = selectedItem.getContent();

        if (content != null) {
            CodeArea codeArea = (CodeArea) content;
            String text = codeArea.getText();
            fileChooser = new FileChooser();
            fileChooser.setInitialFileName(selectedItem.getText());
            File destFile = fileChooser.showSaveDialog(null);
            try {
                Utils.saveToFile(text.getBytes(), destFile);
                selectedItem.setId(destFile.getAbsolutePath());
                selectedItem.setStyle("-fx-background-color: green;");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load file
     *
     * @param file        {@link File}
     * @param newFileName {@link String}
     */
    private void loadFile(File file, String newFileName) {
        try {
            CodeArea codeArea = new CodeArea();
            codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
            // Subscription cleanupWhenNoLongerNeedIt =
            codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500)).subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
            Tab tab = new Tab();
            URL resource = getClass().getClassLoader().getResource("java-style.css");
            Optional.ofNullable(resource).ifPresent(url -> codeArea.getStylesheets().add(url.toExternalForm()));
            DoubleProperty fontSize = new SimpleDoubleProperty(18);
            codeArea.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", fontSize));
            codeArea.setPadding(new Insets(0, 0, 0, 10));
            codeArea.textProperty().addListener(new ChangeListener<String>() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!StringUtils.equals(oldValue, newValue) && markedFiles.get(tab.getId()) != null) {
                        markedFiles.replace(tab.getId(), true);
                        tab.setStyle("-fx-background-color: red;");
                    } else {
                        tab.setStyle("-fx-background-color: green;");
                        markedFiles.put(tab.getId(), false);
                    }
                }
            });
            tab.setOnCloseRequest(event -> {
                Boolean isEdited = markedFiles.get(tab.getId());
                if (BooleanUtils.isTrue(isEdited)) {
                    Alert dialogPane = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save this file before you close ? ");
                    Optional<ButtonType> buttonType = dialogPane.showAndWait();
                    if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                        CodeArea codeArea1 = Utils.castTo(tab.getContent());
                        try {
                            String path = tab.getId();
                            if (path == null) {
                                FileChooser fileChooser = new FileChooser();
                                File saved = fileChooser.showSaveDialog(null);
                                if (saved != null) {
                                    path = saved.getAbsolutePath();
                                }
                            }
                            if (path != null) {
                                Utils.saveToFile(codeArea1.getText().getBytes(), path);
                            } else
                                event.consume();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            tab.setOnClosed(event -> {
                String id = tab.getId();
                // remove from the marked files so that it can be reopened
                markedFiles.remove(id);
            });

            tabPane.getSelectionModel().select(tab);
            if (file != null && file.isFile() && markedFiles.get(file.getAbsolutePath()) == null) {
                String fileToString = FileUtils.readFileToString(file, "UTF-8");
                codeArea.replaceText(0, 0, fileToString);
                tab.setText(file.getName());
                tab.setId(file.getAbsolutePath());
                tabPane.getTabs().add(tab);
                tab.setContent(codeArea);
                markedFiles.put(file.getAbsolutePath(), false);
            } else if (file == null) {
                tabPane.getTabs().add(tab);
                tab.setText(newFileName);
                tab.setContent(codeArea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create tree
     *
     * @param file {@link File}
     * @return TreeItem
     */
    private TreeItem<File> createTree(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        Optional<File[]> files = Optional.ofNullable(file.listFiles());
        String iconName;

        if (files.isPresent()) {
            for (File child : files.get()) {
                item.getChildren().add(createTree(child));
            }
            iconName = "folder.png";
        } else {
            iconName = "text-x-generic.png";
        }
        URL resource = getClass().getClassLoader().getResource(iconName);
        Optional.ofNullable(resource).ifPresent(url -> item.setGraphic(new ImageView(url.toExternalForm())));
        return item;
    }

    /**
     * Add java doc
     *
     * @param actionEvent {@link ActionEvent}
     */
    private void addJavaDoc(ActionEvent actionEvent) {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            CodeArea content = (CodeArea) selectedItem.getContent();
            String text = content.getText();
            try {
                content.replaceText(generator.generateJavaDocAsString(text, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete java doc
     *
     * @param actionEvent {@link ActionEvent}
     * @throws IOException Exception levée si erreur.
     */
    private void deleteJavaDoc(ActionEvent actionEvent) {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            CodeArea content = (CodeArea) selectedItem.getContent();
            String text = content.getText();
            content.replaceText(generator.deleteJavaDoc(text));
        }
    }
}
