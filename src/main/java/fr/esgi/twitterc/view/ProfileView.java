package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.main.TwitterCController;
import fr.esgi.twitterc.utils.CustomClassLoader;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import twitter4j.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
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
    public Pane profileMainImage;   // Profile image
    public Label userName;          // User long name
    public Label userTag;           // User tag name
    public Label followingTitle;    // Following panel title
    public Label followersTitle;    // Followers panel title
    public Label favoritesTitle;    // Favorites panel title
    public Label tweetsTitle;       // Tweets panel title
    public Label timelineTitle;     // Timeline panel title
    public VBox timelineButton;     // List of elements (tweets or users)
    public Label following;         // Number of people following
    public Label followers;         // Number of people followed
    public Label favorites;         // Number of favorites tweets
    public Label tweets;            // Number of total tweets
    public VBox tweetListView;      // List of views
    public TextField searchValue;   // Filtering tweet values
    public VBox tweetListContainer; // Container for the tweet list view
    public Button waitingTweets;    // Button when multiple tweets have been received but not displayed
    public Button followButton;     // Follow button

    public static final String ID = "PROFILE";

    // Running values
    private ProfileViewType type;              // Current type of view displayed
    private User relatedUser;                  // User of the profile

    private TwitterStream twitterStream;       // Stream

    private ObservableList<Status> tweetList;  // List of tweets
    private ObservableList<User> userList;     // List of users
    private ArrayList<Status> waitingTweetList;// List of tweets waiting
    private Paging paging;                     // Paging

    // Performance test
    private ClassLoader cachingClassLoader = new CustomClassLoader(FXMLLoader.getDefaultClassLoader());


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

        waitingTweetList = new ArrayList<>();

        // Prepare the stream
        try {
            twitterStream = new TwitterStreamFactory(TwitterClient.client().getConfiguration()).getInstance();
            twitterStream.setOAuthAccessToken(TwitterClient.client().getOAuthAccessToken());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onShow(Map<String, Object> params) {
        relatedUser = (User) params.get("user");

        if(relatedUser == null) // Fallthrough case
            relatedUser = TwitterClient.get().getCurrentUser();

        updateInfo();
    }

    @Override
    protected void onHide() {
        Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("Hiding profile view for user : {0}, {1}", relatedUser.getScreenName(), relatedUser.getName()));

        twitterStream.clearListeners();

        Utils.asyncTask(() -> {
            twitterStream.shutdown();
            return 0;
        }, null);
    }

    @Override
    protected void onDeletion() {
        onHide();
    }

    @Override
    protected String getID() {
        return ID;
    }

    /**
     * Update the user information using the provided user.
     */
    private void updateInfo() {
        Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("Profile view for user : {0}, {1}", relatedUser.getScreenName(), relatedUser.getName()));

        type = ProfileViewType.FOLLOWERS; // Set default value

        // Set user real name
        userName.setText(relatedUser.getName());

        // Set user TAG
        userTag.setText("@" + relatedUser.getScreenName());

        // Set the banner image (async)
        if(relatedUser.getProfileBannerURL() != null){
            Utils.asyncTask(() -> new Image(relatedUser.getProfileBannerURL().replace("web", "1500x500")), image -> {
                BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
                profilePanel.setBackground(new Background(backgroundImage));
            });
        }

        // Set profile image (async)
        if(relatedUser.getProfileImageURL() != null) {
            Utils.asyncTask(() -> new Image(relatedUser.getProfileImageURL().replace("_normal","")), image -> {
                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileMainImage.setBackground(new Background(backgroundImage));
            });
        }

        // Set following count
        following.setText(String.valueOf(relatedUser.getFriendsCount()));

        // Set followers count
        followers.setText(String.valueOf(relatedUser.getFollowersCount()));

        // Set tweets count
        tweets.setText(String.valueOf(relatedUser.getStatusesCount()));

        // Set favorites count
        favorites.setText(String.valueOf(relatedUser.getFavouritesCount()));

        // Change the view if this is the authenticated user view
        if(relatedUser.getId() == TwitterClient.get().getCurrentUser().getId()) {
            timelineButton.setVisible(true);
            timelineButton.setManaged(true);

            followButton.setVisible(false);
            followButton.setManaged(false);

            // Monitor the stream
            twitterStream.clearListeners();
            waitingTweetList.clear();
            updateWaitingTimeline();

            UserStreamListener listener = new UserStreamAdapter() {
                @Override
                public void onStatus(Status status) {
                    waitingTweetList.add(status);

                    Platform.runLater(ProfileView.this::updateWaitingTimeline);
                }
            };
            twitterStream.addListener(listener);
            twitterStream.user();

            // Update timeline (async)
            showTimelineAction();
        } else {
            timelineButton.setVisible(false);
            timelineButton.setManaged(false);

            followButton.setVisible(true);
            followButton.setManaged(true);

            Utils.asyncTask(() -> TwitterClient.client().showFriendship(TwitterClient.get().getCurrentUser().getId(), relatedUser.getId()),
                    relationship -> {
                        if (relationship.isSourceFollowingTarget())
                            followButton.setText("Ne plus suivre");
                    });

            // Update timeline (async)
            showTweetAction();
        }
    }

    /**
     * Filter the timeline to perform a search.
     *
     * @param filter The string filter.
     */
    private void filterTimeline(String filter) {
        if(type == ProfileViewType.TWEETS) {
            Utils.asyncTask(() -> {
                        Query query = new Query("from:" + relatedUser.getScreenName() + " " + filter);
                        return TwitterClient.client().search(query).getTweets();
                    }, tweetList::setAll);
        }
    }

    /**
     * Select a title label in the menu.
     *
     * @param label The label to select.
     */
    private void selectMenuLabel(Label label) {
        timelineTitle.getStyleClass().remove("selectedTab");
        followingTitle.getStyleClass().remove("selectedTab");
        followersTitle.getStyleClass().remove("selectedTab");
        favoritesTitle.getStyleClass().remove("selectedTab");
        tweetsTitle.getStyleClass().remove("selectedTab");

        label.getStyleClass().add("selectedTab");
    }

    private void updateWaitingTimeline() {
        if(waitingTweetList.isEmpty()) {
            waitingTweets.setVisible(false);
            waitingTweets.setManaged(false);
        } else {
            if(waitingTweetList.size() > 1)
                waitingTweets.setText(waitingTweetList.size() + " nouveaux tweets reçu");
            else
                waitingTweets.setText(waitingTweetList.size() + " nouveau tweet reçu");
            waitingTweets.setVisible(true);
            waitingTweets.setManaged(true);
        }
    }

    /**
     * Update the timeline information, according to the actual type of list of the controller.
     */
    private void updateTimeline() {

        // Empty all the lists & hide buttons
        tweetList.clear();
        userList.clear();

        tweetListContainer.setVisible(false);

        // Clear loaded data
        waitingTweetList.clear();
        updateWaitingTimeline();

        // Update result accordingly
        switch (type) {
            case TIMELINE:
                showTimeline();
                break;
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
        Utils.asyncTask(() -> TwitterClient.client().getFriendsList(relatedUser.getId(), -1), users -> {
            if(users != null)
                userList.setAll(users);
            tweetListContainer.setVisible(true);
        });
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllSubscribers() {
        Utils.asyncTask(() -> TwitterClient.client().getFollowersList(relatedUser.getId(), -1), users ->  {
            if(users != null)
                userList.setAll(users);
            tweetListContainer.setVisible(true);
        });
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showAllTweets() {
        Utils.asyncTask(() -> TwitterClient.client().getUserTimeline(relatedUser.getId(), paging), statuses -> {
            if(statuses != null)
                tweetList.setAll(statuses);
            tweetListContainer.setVisible(true);
        });
    }

    /**
     * Show all the favorites tweets of the current user.
     */
    private void showAllFavorites() {
        Utils.asyncTask(() -> TwitterClient.client().getFavorites(relatedUser.getId(), paging), statuses -> {
            if(statuses != null)
                tweetList.setAll(statuses);
            tweetListContainer.setVisible(true);
        });
    }

    /**
     * Show the user timeline.
     */
    private void showTimeline() {
        Utils.asyncTask(() -> TwitterClient.client().getHomeTimeline(paging), statuses -> {
            if(statuses != null)
                tweetList.setAll(statuses);
            tweetListContainer.setVisible(true);
        });
    }

    /**
     * Action when the "new tweet" button is clicked.
     */
    public void openNewTweetAction() {
        if(relatedUser.getId() != TwitterClient.get().getCurrentUser().getId())
            Utils.showNewTweetPage(getAppController(), relatedUser);
        else
            Utils.showNewTweetPage(getAppController());
    }

    public void showFollowingAction() {
        if(type == ProfileViewType.FOLLOWED)
            return;

        // Update button style
        selectMenuLabel(followingTitle);

        // Change type & reset paging
        type = ProfileViewType.FOLLOWED;
        paging = new Paging(1, 30);

        // Update
        updateTimeline();
    }

    public void showFollowersAction() {
        if(type == ProfileViewType.FOLLOWERS)
            return;

        // Update button style
        selectMenuLabel(followersTitle);

        // Change type & reset paging
        type = ProfileViewType.FOLLOWERS;
        paging = new Paging(1, 30);

        updateTimeline();
    }

    public void showFavoritesAction() {
        if(type == ProfileViewType.FAVORITES)
            return;

        // Update button style
        selectMenuLabel(favoritesTitle);

        // Change type & reset paging
        type = ProfileViewType.FAVORITES;
        paging = new Paging(1, 30);

        updateTimeline();
    }

    public void showTweetAction() {
        if(type == ProfileViewType.TWEETS)
            return;

        // Update button style
        selectMenuLabel(tweetsTitle);

        // Change type & reset paging
        type = ProfileViewType.TWEETS;
        paging = new Paging(1, 30);

        updateTimeline();
    }

    public void showTimelineAction() {
        if(type == ProfileViewType.TIMELINE)
            return;

        // Update button style
        selectMenuLabel(timelineTitle);

        // Change type & reset paging
        type = ProfileViewType.TIMELINE;
        paging = new Paging(1, 30);

        updateTimeline();
    }

    public void filterTweetAction() {
        filterTimeline(searchValue.getText());
    }

    public void nextPageAction() {
        int newPage = paging.getPage() + 1;
        paging = new Paging(newPage, 30);
        updateTimeline();
    }

    public void previousPageAction() {
        if(paging.getPage() > 1) {
            int newPage = paging.getPage() - 1;
            paging = new Paging(newPage, 30);
            updateTimeline();
        }
    }

    public void addWaitingTweetsAction() {
        tweetList.addAll(0, waitingTweetList);
        waitingTweetList.clear();

        updateWaitingTimeline();
    }

    public void searchAction() {
        getAppController().createWindow("Recherche", "SearchView.fxml");
    }

    public void openFollowAction() {
        Utils.asyncTask(() -> TwitterClient.client().showFriendship(TwitterClient.get().getCurrentUser().getId(), relatedUser.getId()),
                relationship -> {
                    try {
                        if (relationship.isSourceFollowingTarget()) {
                            TwitterClient.client().destroyFriendship(relatedUser.getId());
                            ((TwitterCController) getAppController()).showNotification("Information", "Vous ne suivez plus " + relatedUser.getScreenName());
                            followButton.setText("Suivre");
                        } else {
                            TwitterClient.client().createFriendship(relatedUser.getId());
                            ((TwitterCController) getAppController()).showNotification("Information", "Vous suivez désormais " + relatedUser.getScreenName());
                            followButton.setText("Ne plus suivre");
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
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
     * Timeline of the current user.
     */
    TIMELINE,

    /**
     * Will show all the subscriptions the user had made.
     */
    FOLLOWED
}
