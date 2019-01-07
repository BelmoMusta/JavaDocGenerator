package musta.belmo.fx.mustagui;

import musta.belmo.fx.mustagui.annotations.Pane;
import musta.belmo.fx.mustagui.annotations.TextField;

@Pane
public class FormControllerExample {

    @TextField(name = "name", label = "Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
