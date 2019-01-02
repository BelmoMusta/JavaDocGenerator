package musta.belmo.fx.mustagui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class MustaApp extends Application {
    public static final CountDownLatch latch = new CountDownLatch(1);
    public static MustaApp startUpTest = null;
    private MustaPane mustaPane;
    private String text;


    public static MustaApp waitForStartUpTest() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return startUpTest;
    }

    public MustaApp() {
        setStartUpTest(this);
    }

    public void printSomething(String text) {
        this.text = text;
    }

    public static void setStartUpTest(MustaApp startUpTest0) {
        startUpTest = startUpTest0;
        latch.countDown();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        mustaPane = new MustaPane();
        mustaPane.addButton("save", "fa-save", "lol");
        mustaPane.addMenuGroup("File");
        mustaPane.addMenuItemToGroup("Save", "File");
        primaryStage.setScene(new Scene(mustaPane, 800, 500));
        mustaPane.setText(text);
        primaryStage.show();
    }
}
