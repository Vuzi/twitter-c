package fr.esgi.twitterc.view.component;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.WindowEvent;

/**
 * Media (video) view.
 */
public class TwitterMediaVideoView extends TwitterMediaView {


    private String videoURL;
    private MediaPlayer mediaPlayer;
    private boolean playing;

    public TwitterMediaVideoView(Image previewImage, String videoURL) {
        this();
        setVideo(previewImage, videoURL);
    }

    public TwitterMediaVideoView() {
        super();
    }

    public void setVideo(Image previewImage, String videoURL) {
        setImage(previewImage);
        mediaPlayer = null;
        playing = false;

        if(imageView == null || videoURL == null)
            return;

        this.videoURL = videoURL;
    }

    private void showVideo() {
        showPlayerControls(false);

        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(new Media(videoURL));
        } else {
            mediaPlayer.getOnReady().run(); // Force ready
            mediaPlayer.play();
        }

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(imageView.getFitWidth());

        mediaView.setOnMouseClicked(event -> startOrStopVideo());

        mediaPlayer.setOnReady(() -> {
            mediaContent.getChildren().set(0, mediaView);
            mediaPlayer.play();
            playing = true;
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            showPlayerControls(true);
            mediaContent.getChildren().set(0, imageView);
            mediaPlayer.stop();
            playing = false;
        });

        getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> cleanVideo());

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                cleanVideo();
            }
        });
    }

    private void cleanVideo() {
        playing = false;
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        mediaContent.getChildren().set(0, imageView);
    }

    /**
     * Start or stop the video based on the current player states. Also hide/show the controls.
     */
    private void startOrStopVideo() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.pause();
            showPlayerControls(true);
        } else {
            mediaPlayer.play();
            showPlayerControls(false);
        }
    }

    /**
     * Show the video controls.
     *
     * @param show True to show, false to hide.
     */
    private void showPlayerControls(boolean show) {
        videoControls.setVisible(show);
        videoControls.setManaged(show);
    }

    @Override
    protected void showImageFull() {
        super.showImageFull();

        videoControls.setVisible(true);
        videoControls.setManaged(true);
    }

    @FXML
    protected void playMedia() {
        if(playing)
            startOrStopVideo();
        else if (!preview)
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
