package fr.esgi.twitterc.utils;

import com.twitter.Extractor;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
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

    /**
     * Parse the provided tweet, and return a list of all the elements contained.
     *
     * @param tweet The tweet to parse.
     * @return The tweet parsed.
     */
    public static List<String> parseTweet(String tweet) {
        if(tweet == null)
            tweet = "";

        List<String> tweetParsed = new ArrayList<>();
        Extractor extractor = new Extractor();
        int index = 0;

        // Elements (ordered by position in the tweet)
        List<Extractor.Entity> entities = new ArrayList<>();
        entities.addAll(extractor.extractURLsWithIndices(tweet));
        entities.addAll(extractor.extractHashtagsWithIndices(tweet));
        entities.addAll(extractor.extractMentionedScreennamesWithIndices(tweet));

        Collections.sort(entities, (e1, e2) -> e1.getStart() - e2.getStart());

        // Cut the tweet
        for(Extractor.Entity entity : entities) {
            tweetParsed.add(tweet.substring(index, entity.getStart()));
            tweetParsed.add(tweet.substring(entity.getStart(), entity.getEnd()));
            index = entity.getEnd();
        }

        // Last part
        if(index < tweet.length())
            tweetParsed.add(tweet.substring(index));

        return tweetParsed;
    }

}
