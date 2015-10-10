package fr.esgi.twitterc.client;

import fr.esgi.twitterc.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.logging.Logger;

/**
 * Wrapper around the twitter4j Twitter class, to handle errors, credentials save/load and credentials verifications.
 * This static class must be initialized with the provided value.
 */
public class TwitterClient {

    // Static values
    private static boolean IS_INITIALIZED;
    private static TwitterClient TWITTER_CLIENT;
    private static String SERIALIZED_TOKEN_FILENAME = "accessToken.bin";

    /**
     * Return the twitter client instance. If the twitter client have not been initialized yet,
     * it will automatically be.
     *
     * @return The twitter client instance.
     */
    public static TwitterClient get() {
        return TwitterClient.TWITTER_CLIENT;
    }

    /**
     * Return the wrapped twitter4j client instance. If the twitter client have not been initialized yet,
     * it will automatically be.
     *
     * @return The twitter4j client instance.
     */
    public static Twitter client() {
        return get().twitter;
    }

    /**
     * Initialize the static class. If the instance is already initialized, null will be returned.
     *
     * @return The twitter client instance.
     */
    public static TwitterClient initialize(String apiKey, String apiSecret) {
        if(TwitterClient.IS_INITIALIZED)
            return TwitterClient.TWITTER_CLIENT;

        TwitterFactory tf = new TwitterFactory(
                new ConfigurationBuilder()
                        .setOAuthConsumerKey(apiKey)
                        .setOAuthConsumerSecret(apiSecret)
                        .setOAuthAccessToken(null)
                        .setOAuthAccessTokenSecret(null).build());

        return TwitterClient.TWITTER_CLIENT = new TwitterClient(tf.getInstance());
    }

    // Instance values
    private Twitter twitter;
    private User user;
    private RequestToken requestToken;
    private Utils.Consumer<TwitterClient> onConnected;

    /**
     * Private constructor for the twitter client.
     *
     * @param twitter The wrapped twitter4j client.
     */
    private TwitterClient(Twitter twitter) {
        TwitterClient.IS_INITIALIZED = true;
        TwitterClient.TWITTER_CLIENT = this;

        this.twitter = twitter;
    }

    /**
     * Return the request token needed to connect to Twitter.
     *
     * @return The request token.
     * @throws TwitterClientException
     */
    public RequestToken requestToken() throws TwitterClientException {
        if(requestToken != null)
            return requestToken;

        try {
            return this.requestToken = twitter.getOAuthRequestToken();
        } catch (TwitterException e) {
            Logger.getLogger(this.getClass().getName()).info("Error while getting an OAuth request token : " + e.getMessage());
            throw new TwitterClientException("Error while getting an OAuth request token", e);
        }
    }

    /**
     * Set a listener called when the application is connected.
     *
     * @param onConnected Called when connected.
     */
    public void setOnConnected(Utils.Consumer<TwitterClient> onConnected) {
        this.onConnected = onConnected;
    }

    /**
     * Try to authenticate using the saved access token.
     *
     * @throws TwitterClientException Thrown if no token is saved, or the loaded token does not work.
     */
    public void authenticate() throws TwitterClientException {
        AccessToken accessToken = loadAccessToken();

        if(accessToken != null) {
            twitter.setOAuthAccessToken(accessToken);

            // Test the token
            try {
                user = twitter.verifyCredentials();

                if(onConnected != null)
                    onConnected.apply(this);
            } catch (TwitterException e) {
                throw new TwitterClientException("Error while using the saved access token", e);
            }
        } else
            throw new TwitterClientException("No saved access token");
    }

    /**
     * Authenticate using the provided request token and associated pin. The pin should be manually given by the
     * user after authorizing the application to use his twitter account.
     *
     * @param token The token.
     * @param pinCode The pin code.
     * @throws TwitterClientException Thrown if the pin is invalid.
     */
    public void authenticate(RequestToken token, String pinCode) throws TwitterClientException {
        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(token, pinCode);
            user = twitter.verifyCredentials();
            saveAccessToken(accessToken);
        } catch (TwitterException e) {
            Logger.getLogger(this.getClass().getName()).info("Error while using the provided pin : " + e.getMessage());
            throw new TwitterClientException("Error while using the provided pin", e);
        }
    }

    /**
     * Save the provided token to the filesystem. If any error occurs, no save will be made.
     *
     * @param token The token to save.
     */
    private void saveAccessToken(AccessToken token) {
        try {
            new ObjectOutputStream(new FileOutputStream(SERIALIZED_TOKEN_FILENAME)).writeObject(token);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).info("Error while saving access token : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Try to load a token from the filesystem. If no token could be load, null is returned.
     *
     * @return The access token.
     */
    private AccessToken loadAccessToken() {
        try {
            if(new File(SERIALIZED_TOKEN_FILENAME).exists()) {
                return (AccessToken) new ObjectInputStream(new FileInputStream(SERIALIZED_TOKEN_FILENAME)).readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).info("Error while loading access token : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Return the current user. Note that if this value is null if not authentication have been successfully performed.
     * @return The user.
     */
    public User getCurrentUser() {
        return user;
    }
}

