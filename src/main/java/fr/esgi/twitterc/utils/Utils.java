package fr.esgi.twitterc.utils;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Collection of utility methods that does not belong in any other class.
 *
 * Created by Vuzi on 26/09/2015.
 */
public class Utils {

    private Utils() {}

    /**
     * Open the provided URL in the web browser.
     *
     * @param url The URL (string) to open.
     */
    public static void openWebPage(String url) {
        try {
            openWebPage(new URL(url));
        } catch (MalformedURLException e) {
            Logger.getLogger(Utils.class.getName()).info("Malformed provided URL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open the provided URL in the web browser.
     *
     * @param url The URL to open.
     */
    public static void openWebPage(URL url) {
        try {
            openWebPage(url.toURI());
        } catch (URISyntaxException e) {
            Logger.getLogger(Utils.class.getName()).info("Failed to get the URI from the provided URL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open the provided URI in the web browser.
     *
     * @param uri The URI to open.
     */
    public static void openWebPage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                Logger.getLogger(Utils.class.getName()).info("Failed to open the web browser : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Logger.getLogger(Utils.class.getName()).info("Desktop or browsing operation is not supported");
        }
    }

}
