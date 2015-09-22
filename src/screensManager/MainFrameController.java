package screensManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import mainPackage.Main;
import twitterPackage.App;

import javax.swing.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Emerich on 22/09/2015.
 */
public class MainFrameController implements Initializable, ControlledScreen {

    public ListView listview;
    public TextField twwetLabel;
    public ImageView userProfil;

    ScreenController myController;
    App twitterApi;

    /**
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        List<String> items = Arrays.asList("One", "Two", "Three");
        ObservableList<String> itemsObservable = FXCollections.observableList(items);
        listview.setItems(itemsObservable);

    }

    /**
     * @param screenParent
     */
    public void setScreenParent(ScreenController screenParent) {
        myController = screenParent;
    }

    public static void showned() {

    }

    public void change(ActionEvent actionEvent) {
        System.out.println(App.getUserProfilImage());
        Image image = new Image(App.getUserProfilImage());
        userProfil.setImage(image);
        //myController.setScreen(Main.PIN_SCREEN);
    }

    public void mouseClicked(Event event) {
        listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().name().equals("PRIMARY")) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.printf(listview.getSelectionModel().getSelectedItem().toString());
                    }
                }
            }
        });
    }

    public void sendTweet(ActionEvent actionEvent) {
        String content = twwetLabel.getText();
        if(App.updateStatus(content))
            twwetLabel.setText("");
    }

    public void userProfilClicked(Event event) {
        System.out.println("profiiiiiiiiiiiiiiiiiiiiiiiil");
    }
}
