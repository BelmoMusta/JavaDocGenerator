package musta.belmo.mappinggenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import musta.belmo.fx.mustagui.Binder;
import musta.belmo.fx.mustagui.CustomButton;
import musta.belmo.fx.mustagui.FormControllerExample;
import musta.belmo.fx.mustagui.MustaPane;

public class MappingGuiController {
    @FXML
    public MustaPane mustaPane;

    @FXML
    public void initialize() {
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
        //
        Binder binder = new Binder();
        FormControllerExample formControllerExample = new FormControllerExample();
        formControllerExample.setName("a simple name");

        try {
            Pane bind = binder.bind(formControllerExample);
            mustaPane.getChildren().add(bind) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
