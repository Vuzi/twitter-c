package fr.esgi.twitterc.view;

import fr.esgi.twitterc.apiEngine.App;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 626 on 23/09/2015.
 */
public class TweetView extends ViewController {

    public static String ID = "TWEETVIEW";
    public Label tweetosName;
    public Label tweetContent;
    public WebView tweetWebview;

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onShow() {
        System.out.println(App.TWEETNUMBER);
        Status status = App.showTweet();
        tweetosName.setText("@" + status.getUser().getScreenName());
        tweetContent.setText(status.getText());
        tweetContent.setWrapText(true);

        ArrayList<String> lien = pullLinks(status.getText());
        final WebEngine webEngine = tweetWebview.getEngine();
        Hyperlink hpl = new Hyperlink(lien.get(0));
        hpl.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                webEngine.load(lien.get(0));
            }
        });
    }

    @Override
    protected void onHide() {

    }

    @Override
    protected void onDeletion() {

    }

    @Override
    protected String getID() {
        return null;
    }

    //Pull all links from the body for easy retrieval
    private ArrayList pullLinks(String text) {
        ArrayList links = new ArrayList();

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }
}
