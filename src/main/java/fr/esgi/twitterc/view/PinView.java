package fr.esgi.twitterc.view;

import fr.esgi.twitterc.view.controller.ViewController;
import fr.esgi.twitterc.apiEngine.*;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import static fr.esgi.twitterc.apiEngine.App.*;

public class PinView extends ViewController {

    public TextField pinLabel;
    public static String ID = "PIN";
    /**
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        App.loadTwitter();
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onShow() {

    }

    @Override
    protected void onHide() {

    }

    @Override
    protected void onDeletion() {

    }

    @Override
    protected String getID() {
        return null;
    }

    public void change(ActionEvent actionEvent) {
        if(testPin(pinLabel.getText())) {
            getWindowController().showOrReuseView("TimelineView.fxml", PinView.ID);
        }
        else{
            pinLabel.clear();
            App.loadTwitter();
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
