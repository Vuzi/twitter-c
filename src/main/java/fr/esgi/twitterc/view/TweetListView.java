package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.main.TwitterCController;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.component.TwitterMediaVideoView;
import fr.esgi.twitterc.view.component.TwitterMediaView;
import fr.esgi.twitterc.view.controller.AppController;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import twitter4j.ExtendedMediaEntity;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller of a tweet contained in a tweet list view.
 *
 * Created by Vuzi on 27/09/2015.
 */
public class TweetListView {

    // XML values
    public Pane profileImage;
    public HBox retweetedPanel;
    public Label retweetedBy;
    public Label userName;
    public Label userTag;
    public Label date;
    public TextFlow content;
    public Pane tweetPanel;
    public Button seeDetailButton;
    public Button addFavoriteButton;
    public Button retweetButton;
    public Button respondButton;
    public HBox responseAtPanel;
    public Label responseTo;
    public HBox hasResponsePanel;
    public Label responses;
    public VBox medias;

    // Running values
    private Status status;                 // Status displayed
    private Image userImage;               // Status' author's image
    private AppController appController;   // Application controller
    private User author;                   // Status' author
    private Status responseToValue;        // Status answered by the current status

    /**
     * Set the controller.
     *
     * @param controller The parent controller.
     */
    public void setController(ViewController controller) {
        this.appController = controller.getAppController();
    }

    /**
     * Return the view of the controller.
     *
     * @return The view.
     */
    public Node getView() {
        return tweetPanel;
    }

