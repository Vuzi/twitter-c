package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;

import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.User;

import java.util.Map;
import java.util.logging.Logger;

/**
 * New tweet view. Show an empty tweet with an editable content.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class NewTweetView extends ViewController {

    // XML values
    public Pane profileImage;
    public Label userName;
    public Label userTag;
    public Label remainingCharacters;
    public TextArea tweetValue;
    public Button sendButton;
    public Button cancelButton;

    public static final String ID = "TWEET-VIEW";

    private User user;                // User sending the tweet
    private Status relatedStatus;     // Reply to
    private User relatedUser;         // User

    @Override
    protected void onCreation() {}

    @Override
    protected void onShow(Map<String, Object> params) {
        this.user = TwitterClient.get().getCurrentUser();
        this.relatedUser = (User) params.get("user");
        this.relatedStatus = (Status) params.get("status");

        updateInfo();
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
     */
    private void updateInfo() {
        Logger.getLogger(this.getClass().getName()).info("New tweet view for current user");

        // Set user real name
        userName.setText(user.getName());

        // Set user TAG
        userTag.setText("@" + user.getScreenName());

        // Set profile image
        if(user.getProfileImageURL() != null) {
            Task<Image> profileImageLoader = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(user.getProfileImageURL());
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

        // Set remaining chars
        remainingCharacters.setText("140 caractères restants");

        // Set listener on the tweet content
        tweetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            update(newValue);
        });

        if(relatedStatus != null) {
            tweetValue.setText("@" + relatedStatus.getUser().getScreenName() + " ");
            tweetValue.positionCaret(tweetValue.getText().length());
        } else if(relatedUser != null) {
            tweetValue.setText("@" + relatedUser.getScreenName() + " ");
            tweetValue.positionCaret(tweetValue.getText().length());
        }
    }

    /**
     * Update the panel information with the provided tweet text.
     *
     * @param tweet The tweet textual content.
     */
    private void update(String tweet) {
        int remaining = 140 - tweet.length();

        // Update the remaining characters
        if(remaining >= 0) {
            if(remaining <= 1)
                remainingCharacters.setText(remaining + " caractère restant");
            else
                remainingCharacters.setText(remaining + " caractères restants");
            remainingCharacters.setStyle("-fx-text-fill: inherit");
        } else {
            remainingCharacters.setText((-remaining) + " caractère(s) en trop");
            remainingCharacters.setStyle("-fx-text-fill: darkred");
        }

        // Set the buttons enable
        sendButton.setDisable(remaining < 0);
    }

    /**
     * Action when the "send" button is selected.
     */
    public void sendAction() {
        Utils.asyncTask(() -> {
                    StatusUpdate statusUpdate = new StatusUpdate(tweetValue.getText());
                    if(relatedStatus != null)
                        statusUpdate.setInReplyToStatusId(relatedStatus.getId());
                    return TwitterClient.client().updateStatus(statusUpdate);
                },
                status -> getWindowController().close());
    }

    /**
     * Action when the "cancel" button is selected.
     */
    public void cancelAction() {
        // Just close the current window
        getWindowController().close();
    }
}
