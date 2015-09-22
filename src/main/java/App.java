import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static Twitter TWITTER = TwitterFactory.getSingleton();

    /**
     * Entry point.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Swing UI initialization
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Twitter connection and OAuth
        try {
            TWITTER.setOAuthConsumer("APP_KEY", "SECRET_KEY");
            RequestToken requestToken = TWITTER.getOAuthRequestToken();

            openWebpage(new URI(requestToken.getAuthorizationURL()));

            String pin = (String) JOptionPane.showInputDialog(
                   null,
                   "<html>Please, enter the given <b>pin</b> code : </html>",
                   "Twitter credentials",
                   JOptionPane.PLAIN_MESSAGE,
                   null,
                   null,
                   "");

            TWITTER.getOAuthAccessToken(requestToken, pin);
        } catch (TwitterException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Main UI launching
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame panel = new JFrame();
                panel.setTitle("TwitterC");
                panel.setContentPane(new MainPanel());
                panel.setMinimumSize(new Dimension(900, 700));
                panel.setPreferredSize(new Dimension(900, 700));
                panel.setVisible(true);
                panel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }
        });
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
}
