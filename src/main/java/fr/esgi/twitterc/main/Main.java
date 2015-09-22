package fr.esgi.twitterc.main;

import fr.esgi.twitterc.view.controller.AppController;

import java.util.logging.Logger;

/**
 * App main controller and entry point.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class Main extends AppController {

    /**
     * Entry point.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        Logger.getLogger(Main.class.getName()).info("App started");
        Main.launch(Main.class, args);
    }

    @Override
    protected String getAppName() {
        return "TwitterC";
    }

    @Override
    protected String getFirstView() {
        return "TestViewA.fxml";
    }
}
