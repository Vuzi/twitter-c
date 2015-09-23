package fr.esgi.twitterc.view;

import fr.esgi.twitterc.apiEngine.App;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import twitter4j.User;

/**
 * Created by Emerich on 23/09/2015.
 */
public class ProfileView extends ViewController {
    public WebView webview;
    public ImageView coverImage;
    public ImageView profilImage;
    public Label name;
    public Label tagName;
    public Label createdAt;
    public Label followers;

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onShow() {
        System.out.println(App.getUser());
        User user = App.getUser();

        // Set the banner image
        if(user.getProfileBannerURL() != null){
            String imageURL = user.getProfileBannerURL();

            imageURL =  imageURL.replace("_normal","");
            Image image = new Image(imageURL);
            coverImage.setImage(image);
        }

        // Set profil image
        if(user.getProfileImageURL() != null) {
            String imageURL = user.getProfileImageURL();

            imageURL =  imageURL.replace("_normal","");
            Image imagep = new Image(imageURL);
            profilImage.setImage(imagep);
        }

        // Set user real name
        name.setText(user.getName());

        // Set user TAG
        tagName.setText("@" + user.getScreenName());

        // Set followers count
        followers.setText("Followers : " + String.valueOf(user.getFollowersCount()));

        // Set tweets count
        createdAt.setText("Created at : " + user.getCreatedAt().toString());
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