    /**
     * Update the view with the provided status.
     *
     * @param status The status.
     */
    public void update(Status status) {
        this.status = status;
        this.author = status.isRetweet() ? status.getRetweetedStatus().getUser() : status.getUser();

        // Set retweet information
        if(status.isRetweet()) {
            retweetedPanel.setVisible(true);
            retweetedPanel.setManaged(true);
            retweetedBy.setText(status.getUser().getName());
        } else {
            retweetedPanel.setVisible(false);
            retweetedPanel.setManaged(false);
        }

        // Set response information
        if(status.getInReplyToStatusId() >= 0) {
            responseAtPanel.setVisible(true);
            responseAtPanel.setManaged(true);

            Utils.asyncTask(() -> TwitterClient.client().showStatus(status.getInReplyToStatusId()), replied -> {
                if(replied != null) {
                    responseTo.setText("@" + replied.getUser().getScreenName());
                    responseToValue = replied;
                }
            });
        } else {
            responseAtPanel.setVisible(false);
            responseAtPanel.setManaged(false);
        }

        // Set user real name
        userName.setText(author.getName());

        // Set user TAG
        userTag.setText("@" + author.getScreenName());

        // Set date
        date.setText(Utils.formatDate(status.getCreatedAt()));

        // Set content
        content.getChildren().clear();
        if(status.isRetweet()) {
            updateContent(status.getRetweetedStatus().getText());
        } else {
            updateContent(status.getText());
        }

        // Set user image
        userImage = null;

        if(status.getUser().getProfileImageURL() != null) {
            Utils.asyncTask(() -> new Image(author.getProfileImageURL()), image -> {
                userImage = image;
                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(userImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileImage.setBackground(new Background(backgroundImage));
            });
        }

        // Update retweet button
        if(status.getUser().getId() == TwitterClient.get().getCurrentUser().getId())
            retweetButton.setDisable(true);
        retweetButton.setText("Retweeter (" + status.getRetweetCount() + ")");

        // Update favorite button
        if(status.isFavorited())
            addFavoriteButton.setText("Retirer des favoris (" + status.getFavoriteCount() + ")");
        else
            addFavoriteButton.setText("Ajouter aux favoris (" + status.getFavoriteCount() + ")");

        // Update media
        medias.getChildren().clear();
        for(ExtendedMediaEntity mediaEntity : status.getExtendedMediaEntities()) {
            if(mediaEntity.getType().equals("photo")) {
                Utils.asyncTask(() -> new Image(mediaEntity.getMediaURL()), image -> {
                    if(image != null)
                        medias.getChildren().add(new TwitterMediaView(image));
                });
            } else if(mediaEntity.getType().equals("video") || mediaEntity.getType().equals("animated_gif")) {
                // Multiples videos can be given, try to get the better MP4 one
                ExtendedMediaEntity.Variant selectedVariant = null;
                for(ExtendedMediaEntity.Variant variant : mediaEntity.getVideoVariants()) {
                    if(variant.getContentType().equals("video/mp4")) {
                        if(selectedVariant == null || variant.getBitrate() > selectedVariant.getBitrate())
                            selectedVariant = variant;
                    }
                }

                // If video, show image preview
                if(selectedVariant != null) {
                    final ExtendedMediaEntity.Variant finalSelectedVariant = selectedVariant;
                    Utils.asyncTask(() -> new Image(mediaEntity.getMediaURL()), image -> {
                        if(image != null)
                            medias.getChildren().add(new TwitterMediaVideoView(image, "http" + finalSelectedVariant.getUrl().substring(5)));
                    });
                }
            }

        }

        // Set responses information
        // Too much data ?
        /*
        Utils.asyncTask(this::getResponses, statuses -> {
            if(statuses.isEmpty()) {
                hasResponsePanel.setVisible(false);
                hasResponsePanel.setManaged(false);
            } else {
                hasResponsePanel.setVisible(true);
                hasResponsePanel.setManaged(true);

                responses.setText(String.valueOf(statuses.size()));
            }
        });*/
    }

    private List<Status> getResponses() {
        ArrayList<Status> replies = new ArrayList<>();

        try {
            long id = status.getId();
            String screenName = status.getUser().getScreenName();

            Query query = new Query("@" + screenName + " since_id:" + id);
            query.setCount(100);

            replies.addAll(TwitterClient.client().search(query)
                    .getTweets().stream()
                    .filter(status -> status.getInReplyToStatusId() == id)
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return replies;
    }

    /**
     * Update the tweet content using the provided string.
     *
     * @param text The tweet content.
     */
    private void updateContent(String text) {
        for(String element : Utils.parseTweet(text)) {
            if(element.isEmpty())
                continue;

            Text textElement = new Text();

            // User tag
            if(element.startsWith("@")) {
                textElement.setText(element);
                textElement.setFill(Color.LIGHTBLUE);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: darkblue; -fx-cursor: hand"));
                textElement.setOnMouseExited(event  -> textElement.setStyle("-fx-text-fill: lightblue; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> Utils.asyncTask(() -> TwitterClient.client().showUser(element.substring(1)), user -> {
                    if (user != null)
                        Utils.showProfilePage(appController, user);
                }));
            }
            // HashTags
            else if(element.startsWith("#")) {
                textElement.setText(element);
                textElement.setFill(Color.GRAY);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: lightgray; -fx-cursor: hand"));
                textElement.setOnMouseExited(event -> textElement.setStyle("-fx-text-fill: gray; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event ->
                        appController.createWindow("Recherche", "SearchView.fxml", Collections.singletonMap("filter", element)));
            }
            // URLs
            else {
                if (element.startsWith("http://") || element.startsWith("https://")) {
                    textElement.setText(element);
                    textElement.setFill(Color.LIGHTGREEN);
                    textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: darkgreen; -fx-cursor: hand"));
                    textElement.setOnMouseExited(event  -> textElement.setStyle("-fx-text-fill: lightgreen; -fx-cursor: inherit"));
                    textElement.setOnMouseClicked(event -> Utils.openWebPage(element));
                }
                // Regular text
                else {
                    textElement.setText(element);
                }
            }

            content.getChildren().add(textElement);
        }
    }

    /**
     * Action when the "respond" button is clicked.
     */
    public void respondAction() {
        Utils.showNewTweetPage(appController, status);
    }

    /**
     * Action when the "retweet" button is clicked.
     */
    public void retweetAction() {

        if(status == null)
            return;

        Utils.asyncTask(() -> TwitterClient.client().retweetStatus(status.getId()), status -> {
            ((TwitterCController) appController).showNotification("Retweet", "Retweet effectué avec succès !");

            this.status = status;
            update(status);
        });
    }

    /**
     * Action when the "favorite" button is clicked.
     */
    public void addFavoriteAction() {

        if(status == null)
            return;

        if(status.isFavorited()) {
            Utils.asyncTask(() -> TwitterClient.client().destroyFavorite(status.getId()), status -> {
                ((TwitterCController) appController).showNotification("Confirmation favoris",
                        "Suppression des favoris du tweet de " + status.getUser().getName() + " effectué avec succès !");

                this.status = status;
                update(status);
            });
        } else {
            Utils.asyncTask(() -> TwitterClient.client().createFavorite(status.getId()), status -> {
                ((TwitterCController) appController).showNotification("Confirmation favoris",
                        "Mise en favoris du tweet de " + status.getUser().getName() + " effectué avec succès !");

                this.status = status;
                update(status);
            });
        }
    }

    /**
     * Action when the "see" button is clicked.
     */
    public void seeDetailAction() {
        Utils.showTweetPage(appController, status);
    }

    /**
     * Action when the user name is clicked.
     */
    public void showUserAction() {
        Utils.showProfilePage(appController, author);
    }

    /**
     * Action when the retweet user name is clicked.
     */
    public void showRetweetUserAction() {
        Utils.showProfilePage(appController, status.getUser());
    }

    /**
     * Action when the response to label is clicked.
     */
    public void showResponseTweetAction() {
        if(responseToValue != null)
            Utils.showTweetPage(appController, responseToValue);
    }

    /**
     * Action when the response author to label is clicked.
     */
    public void showResponseUserAction() {
        if(responseToValue != null)
            Utils.showProfilePage(appController, responseToValue.getUser());
    }

    public void seeTweetOnTweeter() {
        Utils.openWebPage("https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());
    }
}
