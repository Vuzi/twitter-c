package fr.esgi.twitterc.view;

import fr.esgi.twitterc.apiEngine.App;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.net.URL;
import java.util.*;
import java.util.TimerTask;
import java.util.Timer;

/**
 * Created by Emerich on 22/09/2015.
 */
public class TimelineView extends ViewController {

    public ListView listview;
    public TextField tweetLabel;
    public ImageView userProfil;
    public static String ID = "TIMELINE";
    public Label tagName;
    private List<Status> timeline;
    ArrayList<String> items = null;
    ObservableList<String> itemsObservable ;


   /**
    * @param url
    * @param rb
    */
    public void initialize(URL url, ResourceBundle rb) {
        items = new ArrayList<String>();
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onShow() {
        // Set user profil image
        String imageURL = App.getUserProfilImage();
        if(imageURL != null) {
            imageURL =  imageURL.replace("_normal","");
            Image image = new Image(imageURL);
            userProfil.setImage(image);
        }

        // Set user Tag Name
        tagName.setText("@" + App.getUser().getScreenName());

        updateInfo();
    /*
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateInfo();
            }
        }, 0, 120000); // Every 2 */
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

    public void mouseClicked(Event event) {
        listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().name().equals("PRIMARY")) {
                    if (mouseEvent.getClickCount() == 2) {
                        App.TWEETNUMBER = timeline.get(listview.getSelectionModel().getSelectedIndex()).getId();
                        getAppController().createWindow("Twwet", "TweetView.fxml");
                    }
                }
            }
        });
    }

    public void sendTweet(ActionEvent actionEvent) {
        String content = tweetLabel.getText();
        if(App.updateStatus(content))
            tweetLabel.setText("");
    }

    public void userProfilClicked(Event event) {
        getAppController().createWindow("Profil", "ProfilView.fxml");
    }

    private void updateInfo() {
        try {
            timeline = null;
            timeline = App.TWITTER.getHomeTimeline();

            for (twitter4j.Status status3 : timeline) {
                items.add(statusMAker(status3));
            }

            itemsObservable = FXCollections.observableList(items);

            listview.setItems(itemsObservable);
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    private String statusMAker( Status status){
        String result  =  "\nDate : " + status.getCreatedAt().toString() + "\n" +
                "By   : @" + status.getUser().getScreenName() + " (" + status.getUser().getName() + ")" +
                "\n" +status.getText() + "\n";
        return result;
    }
}
