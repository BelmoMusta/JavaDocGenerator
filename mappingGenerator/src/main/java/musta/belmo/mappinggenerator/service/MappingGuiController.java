package musta.belmo.mappinggenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import musta.belmo.fx.mustagui.Binder;
import musta.belmo.fx.mustagui.CustomButton;
import musta.belmo.fx.mustagui.MustaPane;
import musta.belmo.fx.mustagui.TextFieldFormController;
import musta.belmo.javacodecore.MyOptional;
import musta.belmo.javacodegenerator.service.GenerateOnDemandeHolderPattern;
import org.apache.commons.lang3.StringUtils;

public class MappingGuiController {
    @FXML
    public MustaPane mustaPane;

    @FXML
    public void initialize() {
        CustomButton generateMapper = mustaPane.addButton("generate mapper", "fa-save", "Generate mapper");
        CustomButton deriveInterface = mustaPane.addButton("derive interface", "fa-fire", "Derive interface");
        CustomButton generateOnDemandeHolder = mustaPane.addButton("ODH pattern", "fa-fire", "Generate ODH pattern");
        deriveInterface.setOnAction(event -> {
            InterfaceDeriver interfaceDeriver = new InterfaceDeriver();
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("interface name");
            inputDialog.setHeaderText("Interface");
            inputDialog.setContentText("Enter the interface name");
            MyOptional<String> stringMyOptional = MyOptional.fromOptional(inputDialog.showAndWait());
            String iterfaceName = stringMyOptional.orElseIfPredicate("I", StringUtils::isBlank);
            CompilationUnit compilationUnit = interfaceDeriver
                    .deriveInterfaceFromClass(mustaPane.getTextArea().getText(), iterfaceName);
            mustaPane.setText(compilationUnit);
        });
        generateMapper.setOnAction(event -> {
            MappingGenerator mappingGenerator = new MappingGenerator();
            mappingGenerator.setSource(mustaPane.getTextArea().getText());
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Classe name");
            inputDialog.setHeaderText("");
            inputDialog.setContentText("Destination class name");
            MyOptional<String> stringMyOptional = MyOptional.fromOptional(inputDialog.showAndWait());
            String iterfaceName = stringMyOptional.orElseIfPredicate("", StringUtils::isBlank);

            mappingGenerator.setDestinationClassName(iterfaceName);
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
        generateOnDemandeHolder.setOnAction(event -> {
            GenerateOnDemandeHolderPattern generateOnDemandeHolderPattern = new GenerateOnDemandeHolderPattern();
            CompilationUnit compilationUnit = generateOnDemandeHolderPattern.generate(mustaPane.getTextArea().getText());
            mustaPane.setText(compilationUnit);
        });

        mustaPane.addMenuGroup("File");
        mustaPane.addMenuItemToGroup("Save", "File");
        //

        Binder binder = new Binder();
        TextFieldFormController textFieldFormController = new TextFieldFormController();
        textFieldFormController.setName("a simple name");

        try {
            Pane bind = binder.bind(textFieldFormController);
            mustaPane.getChildren().add(bind);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
