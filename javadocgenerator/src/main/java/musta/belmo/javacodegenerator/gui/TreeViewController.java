package musta.belmo.javacodegenerator.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import musta.belmo.javacodecore.Utils;
import musta.belmo.javacodegenerator.service.JavaDocGenerator;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCode.ENTER;

public class TreeViewController {
    private static final String PATHNAME = "C:\\Users\\mustapha\\Desktop\\DocGen\\musta";
    public BorderPane root;
    private Map<String, Boolean> markedFiles = new LinkedHashMap<>();
    private static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[]]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );


    public TreeView<File> tree;
    public TabPane tabPane;


    @FXML
    public void initialize() {
        tree.setVisible(false);
        setupMenuBar();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                loadFile(tree.getSelectionModel().getSelectedItem().getValue());
            }
        });

        tree.setOnKeyPressed(e -> {
            if (ENTER.equals(e.getCode())) {
                loadFile(tree.getSelectionModel().getSelectedItem().getValue());
            }
        });

        tree.setCellFactory((e) -> new TreeCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                    setGraphic(getTreeItem().getGraphic());
                } else {
                    setText("");
                    setGraphic(null);
                }
            }
        });
    }

    private void addFolderToTreeView(String path) {
        tree.setRoot(createTree(new File(path)));
    }

    private void setupMenuBar() {
        MenuBar menuBar = new MenuBar();


        Menu menu = new Menu("File ");
        MenuItem openFolder = new MenuItem("Open folder");
        MenuItem saveAllFilesInFolder = new MenuItem("Save all files in folder");
        MenuItem saveFolderFilesAs = new MenuItem("Save all files in folder as ...");

        MenuItem openFile = new MenuItem("Open file");
        MenuItem saveFile = new MenuItem("Save File");
        MenuItem saveFileAs = new MenuItem("Save File As ...");
        setupMenuItemAction(openFolder, 0);
        setupMenuItemAction(openFile, 1);
        menu.getItems().add(openFolder);
        menu.getItems().add(saveAllFilesInFolder);
        menu.getItems().add(saveFolderFilesAs);
        SeparatorMenuItem separator = new SeparatorMenuItem();
        menu.getItems().add(separator);
        menu.getItems().add(openFile);
        menu.getItems().add(saveFile);
        menu.getItems().add(saveFileAs);
        menuBar.getMenus().add(menu);
        root.setTop(menuBar);
    }

    private void setupMenuItemAction(MenuItem menuItem, int action) {
        menuItem.setOnAction(event -> {
            File file;
            FileChooser fileChooser;
            switch (action) {
                case 0: // open  folder
                    tree.setVisible(true);
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    file = directoryChooser.showDialog(null);
                    addFolderToTreeView(file.getAbsolutePath());
                    break;
                case 1:// open file
                    fileChooser = new FileChooser();
                    file = fileChooser.showOpenDialog(null);
                    loadFile(file);
                    break;
                case 2: // save file as
                    Node content = tabPane.getSelectionModel().getSelectedItem().getContent();

                    if (content != null) {
                        CodeArea codeArea = (CodeArea) content;
                        String text = codeArea.getText();
                        fileChooser = new FileChooser();
                        File destFile = fileChooser.showOpenDialog(null);
                        try {
                            Utils.saveToFile(text.getBytes(),destFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
            }
        });
    }


    private void loadFile(File file) {
        try {
            if (file.isFile() && markedFiles.get(file.getAbsolutePath()) == null) {
                String fileToString = FileUtils.readFileToString(file, "UTF-8");
                CodeArea codeArea = new CodeArea();
                codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

                Subscription cleanupWhenNoLongerNeedIt = codeArea

                        // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                        // multi plain changes = save computation by not rerunning the code multiple times
                        //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                        .multiPlainChanges()

                        // do not emit an event until 500 ms have passed since the last emission of previous stream
                        .successionEnds(Duration.ofMillis(500))

                        // run the following code block when previous stream emits an event
                        .subscribe(ignore ->
                                codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

                codeArea.replaceText(0, 0, fileToString);
                Tab tab = new Tab();
                codeArea.getStylesheets().add(getClass().getClassLoader().getResource("java-style.css").toExternalForm());

                DoubleProperty fontSize = new SimpleDoubleProperty(18);
                codeArea.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", fontSize));
                codeArea.setPadding(new Insets(0, 0, 0, 10));
                tab.setContent(codeArea);
                tab.setText(file.getName());
                tab.setId(file.getAbsolutePath());
                tab.setOnClosed(event -> {
                    String id = tab.getId();
                    // remove from the marked files so that it can be reopened
                    markedFiles.remove(id);
                });
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                markedFiles.put(file.getAbsolutePath(), true);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TreeItem<File> createTree(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        File[] childs = file.listFiles();

        String iconeName;
        if (childs != null) {
            for (File child : childs) {
                item.getChildren().add(createTree(child));
            }
            iconeName = "folder.png";

        } else {
            iconeName = "text-x-generic.png";
        }
        item.setGraphic(new ImageView(getClass()
                .getClassLoader()
                .getResource(iconeName)
                .toExternalForm()));
        return item;
    }


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void addJavaDoc(ActionEvent actionEvent) throws IOException {

        JavaDocGenerator generator = new JavaDocGenerator();
        CodeArea content = (CodeArea) tabPane.getSelectionModel().getSelectedItem().getContent();
        String text = content.getText();
        String s = generator.generateJavaDocAsString(text, true);
        content.replaceText(s);
    }

    public void deleteJavaDoc(ActionEvent actionEvent) throws IOException {
        JavaDocGenerator generator = new JavaDocGenerator();
        CodeArea content = (CodeArea) tabPane.getSelectionModel().getSelectedItem().getContent();
        String text = content.getText();
        String s = generator.deleteJavaDoc(text);
        content.replaceText(s);
    }
}
