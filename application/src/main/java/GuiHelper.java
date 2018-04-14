import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class contain helper methods for the main GUI
 */
class GuiHelper {

    static ImageView imageFinder(Image image, double width, double height, boolean preserveRatio) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }
}
