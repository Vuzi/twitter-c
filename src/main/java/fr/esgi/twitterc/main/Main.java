package fr.esgi.twitterc.main;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.client.TwitterClientException;
import fr.esgi.twitterc.view.controller.AppController;

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

        // Load and try to load the access token
        try {
            TwitterClient.initialize().authenticate();
        } catch (TwitterClientException e) {
            e.printStackTrace();
        }

        // Create the tray icon
        createTray();

        // Show the views
        Main.launch(Main.class, args);
    }

    private static void createTray() {
        if(SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();

                // create a popup menu
                PopupMenu popup = new PopupMenu();

                java.awt.MenuItem showItem = new java.awt.MenuItem("Timeline");
                java.awt.MenuItem quitItem = new java.awt.MenuItem("Quitter");
                //showItem.addActionListener(showListener);
                popup.add(showItem);
                popup.add(quitItem);

                // Tray icon
                BufferedImage trayIconImage = ImageIO.read(Main.class.getResource("/fr/esgi/twitterc/view/icon/twitter.png"));
                int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
                TrayIcon trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "TwitterC", popup);
                //trayIcon.addActionListener(showListener);

                // add the tray image
                tray.add(trayIcon);

                // Add listeners
                quitItem.addActionListener(e -> tray.remove(trayIcon));

                trayIcon.displayMessage("TwitterC", "Lancement de l'application !", TrayIcon.MessageType.INFO);
            } catch (AWTException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getAppName() {
        return "TwitterC";
    }

    @Override
    protected javafx.scene.image.Image getAppIcon() {
        return new javafx.scene.image.Image("/fr/esgi/twitterc/view/icon/twitter.png");
    }

    @Override
    protected String getFirstView() {
        if(TwitterClient.get().getCurrentUser() == null)
            return "PinView.fxml"; // Not authenticated
        return "ProfilView.fxml"; // Authenticated
    }
}
