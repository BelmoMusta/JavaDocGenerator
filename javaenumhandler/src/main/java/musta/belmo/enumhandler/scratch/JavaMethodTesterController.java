package musta.belmo.enumhandler.scratch;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import musta.belmo.fx.mustagui.MustaPane;


import java.net.URL;
import java.util.ResourceBundle;

public class JavaMethodTesterController implements Initializable{
    public MustaPane mustaPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TextField b = new TextField("button b ");
        TextField bb = new TextField("button b ");
        VBox  vBox = new VBox(b,bb);

        mustaPane.setCenter(vBox);
    }
}
