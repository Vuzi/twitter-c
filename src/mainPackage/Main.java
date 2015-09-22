package mainPackage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import screensManager.ScreenController;

public class Main extends Application {

    public static final String MAIN_SCREEN = "main";
    public static final String MAIN_SCREEN_FXML = "mainFrame.fxml";
    public static final String PIN_SCREEN = "pin";
    public static final String PIN_SCREEN_FXML = "pin.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception{

        ScreenController mainContainer = new ScreenController();
        mainContainer.loadScreen(Main.MAIN_SCREEN,
                Main.MAIN_SCREEN_FXML);
        mainContainer.loadScreen(Main.PIN_SCREEN,
                Main.PIN_SCREEN_FXML);

        mainContainer.setScreen(Main.PIN_SCREEN);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TwitterC");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
