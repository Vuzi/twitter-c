package fr.esgi.twitterc.view.controller;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        createWindow(primaryStage, getAppName(), getFirstView(), null);
    }

    /**
     * TwitterClient constructor.
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
        return createWindow(new Stage(), title, mainPanel, null);
    }
    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     * @param mainPanelParameter The main panel parameters.
     */
    public WindowController createWindow(String title, String mainPanel, Map<String, Object> mainPanelParameter) {
        return createWindow(new Stage(), title, mainPanel, mainPanelParameter);
    }

    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param window The window to use.
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     * @param mainPanelParameter The main panel parameters.
     */
    public WindowController createWindow(Stage window, String title, String mainPanel, Map<String, Object> mainPanelParameter) {
        // The controller, AKA the window content
        final WindowController controller = new WindowController(this, mainPanel, mainPanelParameter);

        // Add and show
        Scene scene = new Scene(controller);
        window.setScene(scene);
        window.setTitle(title);
        window.setOnCloseRequest(event -> {
            // Delete the controller
            controller.onDeletion();
            windows.remove(controller);
        });
        //window.setMinHeight(600);
        //window.setMinWidth(800);
        window.show();

        // Add to the list
        windows.add(controller);

        return controller;
    }
}
