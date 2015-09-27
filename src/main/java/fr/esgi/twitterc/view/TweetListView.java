package fr.esgi.twitterc.view;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import twitter4j.Status;

/**
 * Created by Vuzi on 27/09/2015.
 */
public class TweetListView {

    // XML values
    public Pane profileImage;
    public Label userName;
    public Label userTag;
    public Label date;
    public Label content;
    public Pane tweetPanel;

    public Node getView() {
        return tweetPanel;
    }

    public void update(Status status) {

        // Set user real name
        userName.setText(status.getUser().getName());

        // Set user TAG
        userTag.setText("@" + status.getUser().getScreenName());

        // Set date
        date.setText(status.getCreatedAt().toString());

        // Set content
        if(status.isRetweet()) {
            content.setText(status.getRetweetedStatus().getText());
        } else {
            content.setText(status.getText());
        }

        // Set user image
        if(status.getUser().getProfileImageURL() != null) {

            Task<Image> imageLoading = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(status.getUser().getProfileImageURL());
                }
            };

            imageLoading.setOnSucceeded(event -> {
                Image image = imageLoading.getValue();

                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                Background background = new Background(backgroundImage);

                profileImage.setBackground(background);
            });

            imageLoading.run();
        }
    }

}
