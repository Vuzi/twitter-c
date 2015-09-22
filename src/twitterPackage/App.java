package twitterPackage;

import javafx.scene.control.Alert;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static ConfigurationBuilder builder = new ConfigurationBuilder();
    public static Twitter TWITTER;
    private static RequestToken requestToken;
    //public static Twitter TWITTER = TwitterFactory.getSingleton();

    public static void loadTwitter (){

        // Swing UI initialization
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Twitter connection and OAuth
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("lH6JSO5KsVrLDb0bpRjwRKz6J")
                    .setOAuthConsumerSecret("eO1DeOaZdKpXJc5kGEhbD9aWM2zqmOsPnLoMXQU4MOO6cc5FvW")
                    .setOAuthAccessToken(null)
                    .setOAuthAccessTokenSecret(null);
            TwitterFactory tf = new TwitterFactory(cb.build());
            TWITTER = tf.getInstance();
            //TWITTER.setOAuthConsumer("lH6JSO5KsVrLDb0bpRjwRKz6J", "eO1DeOaZdKpXJc5kGEhbD9aWM2zqmOsPnLoMXQU4MOO6cc5FvW");
            requestToken = TWITTER.getOAuthRequestToken();

            openWebpage(new URI(requestToken.getAuthorizationURL()));

        } catch (TwitterException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the provided URI into the default web browser, of possible.
     * @param uri The URI page to open.
     */
    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public  static boolean testPin(String value)
    {
        try {
            TWITTER.getOAuthAccessToken(requestToken, value);
        } catch (TwitterException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return false;
        }
        return true;
    }

    public static boolean updateStatus(String value){

        try {
            TWITTER.updateStatus(value);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Envoi réussi");
            alert.setHeaderText(null);
            alert.setContentText("Tweet sent");

            alert.show();

        } catch (TwitterException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());

            alert.showAndWait();

            return false;

        }
        return true;
    }

    public static String getUserProfilImage(){
        User user = null;
        try {
            user = TWITTER.showUser(TWITTER.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        String url = user.getProfileImageURL();

        return  url;
    }
}
