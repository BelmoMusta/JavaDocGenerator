package musta.belmo.javacodegenerator.gui.controller;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import musta.belmo.javacodecore.Utils;
import musta.belmo.fx.mustagui.CustomButton;
import musta.belmo.javacodegenerator.gui.MenuAction;
import musta.belmo.fx.mustagui.MenuItemWithIcon;
import musta.belmo.javacodegenerator.gui.PropertiesGUI;
import musta.belmo.javacodegenerator.service.JavaDocDeleter;
import musta.belmo.javacodegenerator.service.exception.CompilationException;
import musta.belmo.javacodegenerator.service.JavaDocGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

import static javafx.scene.input.KeyCode.ENTER;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @since 1.0.0.SNAPSHOT
 */
public class TreeViewController implements ControllerConstants {


    private static final String FX_BACKGROUND_COLOR_GREEN = "-fx-background-color: green;";
    /**
     * L'attribut {@link #generator}.
     */
    private JavaDocGenerator generator;

    /**
     * L'attribut {@link #generator}.
     */
    private JavaDocDeleter deleter;

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

    private String treePath;

    /**
     * TODO: Compléter la description de cette méthode
     */
    @FXML
    public void initialize() {
        setupTop();
        deleter = JavaDocDeleter.getInstance();
        generator = JavaDocGenerator.getInstance();
        tree.setVisible(false);
        setupMenuBar();
        setUpIconsBar();
        setupKeys();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                loadFile(tree.getSelectionModel().getSelectedItem().getValue(), null);
            }
        });
        tree.setOnKeyPressed(eventHandler -> {
            if (ENTER.equals(eventHandler.getCode())) {
                loadFile(tree.getSelectionModel().getSelectedItem().getValue(), null);
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
        setupContextualMenu();
    }

    private void setupKeys() {
        tabPane.setOnKeyPressed(this::handleKeyEvents);
        tree.setOnKeyPressed(this::handleKeyEvents);
    }

    private void setupContextualMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addJavadocMenuItem = new MenuItem("add javadoc for all java classes");
        MenuItem deleteJavadocMenuItem = new MenuItem("delete javadoc for all classes");
        MenuItem generateImplMenuItem = new MenuItem("generate the implementation for this interface");
        addJavadocMenuItem.setOnAction(event -> {
            File folder = tree.getSelectionModel().getSelectedItem().getValue();
            try {
                try {
                    generator.generateJavaDocForAllClasses(folder);
                } catch (CompilationException e) {
                    showExceptionAlert(e);
                }
                // tree.getSelectionModel().getSelectedItem().setValue(folder);
                addFolderToTreeView(treePath);
                tree.getSelectionModel().select(tree.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        generateImplMenuItem.setOnAction(event -> {
            File folder = tree.getSelectionModel().getSelectedItem().getValue();
            try {
                deleter.deleteJavaDocForAllClasses(folder);
                addFolderToTreeView(treePath);
                tree.getSelectionModel().select(tree.getSelectionModel().getSelectedItem());
            } catch (IOException | CompilationException e) {
                showExceptionAlert(e);
            }
        });
        deleteJavadocMenuItem.setOnAction(event -> {
            File folder = tree.getSelectionModel().getSelectedItem().getValue();
            try {
                deleter.deleteJavaDocForAllClasses(folder);
                addFolderToTreeView(treePath);
                tree.getSelectionModel().select(tree.getSelectionModel().getSelectedItem());
            } catch (IOException | CompilationException e) {
                showExceptionAlert(e);
            }
        });
        contextMenu.getItems().add(addJavadocMenuItem);
        contextMenu.getItems().add(deleteJavadocMenuItem);
        contextMenu.getItems().add(generateImplMenuItem);
        tree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                TreeItem<File> selectedItem = tree.getSelectionModel().getSelectedItem();
                if (!selectedItem.isLeaf()) {
                    contextMenu.show(tree, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                } else if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
            }
        });
    }

    private void setUpIconsBar() {
        HBox box = new HBox();
        CustomButton generateJavaDocBtn = new CustomButton();
        CustomButton deleteJavaDocBtn = new CustomButton();
        CustomButton saveFileBtn = new CustomButton();
        CustomButton indentCodeBtn = new CustomButton();
        CustomButton reorganizeBtn = new CustomButton();

        BooleanBinding booleanBinding = Bindings.isEmpty(tabPane.getTabs());
        saveFileBtn.disableWhen(booleanBinding);
        saveFileBtn.disableWhen(booleanBinding);
        indentCodeBtn.disableWhen(booleanBinding);
        deleteJavaDocBtn.disableWhen(booleanBinding);
        generateJavaDocBtn.disableWhen(booleanBinding);
        reorganizeBtn.disableWhen(booleanBinding);


        saveFileBtn.setOnAction(event -> saveFile());
        generateJavaDocBtn.setOnAction(this::addJavaDoc);
        indentCodeBtn.setOnAction(this::indentCode);
        reorganizeBtn.setOnAction(this::reorganizeBtn);

        generateJavaDocBtn.setGraphic("fa-comments");
        saveFileBtn.setGraphic(FA_SAVE);
        deleteJavaDocBtn.setGraphic("fa-remove");
        indentCodeBtn.setGraphic("fa-indent");
        reorganizeBtn.setGraphic("fa-sitemap");

        deleteJavaDocBtn.setTooltip("Delete javadoc");
        generateJavaDocBtn.setTooltip("Generate javadoc");
        deleteJavaDocBtn.setOnAction(this::deleteJavaDoc);
        box.getChildren().addAll(generateJavaDocBtn, deleteJavaDocBtn);
        box.getChildren().addAll(saveFileBtn, indentCodeBtn, reorganizeBtn);

        VBox vBox = Utils.castTo(root.getTop());
        vBox.getChildren().add(box);
    }

    private void reorganizeBtn(ActionEvent actionEvent) {

        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            CodeArea content = (CodeArea) selectedItem.getContent();
            String text = content.getText();
            try {
                content.replaceText(generator.reorganize(text));
            } catch (CompilationException e) {
                showExceptionAlert(e);
            }
        }
    }

    /**
     * Add folder to tree view
     *
     * @param path {@link String}
     */
    private void addFolderToTreeView(String path) {
        treePath = path;
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
        setupJavadocMenuItem(menuBar.getMenus());
        setupToolsMenuItem(menuBar.getMenus());
        vBox.getChildren().add(menuBar);
    }

    private void setupJavadocMenuItem(ObservableList<Menu> menus) {
        Menu menu = new Menu("Code ");
        MenuItem addJavadoc = new MenuItemWithIcon("Add Javadoc", "fa-comments");
        MenuItem deleteJavadoc = new MenuItemWithIcon("Remove Javadoc", "fa-remove");
        MenuItem indentCode = new MenuItemWithIcon("Indent Code", "fa-indent");
        MenuItem reorganizeCode = new MenuItemWithIcon("Reorganize code", "fa-sitemap");
        setupMenuItemAction(addJavadoc, MenuAction.ADD_JAVADOC);
        setupMenuItemAction(deleteJavadoc, MenuAction.DELETE_JAVADOC);
        setupMenuItemAction(indentCode, MenuAction.INDENT_CODE);
        setupMenuItemAction(reorganizeCode, MenuAction.REORGANIZE_CODE);
        addJavadoc.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        deleteJavadoc.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        indentCode.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        reorganizeCode.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        indentCode.setAccelerator(CTRL_I);
        addJavadoc.setAccelerator(CTRL_J);
        reorganizeCode.setAccelerator(CTRL_R);
        deleteJavadoc.setAccelerator(CTRL_SHIFT_J);
        ObservableList<MenuItem> items = menu.getItems();
        items.add(addJavadoc);
        items.add(deleteJavadoc);
        items.add(indentCode);
        items.add(reorganizeCode);
        items.add(new SeparatorMenuItem());
        menus.add(menu);
    }

    /**
     * @param menuBar {@link MenuBar}
     */
    private void setupFileMenuItem(MenuBar menuBar) {
        Menu menu = new Menu("File ");
        MenuItem newFile = new MenuItemWithIcon("New file", "fa-file");
        MenuItem openFolder = new MenuItemWithIcon("Open folder", "fa-folder-open");
        MenuItem saveAllFilesInFolder = new MenuItemWithIcon("Save all files in folder", FA_SAVE);
        MenuItem saveFolderFilesAs = new MenuItemWithIcon("Save all files in folder as ...", FA_SAVE);
        MenuItem openFile = new MenuItemWithIcon("Open file", "fa-file");
        MenuItem saveFile = new MenuItemWithIcon("Save File", FA_SAVE);
        MenuItem saveFileAs = new MenuItemWithIcon("Save File As ...", FA_SAVE);
        setupMenuItemAction(openFolder, MenuAction.OPEN_FOLDER);
        setupMenuItemAction(saveFile, MenuAction.SAVE_FILE);
        setupMenuItemAction(saveFileAs, MenuAction.SAVE_FILE_AS);
        setupMenuItemAction(newFile, MenuAction.NEW_FILE);
        setupMenuItemAction(openFile, MenuAction.OPEN_FILE);
        setupMenuItemAction(saveAllFilesInFolder, MenuAction.SAVE_ALL_FILES);
        saveFile.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        saveFileAs.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        saveFolderFilesAs.disableProperty().bind(Bindings.not(tree.visibleProperty()));
        saveAllFilesInFolder.disableProperty().bind(Bindings.not(tree.visibleProperty()));
        //
        newFile.setAccelerator(CTRL_N);
        openFile.setAccelerator(CTRL_O);
        openFolder.setAccelerator(CTRL_SHIFT_O);
        saveFile.setAccelerator(CTRL_S);
        saveAllFilesInFolder.setAccelerator(CTRL_SHIFT_S);
        ObservableList<MenuItem> items = menu.getItems();
        items.add(newFile);
        items.add(new SeparatorMenuItem());
        items.add(openFolder);
        items.add(saveAllFilesInFolder);
        items.add(saveFolderFilesAs);
        items.add(new SeparatorMenuItem());
        items.add(openFile);
        items.add(saveFile);
        items.add(saveFileAs);
        menuBar.getMenus().add(menu);
    }

    /**
     * @param menus {@link Menu}
     */
    private void setupToolsMenuItem(ObservableList<Menu> menus) {
        Menu menu = new Menu("Tools ");
        MenuItem setupProperties = new MenuItemWithIcon("setup properties", "fa-th-list");
        setupMenuItemAction(setupProperties, MenuAction.LOAD_PROPERTIES);
        ObservableList<MenuItem> items = menu.getItems();
        items.add(setupProperties);
        items.add(new SeparatorMenuItem());
        menus.add(menu);
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
                    try {
                        loadProperties();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case SAVE_FILE:
                    saveFile();
                    break;
                case SAVE_ALL_FILES:
                    saveAllFiles();
                    break;
                case ADD_JAVADOC:
                    addJavaDoc(null);
                    break;
                case DELETE_JAVADOC:
                    deleteJavaDoc(null);
                    break;
                case INDENT_CODE:
                    indentCode(null);
                    break;
                case REORGANIZE_CODE:
                    reorganizeBtn(null);
                    break;
            }
        });
    }

    private void saveAllFiles() {
        for (Tab tab : tabPane.getTabs()) {
            String path = tab.getId();
            CodeArea codeArea = (CodeArea) tab.getContent();
            try {
                Utils.saveToFile(codeArea.getText().getBytes(), path);
                markedFiles.put(path, false);
                tab.setStyle(FX_BACKGROUND_COLOR_GREEN);
            } catch (IOException e) {
                showExceptionAlert(e);
            }
        }
    }

    /**
     * Open folder
     */
    private void openFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Optional<File> file = Optional.ofNullable(directoryChooser.showDialog(null));
        file.ifPresent(f -> {
            tree.setVisible(true);
            addFolderToTreeView(f.getAbsolutePath());
        });
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
    private void loadProperties() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PropertiesGUI.class.getClassLoader().getResource(PropertiesGUI.FXML_LOCATION));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 600, 400);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Save file
     */
    private void saveFile() {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        String path = selectedItem.getId();
        CodeArea codeArea = (CodeArea) selectedItem.getContent();
        try {
            if (path != null) {
                Utils.saveToFile(codeArea.getText().getBytes(), path);
                markedFiles.put(path, false);
                selectedItem.setStyle(FX_BACKGROUND_COLOR_GREEN);
            } else {
                saveFileAs();
            }
        } catch (IOException e) {
            showExceptionAlert(e);
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
                if (destFile != null) {
                    Utils.saveToFile(text.getBytes(), destFile);
                    selectedItem.setId(destFile.getAbsolutePath());
                    selectedItem.setStyle(FX_BACKGROUND_COLOR_GREEN);
                }
            } catch (IOException e) {
                showExceptionAlert(e);
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
            Tooltip tooltip = new Tooltip();
            if (file == null) {
                tooltip.setText(newFileName);
            } else {
                tooltip.setText(file.getAbsolutePath());
            }
            tab.setTooltip(tooltip);
            URL resource = getClass().getClassLoader().getResource("java-style.css");
            Optional.ofNullable(resource).ifPresent(url -> codeArea.getStylesheets().add(url.toExternalForm()));
            DoubleProperty fontSize = new SimpleDoubleProperty(18);
            codeArea.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", fontSize));
            codeArea.setPadding(new Insets(0, 0, 0, 10));
            addListenerToCodeArea(codeArea, tab);
            tab.setOnCloseRequest(event -> {
                Boolean isEdited = markedFiles.get(tab.getId());
                if (BooleanUtils.isTrue(isEdited)) {
                    Alert dialogPane = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save this file before you close ? ");
                    ObservableList<ButtonType> buttonTypes = dialogPane.getButtonTypes();

                    buttonTypes.clear();
                    buttonTypes.add(ButtonType.OK);
                    buttonTypes.add(ButtonType.NO);
                    buttonTypes.add(ButtonType.CANCEL);
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
                            showExceptionAlert(e);
                        }
                    } else if (buttonType.isPresent() && buttonType.get().equals(ButtonType.CANCEL)) {
                        event.consume();
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

            codeArea.requestFocus();
        } catch (IOException e) {
            showExceptionAlert(e);
        }
    }

    private void addListenerToCodeArea(CodeArea codeArea, Tab tab) {
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
                    tab.setStyle(FX_BACKGROUND_COLOR_GREEN);
                    markedFiles.put(tab.getId(), false);
                }
            }
        });
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
                content.replaceText(generator.generateJavaDocAsString(text, false));
            } catch (IOException | CompilationException e) {
                showExceptionAlert(e);
            }
        }
    }

    /**
     * Delete java doc
     *
     * @param actionEvent {@link ActionEvent}
     */
    private void deleteJavaDoc(ActionEvent actionEvent) {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            CodeArea content = (CodeArea) selectedItem.getContent();
            String text = content.getText();
            try {
                content.replaceText(deleter.deleteJavaDoc(text));
            } catch (CompilationException e) {
                showExceptionAlert(e);
            }
        }
    }

    /**
     * Delete java doc
     *
     * @param actionEvent {@link ActionEvent}
     */
    private void indentCode(ActionEvent actionEvent) {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            CodeArea content = (CodeArea) selectedItem.getContent();
            String text = content.getText();
            try {
                content.replaceText(generator.indentCode(text));
            } catch (CompilationException e) {
                showExceptionAlert(e);
            }
        }
    }

    private void handleKeyEvents(KeyEvent event) {
        KeyCode eventCode = event.getCode();
        if (event.isControlDown()) {
            Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
            switch (eventCode) {
                case W:
                    if (selectedItem != null) {
                        TabPaneBehavior behavior = new TabPaneBehavior(tabPane);
                        if (behavior.canCloseTab(selectedItem)) {
                            behavior.closeTab(selectedItem);
                        }
                    }
                    break;
                case F: {

                }
            }
        }
    }

    private void showExceptionAlert(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(exception.getMessage());
        alert.setTitle("Error while processing");
        alert.showAndWait();
    }
}
