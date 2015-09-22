package screensManager;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import mainPackage.Main;
import twitterPackage.App;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class PinController implements Initializable, ControlledScreen {

    public TextField pinLabel;
    ScreenController myController;
    App twitterApi;
    /**
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        twitterApi.loadTwitter();
    }

    /**
     * @param screenParent
     */
    public void setScreenParent(ScreenController screenParent){
        myController = screenParent;
    }


    public void change(ActionEvent actionEvent) {
        if(twitterApi.testPin(pinLabel.getText()))
            myController.setScreen(Main.MAIN_SCREEN);
        else{
            pinLabel.clear();
            twitterApi.loadTwitter();
        }
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
