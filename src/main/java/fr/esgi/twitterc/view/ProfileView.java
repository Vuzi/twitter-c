package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
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

    // Performance test
    public ClassLoader cachingClassLoader = new MyClassLoader(FXMLLoader.getDefaultClassLoader());


    @Override
    protected void onCreation() {
        // Prepare the tweet list
        tweetList = FXCollections.observableArrayList();

        // Map our list to the Vbox children
        mapByValue(tweetList, tweetListView.getChildren(), status -> {
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
                //showAllSubscribed();
                break;
            case FOLLOWERS:
                //showAllSubscribers();
                break;
        }
    }

    /**
     * Show all the tweets of the current user.
     */
    /*
    private void showAllSubscribed() {
        userListView.setVisible(false);

        Task<ResponseList<User>> tweetLoader = new Task<ResponseList<User>>() {
            @Override
            protected ResponseList<User> call() throws Exception {
                return TwitterClient.client().getFriendsList(user.getId(), -1);
            }
        };

        tweetLoader.setOnSucceeded(event -> {
            userList.setAll(tweetLoader.getValue());
            userListView.scrollTo(0);
            userListView.setVisible(true);
        });

        tweetLoader.run();
    }*/

    /**
     * Show all the tweets of the current user.
     *//*
    private void showAllSubscribers() {
        userListView.setVisible(false);

        Task<ResponseList<User>> tweetLoader = new Task<ResponseList<User>>() {
            @Override
            protected ResponseList<User> call() throws Exception {
                return TwitterClient.client().getFollowersList(user.getId(), -1);
            }
        };

        tweetLoader.setOnSucceeded(event -> {
            userList.setAll(tweetLoader.getValue());
            userListView.scrollTo(0);
            userListView.setVisible(true);
        });

        tweetLoader.run();
    }*/

    /**
     * Show all the tweets of the current user.
     */
    private void showAllTweets() {
        Task<ResponseList<Status>> tweetLoader = new Task<ResponseList<Status>>() {
            @Override
            protected ResponseList<Status> call() throws Exception {
                return TwitterClient.client().getUserTimeline(user.getId());
            }
        };

        tweetLoader.setOnSucceeded(event -> tweetList.setAll(tweetLoader.getValue()));

        tweetLoader.run();
    }

    /**
     * Show all the favorites tweets of the current user.
     */
    private void showAllFavorites() {
        Task<ResponseList<Status>> tweetLoader = new Task<ResponseList<Status>>() {
            @Override
            protected ResponseList<Status> call() throws Exception {
                return TwitterClient.client().getFavorites(user.getId());
            }
        };

        tweetLoader.setOnSucceeded(event -> tweetList.setAll(tweetLoader.getValue()));

        tweetLoader.run();
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

        /*
        following.getStyleClass().add("selectedTab");
        followers.getStyleClass().remove("selectedTab");
        favorites.getStyleClass().remove("selectedTab");
        tweets.getStyleClass().remove("selectedTab");

        if(type.contentType != ProfileViewTypeContent.USER)
            listContainer.setCenter(userListView);

        type = ProfileViewType.FOLLOWED;
        updateTimeline();*/
    }

    public void showFollowersAction() {
        if(type == ProfileViewType.FOLLOWERS)
            return;

        /*
        followers.getStyleClass().add("selectedTab");
        following.getStyleClass().remove("selectedTab");
        favorites.getStyleClass().remove("selectedTab");
        tweets.getStyleClass().remove("selectedTab");

        if(type.contentType != ProfileViewTypeContent.USER)
            listContainer.setCenter(userListView);

        type = ProfileViewType.FOLLOWERS;
        updateTimeline();*/
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

    public static <S, T> void mapByValue(ObservableList<S> sourceList, ObservableList<T> targetList, Function<S, T> mapper) {
        Objects.requireNonNull(sourceList);
        Objects.requireNonNull(targetList);
        Objects.requireNonNull(mapper);

        Map<S, T> sourceToTargetMap = new HashMap<>();

        targetList.clear();

        // Populate targetList by sourceList and mapper
        for (S s : sourceList) {
            T t = mapper.apply(s);
            targetList.add(t);
            sourceToTargetMap.put(s, t);
        }
        // Listen to changes in sourceList and update targetList accordingly
        ListChangeListener<S> sourceListener = c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        int j = c.getPermutation(i);
                        S s = sourceList.get(j);
                        T t = sourceToTargetMap.get(s);
                        targetList.set(i, t);
                    }
                } else {
                    for (S s : c.getRemoved()) {
                        T t = sourceToTargetMap.get(s);
                        targetList.remove(t);
                        sourceToTargetMap.remove(s);
                    }

                    int i = c.getFrom();

                    for (S s : c.getAddedSubList()) {
                        T t = mapper.apply(s);
                        targetList.add(i, t);
                        sourceToTargetMap.put(s, t);
                        i += 1;
                    }
                }
            }
        };
        sourceList.addListener(new WeakListChangeListener<>(sourceListener));
        // Store the listener in targetList to prevent GC
        // The listener should be active as long as targetList exists
        targetList.addListener((InvalidationListener) iv -> {
            Object[] refs = {sourceListener,};
            Objects.requireNonNull(refs);
        });
    }
}

