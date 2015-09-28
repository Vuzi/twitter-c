package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Profile view controller. Show a twitter profile information, its tweets, favorite tweets, users followed and followers.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class ProfileView extends ViewController {

    // XML values
    public AnchorPane profilePanel; // Background image
    public Pane profileImage;       // Profile image
    public Label userName;          // User long name
    public Label userTag;           // User tag name
    public Label following;         // Number of people following
    public Label followers;         // Number of people followed
    public Label favorites;         // Number of favorites tweets
    public Label tweets;            // Number of total tweets
    public ListView<Status> tweetListView; // List of tweets (used for favorites and tweets)
    public ListView<User> userListView;    // List of users (used for followers and followed users)
    public BorderPane listContainer;       // List container, used to switch between lists

    public static final String ID = "PROFILE";

    // Running values
    private ProfileViewType type = ProfileViewType.TWEETS; // Current type of view displayed
    private User user;                                     // Current user

    public HashMap<Status, TweetListView> cachedTweets = new HashMap<>(); // List of cached tweet cell view
    public ObservableList<Status> tweetList;                              // List of tweets

    public HashMap<User, UserListView> cachedUsers = new HashMap<>(); // List of cached user cell view
    public ObservableList<User> userList;                             // List of users

    @Override
    protected void onCreation() {
        // Prepare the tweet list
        tweetListView.setCellFactory(param -> new TweetListCell(this));
        tweetListView.setItems(tweetList = FXCollections.observableArrayList());

        // Prepare the user list
        userListView.setCellFactory(param -> new UserListCell(this));
        userListView.setItems(userList = FXCollections.observableArrayList());
    }

    @Override
    protected void onShow(Map<String, Object> params) {
        User u = (User) params.get("user");

        if(u == null) // Fallthrough case
            u = TwitterClient.get().getCurrentUser();

        updateInfo(u);
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
     * Return the list of cached tweet cells.
     *
     * @return The list of cached tweet cells.
     */
    public Map<Status, TweetListView> getCachedTweetCells() {
        return cachedTweets;
    }

    /**
     * Return the list of cached user cells.
     *
     * @return The list of cached user cells.
     */
    public Map<User, UserListView> getCachedUserCells() {
        return cachedUsers;
    }

    /**
     * Update the user information using the provided user.
     *
     * @param user The user.
     */
    private void updateInfo(User user) {
        Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("Profile view for user : {0}, {1}", user.getScreenName(), user.getName()));

        // Save user
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
     * Update the timeline information, according to the actual type of list of the controller.
     */
    private void updateTimeline() {

        switch (type) {
            case TWEETS:
                showAllTweets();
                break;
            case FAVORITES:
                showAllFavorites();
                break;
            case FOLLOWED:
                showAllSubscribed();
                break;
            case FOLLOWERS:
                showAllSubscribers();
                break;
        }

    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllSubscribed() {
        try {
            userList.setAll(TwitterClient.client().getFriendsList(user.getId(), -1));
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllSubscribers() {
        try {
            userList.setAll(TwitterClient.client().getFollowersList(user.getId(), -1));
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllTweets() {
        try {
            tweetList.setAll(TwitterClient.client().getUserTimeline(user.getId()));
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    /**
     * Show all the favorites tweets of the current user.
     */
    private void showAllFavorites() {
        try {
            tweetList.setAll(TwitterClient.client().getFavorites(user.getId()));
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }

    /**
     * Action when the "new tweet" button is clicked.
     */
    public void openNewTweetAction() {
        getAppController().createWindow("Nouveau tweet", "NewTweetView.fxml", Collections.singletonMap("user", user));
    }

    public void showFollowingAction() {
        if(type == ProfileViewType.FOLLOWED)
            return;

        following.getStyleClass().add("selectedTab");
        followers.getStyleClass().remove("selectedTab");
        favorites.getStyleClass().remove("selectedTab");
        tweets.getStyleClass().remove("selectedTab");

        listContainer.setCenter(userListView);

        type = ProfileViewType.FOLLOWED;
        updateTimeline();
    }

    public void showFollowersAction() {
        if(type == ProfileViewType.FOLLOWERS)
            return;

        followers.getStyleClass().add("selectedTab");
        following.getStyleClass().remove("selectedTab");
        favorites.getStyleClass().remove("selectedTab");
        tweets.getStyleClass().remove("selectedTab");

        listContainer.setCenter(userListView);

        type = ProfileViewType.FOLLOWERS;
        updateTimeline();
    }

    public void showFavoritesAction() {
        if(type == ProfileViewType.FAVORITES)
            return;

        favorites.getStyleClass().add("selectedTab");
        following.getStyleClass().remove("selectedTab");
        followers.getStyleClass().remove("selectedTab");
        tweets.getStyleClass().remove("selectedTab");

        listContainer.setCenter(tweetListView);

        type = ProfileViewType.FAVORITES;
        updateTimeline();
    }

    public void showTweetAction() {
        if(type == ProfileViewType.TWEETS)
            return;

        tweets.getStyleClass().add("selectedTab");
        following.getStyleClass().remove("selectedTab");
        followers.getStyleClass().remove("selectedTab");
        favorites.getStyleClass().remove("selectedTab");

        listContainer.setCenter(tweetListView);

        type = ProfileViewType.TWEETS;
        updateTimeline();
    }
}

/**
 * Enumeration of all the possible type of value that can take the main list of the profile view.
 */
enum ProfileViewType {
    /**
     * Will show all the tweets of the user.
     */
    TWEETS,

    /**
     * Will show all the favorites tweets of the user.
     */
    FAVORITES,

    /**
     * Will show all the subscribers of the user.
     */
    FOLLOWERS,

    /**
     * Will show all the subscriptions the user had made.
     */
    FOLLOWED
}


/**
 * Timeline list cell view. This cell will display all the information of the status given. This actually
 * only use the cached view from the profile view itself, only being a proxy between the list view and
 * our map of cached values.
 */
class UserListCell extends ListCell<User> {

    // View
    private final ProfileView parentController;

    /**
     * Timeline Tweet constructor.
     */
    public UserListCell(ProfileView parentController) {
        this.parentController = parentController;
    }

    @Override
    public void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        // No text content
        setText(null);

        // Graphical components
        if(!empty) {
            // Creating our customs view is too heavy for FX, so every created view is cached
            // and re-used when needed. The TweetListCell class is actually only used to given the
            // corresponding graphic according to the provided status
            UserListView controller = parentController.getCachedUserCells().get(user);

            if(controller == null) {
                // Create the controller
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/UserListView.fxml"));
                    fxmlLoader.load();
                    controller = fxmlLoader.getController();
                    controller.setController(parentController);
                    controller.update(user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Cache it
                parentController.getCachedUserCells().put(user, controller);
            }

            setGraphic(controller.getView());
        } else
            setGraphic(null);
    }

}

/**
 * Timeline list cell view. This cell will display all the information of the status given. This actually
 * only use the cached view from the profile view itself, only being a proxy between the list view and
 * our map of cached values.
 */
class TweetListCell extends ListCell<Status> {

    // View
    private final ProfileView parentController;

    /**
     * Timeline Tweet constructor.
     */
    public TweetListCell(ProfileView parentController) {
        this.parentController = parentController;
    }

    @Override
    public void updateItem(Status status, boolean empty) {
        super.updateItem(status, empty);

        // No text content
        setText(null);

        // Graphical components
        if(!empty) {
            // Creating our customs view is too heavy for FX, so every created view is cached
            // and re-used when needed. The TweetListCell class is actually only used to given the
            // corresponding graphic according to the provided status
            TweetListView controller = parentController.getCachedTweetCells().get(status);

            if(controller == null) {
                // Create the controller
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/TweetListView.fxml"));
                    fxmlLoader.load();
                    controller = fxmlLoader.getController();
                    controller.setController(parentController);
                    controller.update(status);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Cache it
                parentController.getCachedTweetCells().put(status, controller);
            }

            setGraphic(controller.getView());
        } else
            setGraphic(null);
    }

}