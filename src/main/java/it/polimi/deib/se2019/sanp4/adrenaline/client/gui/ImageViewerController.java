package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A controller used to manage windows where an image is shown
 */
public class ImageViewerController extends GUIController {

    @FXML
    private ImageView imageView;

    /**
     * Sets the image shown in the image view control
     *
     * @param imagePath The path of the image file
     */
    public void setImage(String imagePath) {
        imageView.setImage(new Image(imagePath, 300, 400, true, true));
    }
}
