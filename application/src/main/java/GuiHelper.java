import javafx.scene.image.ImageView;

/**
 * This class contain helper methods for the main GUI
 */
class GuiHelper {

    static ImageView imageFinder(String path, double width, double height, boolean preserveRatio) {
        ImageView image = new ImageView(path);
        image.setPreserveRatio(preserveRatio);
        image.setFitWidth(width);
        image.setFitHeight(height);
        return image;
    }
}
