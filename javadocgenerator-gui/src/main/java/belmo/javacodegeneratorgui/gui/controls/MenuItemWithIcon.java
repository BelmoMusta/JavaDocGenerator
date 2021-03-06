package belmo.javacodegeneratorgui.gui.controls;

import javafx.scene.control.MenuItem;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuItemWithIcon extends MenuItem {

    public MenuItemWithIcon(String text) {
        super(text);
    }

    public MenuItemWithIcon(String text, String icon) {
        super(text, FontIcon.of(FontAwesome.findByDescription(icon)));

    }

}
