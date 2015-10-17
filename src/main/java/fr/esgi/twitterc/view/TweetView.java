package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * New tweet view. Show an empty tweet with an editable content.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class TweetView extends ViewController {

    // XML values
    public VBox tweet;
    public VBox replies;

    public static final String ID = "TWEET-VIEW";
    private Status status;

    private ObservableList<Status> tweetList;   // List of tweets
    private ObservableList<Status> repliesList;  // List of replies

    @Override
    protected void onCreation() {

        // Prepare the tweet list
        tweetList = FXCollections.observableArrayList();

        // Map our list to the Vbox children
        Utils.mapByValue(tweetList, tweet.getChildren(), status -> {
            try {
                // Load XML with custom loader
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/TweetListView.fxml"));
                //fxmlLoader.setClassLoader(this.cachingClassLoader);
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
        repliesList = FXCollections.observableArrayList();

        // Map our list to the Vbox children
        Utils.mapByValue(repliesList, replies.getChildren(), status -> {
            try {
                // Load XML with custom loader
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/TweetListView.fxml"));
                //fxmlLoader.setClassLoader(this.cachingClassLoader);
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
        updateInfo((Status) params.get("tweet"));
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
     * @param status The user.
     */
    private void updateInfo(Status status) {
        Logger.getLogger(this.getClass().getName()).info("New tweet view for tweet");

        // Save status
        this.status = status;

        // Set the first status
        tweetList.setAll(status);

        // Set responses
        Utils.asyncTask(() -> getResponses(20), repliesList::setAll);
    }

    /**
     * Try to return the number of specified response to the actual tweet.
     *
     * @param size Maximum number of answer to get.
     * @return Status answering the view's status.
     */
    private List<Status> getResponses(int size) {
        ArrayList<Status> replies = new ArrayList<>();

        try {
            long id = status.getId();
            String screenName = status.getUser().getScreenName();

            Query query = new Query("@" + screenName + " since_id:" + id);
            query.setCount(200);

            do {
                QueryResult response = TwitterClient.client().search(query);

                replies.addAll(response.getTweets().stream()
                        .filter(status -> status.getInReplyToStatusId() == id)
                        .collect(Collectors.toList()));

                query = response.nextQuery();
            } while (query != null && replies.size() < size);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return replies;
    }
}
