import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

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
        File file = new File(classLoader.getResource(filePath).getFile());

        Media hit = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
        mediaPlayer.setVolume(volume);
        if (repeat) {
            mediaPlayer.setCycleCount(mediaPlayer.INDEFINITE);
        }
    }

    public void clear() {
        mediaPlayer.dispose();
    }
}
