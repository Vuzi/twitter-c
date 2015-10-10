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
import java.util.logging.Logger;

/**
 * TwitterClient main controller and entry point.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class Main extends AppController {

    /**
     * Entry point.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        Logger.getLogger(Main.class.getName()).info("TwitterClient started");

        Platform.setImplicitExit(false);

        // Load and try to load the access token
        try {
            TwitterClient.initialize().authenticate();
        } catch (TwitterClientException e) {
            e.printStackTrace();
        }

        // Show the views
        Main.launch(Main.class, args);
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

        TwitterClient.get().setOnConnected(twitterClient -> {
            if(trayIcon != null)
                startStream();
        });

        if(TwitterClient.get().getCurrentUser() == null)
            createWindow("Connexion à Twitter", "PinView.fxml");
        else
            Utils.showProfilePage(this, TwitterClient.get().getCurrentUser());
    }


    @Override
    protected void onEnd() {
        // Destroy the stream when the application is stopped
        if(twitterStream != null) {
            Logger.getLogger(this.getClass().getName()).info("Shutdown of the user stream from Twitter...");
            twitterStream.shutdown();
        }
    }

    private void startStream() {
        Logger.getLogger(this.getClass().getName()).info("Creation of the user stream from Twitter...");

        try {
            twitterStream = new TwitterStreamFactory(TwitterClient.client().getConfiguration()).getInstance();
            twitterStream.setOAuthAccessToken(TwitterClient.client().getOAuthAccessToken());

            UserStreamListener listener = new UserStreamAdapter() {
                @Override
                public void onStatus(Status status) {
                    trayIcon.displayMessage("Nouveau tweet @" + status.getUser().getName(), status.getText(), TrayIcon.MessageType.NONE);
                }

                @Override
                public void onFavorite(User source, User target, Status favoritedStatus) {
                    trayIcon.displayMessage("Nouveau favoris !", source.getScreenName() + " a mis en favoris un de vos tweets", TrayIcon.MessageType.NONE);
                }
            };
            twitterStream.addListener(listener);
            twitterStream.user();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

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
                BufferedImage trayIconImage = ImageIO.read(Main.class.getResource("/fr/esgi/twitterc/view/image/twitter.png"));
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
