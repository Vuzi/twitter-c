package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Profile view. Show a twitter profile information, and its timeline.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class ProfileView extends ViewController {

    // XML values
    public AnchorPane profilePanel;
    public Pane profileImage;
    public Label userName;
    public Label userTag;
    public Label following;
    public Label followers;
    public Label favorites;
    public Label tweets;
    public ListView<Status> tweetList;

    public static final String ID = "PROFILE";
    public ObservableList<Status> statusList;
    private User user;

    @Override
    protected void onCreation() {
        tweetList.setCellFactory(param -> new TweetListCell(this));
        tweetList.setItems(statusList = FXCollections.observableArrayList());
    }

    @Override
    protected void onShow(Map<String, Object> params) {
        updateInfo((User) params.get("user"));
    }

    @Override
    protected void onHide() {}

    @Override
    protected void onDeletion() {}

    @Override
    protected String getID() {
        return ID;
    }

    /**
     * Update the user information using the provided user.
     *
     * @param user The user.
     */
    private void updateInfo(User user) {
        this.user = user;

        // Set user real name
        userName.setText(user.getName());

        // Set user TAG
        userTag.setText("@" + user.getScreenName());

        // Set the banner image
        if(user.getProfileBannerURL() != null){
            Task<Image> profileBannerLoader = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(user.getProfileBannerURL().replace("web","1500x500"));
                }
            };

            profileBannerLoader.setOnSucceeded(event -> {
                Image image = profileBannerLoader.getValue();

                BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
                profilePanel.setBackground(new Background(backgroundImage));
            });

            profileBannerLoader.run();
        }

        // Set profile image
        if(user.getProfileImageURL() != null) {
            Task<Image> profileImageLoader = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(user.getProfileImageURL().replace("_normal",""));
                }
            };

            profileImageLoader.setOnSucceeded(event -> {
                Image image = profileImageLoader.getValue();

                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileImage.setBackground(new Background(backgroundImage));
            });

            profileImageLoader.run();
        }

        // Set following count
        following.setText(String.valueOf(user.getFriendsCount()));

        // Set followers count
        followers.setText(String.valueOf(user.getFollowersCount()));

        // Set tweets count
        tweets.setText(String.valueOf(user.getStatusesCount()));

        // Set favorites count
        favorites.setText(String.valueOf(user.getFavouritesCount()));

        // Update timeline
        updateTimeline();
    }

    /**
     * Update the timeline informations.
     */
    private void updateTimeline() {
        try {
            statusList.setAll(TwitterClient.client().getUserTimeline(user.getId()));
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    /**
     * Action when the "new tweet" button is clicked.
     *
     * @param actionEvent The action event.
     */
    public void openNewTweetAction(ActionEvent actionEvent) {
        getAppController().createWindow("Nouveau tweet", "NewTweetView.fxml", Collections.singletonMap("user", user));
    }
}

/**
 * Timeline list cell view. This cell will display all the information of the status given.
 */
class TweetListCell extends ListCell<Status> {

    // View
    private final TweetListView controller;

    /**
     * Timeline Tweet constructor.
     */
    public TweetListCell(ProfileView parentController) {
        // Prepare view
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/TweetListView.fxml"));
            fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add listener
        // TODO
    }

    @Override
    public void updateItem(Status status, boolean empty) {
        super.updateItem(status, empty);

        // No text content
        setText(null);

        // Graphical components
        if(!empty) {
            controller.update(status);
            setGraphic(controller.getView());
        } else
            setGraphic(null);
    }

}