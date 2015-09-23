package fr.esgi.twitterc.view;

import fr.esgi.twitterc.apiEngine.App;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import twitter4j.Status;

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
}
