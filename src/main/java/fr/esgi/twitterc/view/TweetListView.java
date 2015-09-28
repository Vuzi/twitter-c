package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.AppController;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Controller of a tweet contained in a tweet list view.
 *
 * Created by Vuzi on 27/09/2015.
 */
public class TweetListView {

    // XML values
    public Pane profileImage;
    public Label userName;
    public Label userTag;
    public Label date;
    public TextFlow content;
    public Pane tweetPanel;

    // Running values
    private Status status;
    private Image userImage;
    private AppController appController;

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

        // Set user real name
        userName.setText(status.getUser().getName());

        // Set user TAG
        userTag.setText("@" + status.getUser().getScreenName());

        // Set date
        date.setText(status.getCreatedAt().toString());

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

            Task<Image> imageLoading = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(status.getUser().getProfileImageURL());
                }
            };

            imageLoading.setOnSucceeded(event -> {
                userImage = imageLoading.getValue();

                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(userImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileImage.setBackground(new Background(backgroundImage));
            });

            imageLoading.run();
        }
    }

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
                textElement.setOnMouseClicked(event -> {
                    try {
                        User u = TwitterClient.client().showUser(element.substring(1));
                        if (u != null)
                            appController.createWindow("Profil", "ProfilView.fxml", Collections.singletonMap("user", u));
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }
            // HashTags
            else if(element.startsWith("#")) {
                textElement.setText(element);
                textElement.setFill(Color.GRAY);
                textElement.setOnMouseEntered(event -> textElement.setStyle("-fx-text-fill: lightgray; -fx-cursor: hand"));
                textElement.setOnMouseExited(event  -> textElement.setStyle("-fx-text-fill: gray; -fx-cursor: inherit"));
                textElement.setOnMouseClicked(event -> System.out.println("TODO > " + element));
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
    }

    /**
     * Action when the "retweet" button is clicked.
     */
    public void retweetAction() {

        if(status == null)
            return;

        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation retweet");
        alert.setHeaderText("Confirmation de retweet");

        if(userImage != null)
            alert.setGraphic(new ImageView(userImage));

        alert.setContentText(MessageFormat.format("Êtes-vous certain de vouloir retweeter le message de {0} :\n\"{1}\" ?",
                status.getUser().getName(), status.isRetweet() ? status.getRetweetedStatus().getText() : status.getText()));

        if(alert.showAndWait().get() == ButtonType.OK) {
            try {
                TwitterClient.client().retweetStatus(status.getId());

                // Show success
                alert.setTitle("Retweet effectué");
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Retweet effectué avec succès !");
                alert.showAndWait();

            } catch (TwitterException e) {
                Logger.getLogger(this.getClass().getName()).info("Error while retweeting status : " + e.getMessage());
                e.printStackTrace();

                // Show that an error occurred
                alert.setTitle("Erreur durant le retweet");
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Le retweet n'a pas pu être effectué...");
                alert.showAndWait();
            }
        }
    }

    /**
     * Action when the "favorite" button is clicked.
     */
    public void addFavoriteAction() {
    }

    /**
     * Action when the "see" button is clicked.
     */
    public void seeDetailAction() {

    }
}
