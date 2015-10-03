package fr.esgi.twitterc.view;

import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.AppController;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import twitter4j.User;

/**
 * Controller of an user contained in an user list view.
 *
 * Created by Vuzi on 27/09/2015.
 */
public class UserListView {

    // XML values
    public Pane profileImage;
    public Label userName;
    public Label userTag;
    public Pane tweetPanel;
    public Label tweets;
    public Label followers;
    public Label following;

    // Running values
    private User user;
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
     * Update the view with the provided user.
     *
     * @param user The user.
     */
    public void update(User user) {
        this.user = user;

        // Set user real name
        userName.setText(user.getName());

        // Set user TAG
        userTag.setText("@" + user.getScreenName());

        // Set user image
        userImage = null;

        if(user.getProfileImageURL() != null) {
            Utils.asyncTask(() -> new Image(user.getProfileImageURL()), image -> {
                userImage = image;
                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(userImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileImage.setBackground(new Background(backgroundImage));
            });
        }

        // Set tweet number
        tweets.setText(String.valueOf(user.getStatusesCount()));

        // Set follower number
        followers.setText(String.valueOf(user.getFollowersCount()));

        // Set following number
        following.setText(String.valueOf(user.getFriendsCount()));
    }

    /**
     * Action when the "send tweet" button is clicked.
     */
    public void respondAction() {
        // TODO
    }

    /**
     * Action when the "see" button is clicked. This action will create a new user view.
     */
    public void showUserAction() {
        Utils.showProfilePage(appController, user);
    }
}
