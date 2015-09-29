package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import twitter4j.Status;
import twitter4j.User;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
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

    public VBox tweetListView;      // List of views

    public static final String ID = "PROFILE";

    // Running values
    private ProfileViewType type = ProfileViewType.TWEETS; // Current type of view displayed
    private User user;                                     // Current user

    public ObservableList<Status> tweetList;               // List of tweets
    public ObservableList<User> userList;                  // List of users

    // Performance test
    public ClassLoader cachingClassLoader = new MyClassLoader(FXMLLoader.getDefaultClassLoader());


    @Override
    protected void onCreation() {
        // Prepare the tweet list
        tweetList = FXCollections.observableArrayList();

        // Map our list to the Vbox children
        Utils.mapByValue(tweetList, tweetListView.getChildren(), status -> {
            try {
                // Load XML with custom loader
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/TweetListView.fxml"));
                fxmlLoader.setClassLoader(this.cachingClassLoader);
                fxmlLoader.load();

                // Get controller & update
                TweetListView controller = fxmlLoader.getController();
                controller.setController(this);
                controller.update(status);
                return controller.getView();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Error case
            return null;
        });

        // Prepare the tweet list
        userList = FXCollections.observableArrayList();

        // Map our list to the Vbox children
        Utils.mapByValue(userList, tweetListView.getChildren(), user -> {
            try {
                // Load XML with custom loader
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/UserListView.fxml"));
                fxmlLoader.setClassLoader(this.cachingClassLoader);
                fxmlLoader.load();

                // Get controller & update
                UserListView controller = fxmlLoader.getController();
                controller.setController(this);
                controller.update(user);
                return controller.getView();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Error case
            return null;
        });
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

        // Set the banner image (async)
        if(user.getProfileBannerURL() != null){
            Utils.asyncTask(() -> new Image(user.getProfileBannerURL().replace("web", "1500x500")), image -> {
                BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
                profilePanel.setBackground(new Background(backgroundImage));
            });
        }

        // Set profile image (async)
        if(user.getProfileImageURL() != null) {
            Utils.asyncTask(() -> new Image(user.getProfileImageURL().replace("_normal","")), image -> {
                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileImage.setBackground(new Background(backgroundImage));
            });
        }

        // Set following count
        following.setText(String.valueOf(user.getFriendsCount()));

        // Set followers count
        followers.setText(String.valueOf(user.getFollowersCount()));

        // Set tweets count
        tweets.setText(String.valueOf(user.getStatusesCount()));

        // Set favorites count
        favorites.setText(String.valueOf(user.getFavouritesCount()));

        // Update timeline (async)
        updateTimeline();
    }

    /**
     * Update the timeline information, according to the actual type of list of the controller.
     */
    private void updateTimeline() {

        // Empty all the lists
        tweetList.clear();
        userList.clear();

        // Update result accordingly
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
        Utils.asyncTask(() -> TwitterClient.client().getFriendsList(user.getId(), -1), userList::setAll);
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllSubscribers() {
        Utils.asyncTask(() -> TwitterClient.client().getFollowersList(user.getId(), -1), userList::setAll);
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllTweets() {
        Utils.asyncTask(() -> TwitterClient.client().getUserTimeline(user.getId()), tweetList::setAll);
    }

    /**
     * Show all the favorites tweets of the current user.
     */
    private void showAllFavorites() {
        Utils.asyncTask(() -> TwitterClient.client().getFavorites(user.getId()), tweetList::setAll);
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

class MyClassLoader extends ClassLoader{
    private final Map<String, Class> classes = new HashMap<>();
    private final ClassLoader parent;

    public MyClassLoader(ClassLoader parent) {
        this.parent = parent;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> c = findClass(name);
        if ( c == null ) {
            throw new ClassNotFoundException( name );
        }
        return c;
    }

    @Override
    protected Class<?> findClass( String className ) throws ClassNotFoundException {
        if (classes.containsKey(className)) {
            return classes.get(className);
        } else {
            try {
                Class<?> result = parent.loadClass(className);
                classes.put(className, result);
                return result;
            } catch (ClassNotFoundException ignore) {
                classes.put(className, null);
                return null;
            }
        }
    }

    // ========= delegating methods =============
    @Override
    public URL getResource( String name ) {
        return parent.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources( String name ) throws IOException {
        return parent.getResources(name);
    }

    @Override
    public String toString() {
        return parent.toString();
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        parent.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        parent.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        parent.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        parent.clearAssertionStatus();
    }
}