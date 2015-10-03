package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.utils.CustomClassLoader;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.User;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Search view controller.
 *
 * Created by Vuzi on 03/10/2015.
 */
public class SearchView extends ViewController {

    // XML values
    public VBox tweetListView;            // List of views
    public ScrollPane tweetListContainer; // Container for the tweet list view
    public TextField searchValue;         // Filtering tweet values
    public RadioButton userSearch;
    public RadioButton tweetSearch;
    public CheckBox ignoreRetweets;

    public static final String ID = "PROFILE";

    // Running values
    private SearchViewType type;              // Current type of view displayed

    private ObservableList<Status> tweetList;  // List of tweets
    private Query tweetQuery;                  // Tweet tweetQuery
    private QueryResult tweetLastResults;      // Tweet last results
    private ObservableList<User> userList;     // List of users
    private String userQuery;                  // User search
    private int userPage;                      // User page

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
    }

    @Override
    protected void onShow(Map<String, Object> params) {
        String filter = (String) params.get("filter");
        Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("Search view with filter {0}", filter));

        tweetSearch.setSelected(true);
        searchValue.setText(filter);

        searchAction();
    }

    @Override
    protected void onHide() {
        Logger.getLogger(this.getClass().getName()).info("Hiding profile search view");
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
     * Update the timeline information, according to the actual type of list of the controller.
     */
    private void updateResults(boolean nextPage) {

        // Empty all the lists & hide buttons
        if(!nextPage) {
            tweetList.clear();
            userList.clear();

            tweetListContainer.setVisible(false);

            if (searchValue.getText() == null || searchValue.getText().isEmpty())
                return;
        }

        // Update result accordingly
        switch (type) {
            case TWEET:
                showTweetSearch();
                break;
            case USER:
                showUserSearch();
                break;
        }
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showTweetSearch() {
        Utils.asyncTask(() -> {
            tweetLastResults = TwitterClient.client().search(tweetQuery);
            return tweetLastResults.getTweets();
        }, (statuses) -> {
            if (statuses != null) {
                tweetList.addAll(statuses);

                if (!tweetListContainer.isVisible())
                    tweetListContainer.setVisible(true);
            }
        });
    }

    /**
     * Show all the tweets of the current user.
     */
    private void showUserSearch() {
        Utils.asyncTask(() -> TwitterClient.client().searchUsers(userQuery, userPage), (users) -> {
            if (users != null)
                userList.addAll(users);

            if (!tweetListContainer.isVisible())
                tweetListContainer.setVisible(true);
        });
    }

    /**
     * Action when the "load more results" button is selected.
     */
    public void nextPageAction() {
        switch (type) {
            case TWEET:
                if(tweetLastResults != null) {
                    tweetQuery = tweetLastResults.nextQuery();
                    updateResults(true);
                }
                break;
            case USER:
                userPage++;
                updateResults(true);
                break;
        }
    }

    /**
     * Action when the search button is clicked.
     */
    public void searchAction() {
        // Update search type
        if(tweetSearch.isSelected()) {
            type = SearchViewType.TWEET;

            // Prepare the tweetQuery
            if(ignoreRetweets.isSelected())
                tweetQuery = new Query(searchValue.getText() + " +exclude:retweets");
            else
                tweetQuery = new Query(searchValue.getText());
            tweetQuery.setCount(50);
            tweetQuery.setResultType(Query.ResultType.mixed);

            // No last results
            tweetLastResults = null;
        }else {
            type = SearchViewType.USER;
            userQuery = searchValue.getText();
            userPage = 1;
        }


        updateResults(false);
    }
}

/**
 * Type of content searched.
 */
enum SearchViewType {
    /**
     * User search.
     */
    USER,

    /**
     * Tweet search.
     */
    TWEET
}