package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.OrientationEnum;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A control extending Pane that allows the provided image to be rotated according to the provided orientation
 */
public class OrientableImage extends Pane {

    /**
     * Create a new OrientableImage with the provided image having the provided orientation
     *
     * @param image       The object representing the image
     * @param orientation The object representing the orientation
     */
    public OrientableImage(Image image, OrientationEnum orientation) {
        super();
        // Create a new temporary imageview container
        ImageView imageView = new ImageView(image);
        // Rotate the image inside it
        imageView.setRotate(orientation.getRotation());
        // Then generate a snapshot of the rotated image
        // and use it as the background of the pane
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = imageView.snapshot(params, null);
        // Make the background image fill the whole pane
        BackgroundSize imageSize = new BackgroundSize(100, 100, true, true, true, false);
        super.backgroundProperty().set(new Background(new BackgroundImage(rotatedImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, imageSize)));
    }

}
