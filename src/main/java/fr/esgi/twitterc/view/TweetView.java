package fr.esgi.twitterc.view;

import fr.esgi.twitterc.apiEngine.App;
import fr.esgi.twitterc.view.controller.ViewController;

/**
 * Created by 626 on 23/09/2015.
 */
public class TweetView extends ViewController {

    public static String ID = "TWEETVIEW";

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onShow() {
        System.out.println(App.TWEETNUMBER);
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
