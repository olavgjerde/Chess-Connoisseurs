import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URISyntaxException;

public class SoundClipManager {
    private MediaPlayer mediaPlayer;

    public SoundClipManager(String sound, boolean repeat, double volume, boolean enableSound) {
        // If player has muted sounds in options do nothing
        if (!enableSound) {
            return;
        }

        // Load file from resource folder
        String filePath = "sounds/" + sound;
        ClassLoader classLoader = getClass().getClassLoader();

        Media hit = null;
        try {
            hit = new Media(classLoader.getResource(filePath).toURI().toString());
        } catch (NullPointerException | URISyntaxException e) {
            System.out.println("MediaPlayer could not find the requested sound file" + filePath);
            e.printStackTrace();
        }

        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
        mediaPlayer.setVolume(volume);
        if (repeat) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
    }

    public void clear() {
        mediaPlayer.dispose();
    }
}
