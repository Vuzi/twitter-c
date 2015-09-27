package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * Time Line view.
 *
 * Created by Emerich on 22/09/2015.
 */
public class TimelineView extends ViewController {

    // XML values
    public ListView<Status> statusList;
    public TextField tweetTextField;
    public ImageView userImage;
    public Label userLabel;

    // Internal values
    public static String ID = "TIMELINE";
    public ObservableList<Status> itemsObservable;
    private Timer updateTimer;

    /**
     * Initialization of the view controller, called when created from the XML view.
     *
     * @param url The URL used.
     * @param rb The resource bundle used.
     */
    public void initialize(URL url, ResourceBundle rb) {
        final TimelineView me = this;

        // Prepare the item list and the cell factory
        statusList.setCellFactory(param -> new TimelineTweetView(me));
        statusList.setItems(itemsObservable = FXCollections.observableArrayList());
    }

    @Override
    protected void onCreation() {}

    @Override
    protected void onShow(Map<String, Object> params) {
        // Set user profile image
        String imageURL = TwitterClient.get().getCurrentUser().getProfileImageURL();

        if(imageURL != null) {
            imageURL =  imageURL.replace("_normal","");
            Image image = new Image(imageURL);
            userImage.setImage(image);
        }

        // Set user Tag Name
        userLabel.setText("@" + TwitterClient.get().getCurrentUser().getScreenName());

        // Start a time to update the timeline every 10 seconds
        updateTimeline();
        /*
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimeline();
            }
        }, 0, 10000);*/
    }

    @Override
    protected void onHide() {}

    @Override
    protected void onDeletion() {
        // Stop the update timer
        // updateTimer.cancel();
    }

    @Override
    protected String getID() {
        return ID;
    }

    /**
     * Update the timeline information.
     */
    private void updateTimeline() {
        try {
            itemsObservable.setAll(TwitterClient.client().getHomeTimeline());
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    /**
     * Action to send a tweet.
     *
     * @param actionEvent The action event.
     */
    public void sendTweetAction(ActionEvent actionEvent) {
        String content = tweetTextField.getText().trim();

        if(content.isEmpty())
            return;

        try {
            TwitterClient.client().updateStatus(content);
        } catch (TwitterException e) {
            Logger.getLogger(this.getClass().getName()).info("Error while update status : " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred during the status update.");
            alert.showAndWait();
        }
    }

    /**
     * Action to show the user profile.
     *
     * @param event
     */
    public void userProfileAction(Event event) {
        showUserProfile(TwitterClient.get().getCurrentUser());
    }

    /**
     * Show the profile of the author of the provided status.
     *
     * @param status The status of the author to show.
     */
    public void showUserProfile(Status status) {
        showUserProfile(status.getUser());
    }

    /**
     * Show the provided status in a new windows.
     *
     * @param user The user to show.
     */
    public void showUserProfile(User user) {
        getAppController().createWindow("Profil", "ProfilView.fxml", Collections.singletonMap("user", user));
    }
}

/**
 * Timeline list cell view. This cell will display all the information of the status given.
 */
class TimelineTweetView extends ListCell<Status> {

    // View
    private final GridPane grid = new GridPane();
    private final Label nameLabel = new Label();
    private final Label dateLabel = new Label();
    private final Label contentLabel = new Label();

    /**
     * Timeline Tweet constructor.
     */
    public TimelineTweetView(TimelineView controller) {
        // Prepare grid
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));

        // Add elements to the grid
        grid.add(nameLabel, 0, 0);
        grid.add(dateLabel, 1, 0);
        grid.add(contentLabel, 0, 1, 2, 1);

        // Add listener
        nameLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> controller.showUserProfile(getItem()));
        nameLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            nameLabel.setStyle("-fx-text-fill: darkgray");
            getScene().setCursor(Cursor.HAND);
        });
        nameLabel.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            nameLabel.setStyle("-fx-text-fill: inherit");
            getScene().setCursor(Cursor.DEFAULT);
        });

        // TODO : Reply and retweet button
    }

    @Override
    public void updateItem(Status status, boolean empty) {
        super.updateItem(status, empty);

        // No text content
        setText(null);

        // Graphical components
        if(!empty) {
            nameLabel.setText("By   : @" + status.getUser().getScreenName() + " (" + status.getUser().getName() + ")");
            dateLabel.setText("Date : " + status.getCreatedAt());

            if(status.isRetweet())
                contentLabel.setText(status.getRetweetedStatus().getText());
            else
                contentLabel.setText(status.getText());

            setGraphic(grid);
        } else
            setGraphic(null);
    }

}