/**
 * Enumeration of all the possible type of value that can take the main list of the profile view.
 */
enum ProfileViewType {
    /**
     * Will show all the tweets of the user.
     */
    TWEETS(ProfileViewTypeContent.TWEET),

    /**
     * Will show all the favorites tweets of the user.
     */
    FAVORITES(ProfileViewTypeContent.TWEET),

    /**
     * Will show all the subscribers of the user.
     */
    FOLLOWERS(ProfileViewTypeContent.USER),

    /**
     * Will show all the subscriptions the user had made.
     */
    FOLLOWED(ProfileViewTypeContent.USER);

    public final ProfileViewTypeContent contentType;

    ProfileViewType(ProfileViewTypeContent contentType) {
        this.contentType = contentType;
    }
}


/**
 * Enumeration of all the possible type of value that can take the main list of the profile view.
 */
enum ProfileViewTypeContent {
    TWEET,
    USER
}

/**
 * Timeline list cell view. This cell will display all the information of the status given. This actually
 * only use the cached view from the profile view itself, only being a proxy between the list view and
 * our map of cached values.
 */
class UserListCell extends ListCell<User> {

    // View
    private final ProfileView parentController;
    private final UserListView controller;

    /**
     * Timeline Tweet constructor.
     */
    public UserListCell(ProfileView parentController) {
        this.parentController = parentController;

        // Create the controller
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/UserListView.fxml"));

            // Test custom loaded
            fxmlLoader.setClassLoader(parentController.cachingClassLoader);

            fxmlLoader.load();
            this.controller = fxmlLoader.getController();
            this.controller.setController(parentController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        // No text content
        setText(null);

        // Graphical components
        if(!empty) {
            controller.update(user);
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
     //   if(!empty) {
            // Creating our customs view is too heavy for FX, so every created view is cached
            // and re-used when needed. The TweetListCell class is actually only used to given the
            // corresponding graphic according to the provided status
       //     TweetListView controller;// = parentController.getCachedTweetCells().get(status);
/*
            if(controller == null) {
                // Create the controller
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/TweetListView.fxml"));

                    // Test custom loaded
                    fxmlLoader.setClassLoader(parentController.cachingClassLoader);

                    fxmlLoader.load();
                    controller = fxmlLoader.getController();
                    controller.setController(parentController);
                    controller.update(status);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Cache it
                parentController.getCachedTweetCells().put(status, controller);*/
       //     }

            //setGraphic(controller.getView());
       // } else
         //   setGraphic(null);
    }

}

class MyClassLoader extends ClassLoader{
    private final Map<String, Class> classes = new HashMap<String, Class>();
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
// System.out.print("try to load " + className);
        if (classes.containsKey(className)) {
            Class<?> result = classes.get(className);
            return result;
        } else {
            try {
                Class<?> result = parent.loadClass(className);
// System.out.println(" -> success!");
                classes.put(className, result);
                return result;
            } catch (ClassNotFoundException ignore) {
// System.out.println();
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