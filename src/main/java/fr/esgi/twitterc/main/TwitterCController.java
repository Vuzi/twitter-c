package fr.esgi.twitterc.main;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.client.TwitterClientException;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.AppController;
import fr.esgi.twitterc.view.controller.WindowController;
import javafx.application.Platform;
import twitter4j.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * TwitterClient main controller and entry point.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class TwitterCController extends AppController {

    /**
     * Entry point.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        Logger.getLogger(TwitterCController.class.getName()).info("TwitterClient started");

        // Load the properties
        Properties p = new Properties();
        InputStream inputStream =  TwitterCController.class.getClassLoader().getResourceAsStream("conf.properties");

        try {
            p.load(inputStream);

            if(!p.containsKey("twitter.api.key") || !p.containsKey("twitter.api.secret")) {
                Logger.getLogger(TwitterCController.class.getName()).info("Required properties not set !");
                System.exit(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(TwitterCController.class.getName()).info("Could not load the properties : " + e.getMessage());
            System.exit(1);
        }

        // Avoid implicit exit when all windows are closed
        Platform.setImplicitExit(false);

        // Load and try to load the access token
        try {
            TwitterClient.initialize(p.getProperty("twitter.api.key"), p.getProperty("twitter.api.secret")).authenticate();
        } catch (TwitterClientException e) {
            e.printStackTrace();
        }

        // Show the views
        TwitterCController.launch(TwitterCController.class, args);
    }

    // Controller values
    private TwitterStream twitterStream;   // Stream
    private TrayIcon trayIcon;             // Tray Icon

    @Override
    protected javafx.scene.image.Image getAppIcon() {
        return new javafx.scene.image.Image("/fr/esgi/twitterc/view/image/twitter.png");
    }

    @Override
    protected void onCreation() {
        // Create the tray image & launch the stream
        createTray();

        if(trayIcon != null) {
            if(TwitterClient.get().getCurrentUser() != null)
                startStream();
            else
                TwitterClient.get().setOnConnected(twitterClient -> startStream());
        }

        if(TwitterClient.get().getCurrentUser() == null)
            createWindow("Connexion à Twitter", "PinView.fxml");
        else
            Utils.showProfilePage(this, TwitterClient.get().getCurrentUser());
    }

    @Override
    protected void onWindowDeletion() {
        if(getWindows().isEmpty()) {
            Logger.getLogger(this.getClass().getName()).info("No more window displayed");
            showNotification("Information", "L'application est maintenant réduite dans la barre des tâches et " +
                    "continuera de reçevoir vos tweets");
        }
    }

    @Override
    protected void onEnd() {
        // Destroy the stream when the application is stopped
        if(twitterStream != null) {
            Logger.getLogger(this.getClass().getName()).info("Shutdown of the user stream from Twitter...");
            twitterStream.shutdown();
        }
    }

    public void showNotification(String title, String message) {
        if(trayIcon != null)
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.NONE);
    }

    /**
     * Start the twitter stream for the main application thread.
     */
    private void startStream() {
        Logger.getLogger(this.getClass().getName()).info("Creation of the user stream from Twitter...");

        try {
            twitterStream = new TwitterStreamFactory(TwitterClient.client().getConfiguration()).getInstance();
            twitterStream.setOAuthAccessToken(TwitterClient.client().getOAuthAccessToken());

            UserStreamListener listener = new UserStreamAdapter() {
                @Override
                public void onStatus(Status status) {
                    trayIcon.displayMessage("Nouveau tweet @" + status.getUser().getName(), status.getText(), TrayIcon.MessageType.NONE);
                    showNotification("Nouveau tweet @" + status.getUser().getName(), status.getText());
                }

                @Override
                public void onFavorite(User source, User target, Status favoritedStatus) {
                    showNotification("Nouveau favoris !", source.getScreenName() + " a mis en favoris un de vos tweets");
                }
            };
            twitterStream.addListener(listener);
            twitterStream.user();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the system icon tray, allowing to display notifications for the user.
     */
    private void createTray() {
        if(SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();

                // create a popup menu
                PopupMenu popup = new PopupMenu();

                java.awt.MenuItem showItem = new java.awt.MenuItem("Timeline");
                java.awt.MenuItem quitItem = new java.awt.MenuItem("Quitter");
                popup.add(showItem);
                popup.add(quitItem);

                // Tray image
                BufferedImage trayIconImage = ImageIO.read(TwitterCController.class.getResource("/fr/esgi/twitterc/view/image/twitter.png"));
                int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
                this.trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "TwitterC", popup);

                // add the tray image
                tray.add(trayIcon);

                // Add listeners
                showItem.addActionListener(e -> Platform.runLater(() -> {
                    if(TwitterClient.get().getCurrentUser() != null)
                        Utils.showProfilePage(this, TwitterClient.get().getCurrentUser());
                    else
                        createWindow("Connexion à Twitter", "PinView.fxml");
                }));
                quitItem.addActionListener(e -> {
                    tray.remove(trayIcon);
                    Platform.runLater(() -> {
                        this.getWindows().forEach(WindowController::close);
                        Platform.exit();
                    });
                });

                trayIcon.displayMessage("TwitterC", "Lancement de l'application !", TrayIcon.MessageType.INFO);
            } catch (AWTException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
