package fr.esgi.twitterc.view.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TwitterMediaVideoView extends GridPane {

    @FXML
    private VBox mediaContent;
    @FXML
    private ImageView showButton;

    private Image image;
    private ImageView imageView;
    private boolean preview = true;

    private static final double MEDIA_PREVIEW_HEIGHT = 200;
    private static final double MEDIA_MAX_WIDTH = 590;

    public TwitterMediaVideoView(Image image) {
        this();
        setImage(image);
    }

    public TwitterMediaVideoView() {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/component/TwitterMediaView.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setImage(Image image) {
        this.image = image;

        mediaContent.getChildren().clear();
        imageView = null;
        preview = true;
        showButton.setVisible(true);

        if(image == null)
            return;

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        if(image.getHeight() > MEDIA_PREVIEW_HEIGHT * 1.3)
            showImagePreview();
        else {
            imageView.setFitWidth(image.getWidth() < MEDIA_MAX_WIDTH ? image.getWidth() : MEDIA_MAX_WIDTH);
            showButton.setVisible(false);
        }

        mediaContent.getChildren().add(imageView);
    }

    private void showImagePreview() {
        imageView.setViewport(new Rectangle2D(0, 0,
                image.getWidth() < MEDIA_MAX_WIDTH ? image.getWidth() : MEDIA_MAX_WIDTH, MEDIA_PREVIEW_HEIGHT));
        showButton.setOpacity(0.5);
    }

    private void showImage() {
        imageView.setViewport(new Rectangle2D(0, 0,
                image.getWidth() < MEDIA_MAX_WIDTH ? image.getWidth() : MEDIA_MAX_WIDTH, image.getHeight()));
        showButton.setOpacity(0.1);
    }

    @FXML
    public void showMedia() {
        if(image == null)
            return;

        if(preview)
            showImage();
        else
            showImagePreview();

        preview = !preview;
    }

    @FXML
    public void hoverButton() {
        if(preview)
            showButton.setOpacity(0.9);
        else
            showButton.setOpacity(0.7);
    }

    @FXML
    public void outButton() {
        if(preview)
            showButton.setOpacity(0.5);
        else
            showButton.setOpacity(0.1);
    }

}
