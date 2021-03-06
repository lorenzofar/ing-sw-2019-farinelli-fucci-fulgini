package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A control extending {@link Pane} that allows the provided image to be rotated according to the provided orientation
 *
 * @author Lorenzo Farinelli
 */
public class OrientableImage extends Pane {

    /**
     * The orientation of the enum
     */
    private OrientationEnum orientation;

    OrientableImage() {
        super();
        this.orientation = OrientationEnum.UP;
    }

    /**
     * Sets the provided image as the background of the pane
     *
     * @param imagePath The path of the image
     */
    public void setImage(String imagePath) {
        Image image = new Image(imagePath);
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

    /**
     * Sets the orientation of the image
     *
     * @param orientation The object representing the orientation
     */
    public void setOrientation(OrientationEnum orientation) {
        this.orientation = orientation;
    }
}