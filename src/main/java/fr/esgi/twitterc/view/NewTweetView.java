package fr.esgi.twitterc.view;

import fr.esgi.twitterc.view.controller.ViewController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import twitter4j.User;

import java.util.Map;

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
    private User user;

    @Override
    protected void onCreation() {}

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
        remainingCharacters.setText("140 caractères restant");

        // Set listener on the tweet content
        tweetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            update(newValue);
        });
    }

    /**
     * Update the panel information with the provided tweet text.
     *
     * @param tweet The tweet textual content.
     */
    private void update(String tweet) {
        int remaining = 140 - tweet.length();

        System.out.println(remaining);

        // Update the remaining characters
        if(remaining >= 0) {
            if(remaining <= 1)
                remainingCharacters.setText(remaining + " caractère restant");
            else
                remainingCharacters.setText(remaining + " caractères restant");
            remainingCharacters.setStyle("-fx-text-fill: inherit");
        } else {
            remainingCharacters.setText((-remaining) + " caractères en trop");
            remainingCharacters.setStyle("-fx-text-fill: darkred");
        }

        // Set the buttons enable
        sendButton.setDisable(remaining < 0);
    }

    /**
     * Action when the "send" button is selected.
     *
     * @param actionEvent The action event.
     */
    public void sendAction(ActionEvent actionEvent) {
        // TODO
        System.out.println("Send tweet here for " + user.getName());
    }

    /**
     * Action when the "cancel" button is selected.
     *
     * @param actionEvent The action event.
     */
    public void cancelAction(ActionEvent actionEvent) {
        // Just close the current window
        getWindowController().close();
    }
}
