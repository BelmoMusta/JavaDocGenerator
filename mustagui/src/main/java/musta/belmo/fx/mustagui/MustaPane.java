package musta.belmo.fx.mustagui;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class MustaPane extends BorderPane {
    private TextArea textArea;
    private VBox buttonBox;
    private MenuBar menuBar;

    public MustaPane() {
        textArea = new TextArea();
        menuBar = new MenuBar();
        final VBox top = new VBox();
        final HBox menuBarBox = new HBox();
        buttonBox = new VBox();
        menuBarBox.getChildren().add(menuBar);
        top.getChildren().add(menuBarBox);
        top.getChildren().add(buttonBox);
        setTop(top);
        setCenter(textArea);
    }

    public Menu addMenuGroup(String name) {
        Menu menu = new Menu(name);
        menuBar.getMenus().add(menu);
        return menu;
    }

    public MenuItem addMenuItemToGroup(String itemName, String groupName) {
        MenuItemWithIcon menuItem = new MenuItemWithIcon(itemName);
        Optional<Menu> first = menuBar.getMenus().stream().filter(menu ->
                menu.getText().equals(groupName)).findFirst();
        Menu menu;
        menu = first.orElseGet(() -> addMenuGroup(groupName));
        menu.getItems().add(menuItem);
        return menuItem;
    }

    public MenuItem addMenuItemToGroup(String itemName, String icon, String groupName) {
        MenuItemWithIcon menuItem = new MenuItemWithIcon(itemName, icon);
        Optional<Menu> first = menuBar.getMenus().stream().filter(menu ->
                menu.getText().equals(groupName)).findFirst();
        Menu menu;
        menu = first.orElseGet(() -> addMenuGroup(groupName));
        menu.getItems().add(menuItem);
        return menuItem;
    }

    public MenuItem addMenuItemToGroup(MenuItemWithIcon menuItem, String groupName) {
        Optional<Menu> first = menuBar.getMenus().stream().filter(menu ->
                menu.getText().equals(groupName)).findFirst();
        Menu menu;
        menu = first.orElseGet(() -> addMenuGroup(groupName));
        menu.getItems().add(menuItem);
        return menuItem;
    }

    public void setText(String value) {
        textArea.setText(value);
    }

    public void setText(Object value) {
        textArea.setText(String.valueOf(value));
    }

    public CustomButton addButton(String text, String icon, String toolTip) {
        final CustomButton button = new CustomButton(text, icon, toolTip);
        buttonBox.getChildren().add(button);
        return button;
    }

    public <T extends Button> T addButton(T button) {
        buttonBox.getChildren().add(button);
        return button;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public VBox getButtonBox() {
        return buttonBox;
    }

    public void setButtonBox(VBox buttonBox) {
        this.buttonBox = buttonBox;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }


}
