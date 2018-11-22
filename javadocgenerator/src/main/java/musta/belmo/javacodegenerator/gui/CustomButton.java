package musta.belmo.javacodegenerator.gui;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class CustomButton extends Button {
    public CustomButton() {
    }

    public CustomButton(String text) {
        super(text);
    }

    public CustomButton(String text, Node graphic) {
        super(text, graphic);
    }

    public CustomButton(String text, String graphic) {
        this(text);
        setGraphic(graphic);
    }

    public void disableWhen(BooleanBinding booleanBinding) {
        disableProperty().bind(booleanBinding);
    }

    public void setGraphic(String iconName) {
        FontIcon fontIcon = FontIcon.of(FontAwesome.findByDescription(iconName));
        setGraphic(fontIcon);
    }

    public void setTooltip(String tooltip) {
        setTooltip(new Tooltip(tooltip));
    }
}
