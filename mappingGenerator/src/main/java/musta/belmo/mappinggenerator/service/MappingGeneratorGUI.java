package musta.belmo.mappinggenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import musta.belmo.fx.mustagui.CustomButton;
import musta.belmo.fx.mustagui.MustaPane;

import java.util.concurrent.CountDownLatch;

public class MappingGeneratorGUI extends Application {

    private MustaPane mustaPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mustaPane = new MustaPane();
        CustomButton generateMapper = mustaPane.addButton("generate mapper", "fa-save", "Generate mapper");
        CustomButton deriveInterface = mustaPane.addButton("derive interface", "fa-fire", "Derive interface");
        deriveInterface.setOnAction(event -> {

            InterfaceDeriver interfaceDeriver = new InterfaceDeriver();

            CompilationUnit compilationUnit = interfaceDeriver.deriveInterfaceFromClass(mustaPane.getTextArea().getText(), "");
            mustaPane.setText(compilationUnit);
        });
        generateMapper.setOnAction(event -> {
            MappingGenerator mappingGenerator = new MappingGenerator();

            mappingGenerator.setSource(mustaPane.getTextArea().getText());
            mappingGenerator.setDestinationClassName("BookV2");
            mappingGenerator.setDestinationPackage("logic.book");
            mappingGenerator.setMappingMethodPrefix("map");
            mappingGenerator.setMapperClassPrefix("Mapper");
            mappingGenerator.setStaticMethod(true);
            mappingGenerator.setAccessCollectionByGetter(true);
            mappingGenerator.mapField("title", "titre");
            mappingGenerator.createMapper();
            mappingGenerator.createMapper();
            mustaPane.setText(mappingGenerator.getResult());
        });

        mustaPane.addMenuGroup("File");
        mustaPane.addMenuItemToGroup("Save", "File");
        primaryStage.setScene(new Scene(mustaPane, 800, 500));
        primaryStage.show();
    }
}
