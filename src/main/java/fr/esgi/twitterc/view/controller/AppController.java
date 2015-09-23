package fr.esgi.twitterc.view.controller;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Main controller of the application.
 *
 * Created by Vuzi on 22/09/2015.
 */
public abstract class AppController extends Application {

    private List<WindowController> windows;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Logger.getLogger(this.getClass().getName()).info("Application controller started");
        createWindow(primaryStage, getAppName(), getFirstView());
    }

    /**
     * App constructor.
     */
    public AppController() {
        this.windows = new ArrayList<>();
    }

    /**
     * Return the name of the application.
     *
     * @return The name of the application.
     */
    protected abstract String getAppName();

    /**
     * Return the name of the first view.
     *
     * @return The name of the first view.
     */
    protected abstract String getFirstView();

    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     */
    public WindowController createWindow(String title, String mainPanel) {
        return createWindow(new Stage(), title, mainPanel);
    }

    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param window The window to use.
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     */
    public WindowController createWindow(Stage window, String title, String mainPanel) {
        // The controller, AKA the window content
        final WindowController controller = new WindowController(this, mainPanel);

        // Add and show
        Group root = new Group();
        root.getChildren().add(controller);
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setTitle(title);
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Delete the controller
                controller.onDeletion();
                windows.remove(controller);
            }
        });
        window.show();

        // Add to the list
        windows.add(controller);

        return controller;
    }
}
