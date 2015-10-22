package fr.esgi.twitterc.view.component;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class TwitterMediaVideoView extends TwitterMediaView {


    private String videoURL;
    private Media media;

    public TwitterMediaVideoView(Image previewImage, String videoURL) {
        this();
        setVideo(previewImage, videoURL);
    }

    public TwitterMediaVideoView() {
        super();
    }

    public void setVideo(Image previewImage, String videoURL) {
        setImage(previewImage);

        if(imageView == null || videoURL == null)
            return;

        this.videoURL = videoURL;
    }

    private void showVideo() {
        videoControls.setOpacity(0);

        if(media == null)
            media = new Media(videoURL);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(590);

        mediaPlayer.setOnReady(() -> {
            mediaContent.getChildren().set(0, mediaView);
            mediaPlayer.play();
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            videoControls.setOpacity(1);
            mediaContent.getChildren().set(0, imageView);
        });

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                mediaPlayer.stop();
                videoControls.setOpacity(1);
                mediaContent.getChildren().set(0, imageView);
            }
        });
    }

    @FXML
    protected void showMedia() {
        super.showMedia();

        videoControls.setVisible(true);
        videoControls.setManaged(true);
    }

    @FXML
    protected void playMedia() {
        if (!preview)
            showVideo();
    }

    @FXML
    protected void hoverButton() {
        if(preview)
            showButton.setOpacity(0.9);
        else
            playButton.setOpacity(0.9);
    }

    @FXML
    protected void outButton() {
        if(preview)
            showButton.setOpacity(0.5);
        else
            playButton.setOpacity(0.5);
    }
}
