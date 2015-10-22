package fr.esgi.twitterc.view.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Media (image) view.
 */
public class TwitterMediaView extends GridPane {

    @FXML
    protected VBox mediaContent;
    @FXML
    protected ImageView showButton;
    @FXML
    protected VBox imageControls;
    @FXML
    protected ImageView playButton;
    @FXML
    protected VBox videoControls;

    protected Image image;
    protected ImageView imageView;
    protected boolean preview = true;

    protected static final double MEDIA_PREVIEW_HEIGHT = 200;
    protected static final double MEDIA_MAX_WIDTH = 590;

    public TwitterMediaView(Image image) {
        this();
        setImage(image);
    }

    public TwitterMediaView() {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/component/TwitterMediaView.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected void setImage(Image image) {
        this.image = image;

        mediaContent.getChildren().clear();
        imageView = null;
        preview = true;
        imageControls.setVisible(true);
        imageControls.setManaged(true);
        videoControls.setVisible(false);
        videoControls.setManaged(false);

        if(image == null)
            return;

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(image.getWidth() < MEDIA_MAX_WIDTH ? image.getWidth() : MEDIA_MAX_WIDTH);

        if(image.getHeight() > MEDIA_PREVIEW_HEIGHT * 1.3)
            showImagePreview();
        else {
            showImageFull();
            preview = false;
        }

        mediaContent.getChildren().add(imageView);
    }

    protected void showImagePreview() {
        imageView.setViewport(new Rectangle2D(0, 0, image.getWidth(), MEDIA_PREVIEW_HEIGHT));
        showButton.setOpacity(0.5);
    }

    protected void showImageFull() {
        imageView.setViewport(null);
        imageControls.setVisible(false);
        imageControls.setManaged(false);
    }

    @FXML
    protected void showMedia() {
        if(image == null)
            return;

        showImageFull();
        preview = false;
    }

    @FXML
    protected void playMedia() {
        if(image == null)
            return;

        showImageFull();
        preview = false;
    }

    @FXML
    protected void hoverButton() {
        if(preview)
            showButton.setOpacity(0.9);
    }

    @FXML
    protected void outButton() {
        if(preview)
            showButton.setOpacity(0.5);
    }

}
