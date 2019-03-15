package musta.belmo.fx.mustagui;

import musta.belmo.fx.mustagui.annotations.Pane;
import musta.belmo.fx.mustagui.annotations.TextField;

@Pane
public class TextFieldFormController {

    @TextField(name = "name", label = "Name")
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
