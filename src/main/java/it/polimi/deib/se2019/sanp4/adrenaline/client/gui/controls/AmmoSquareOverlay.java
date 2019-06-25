package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * A class describing an overlay representing an ammo square
 */
public class AmmoSquareOverlay extends SquareOverlay {

    /**
     * The ammo card contained in the square
     */
    private AmmoCard ammoCard;

    @FXML
    private Button overlayRoot;

    @FXML
    private GridPane squareContent;
    /**
     * The image container for the ammo card
     */
    private ImageView ammoCardImage;

    AmmoSquareOverlay(CoordPair location) {
        super(location);
        ammoCardImage = new ImageView();
    }

    @FXML
    public void initialize() {
        super.setSelectableRoot(overlayRoot);
    }

    @Override
    public void updateContent(Map<String, ColoredObject> players) {
        squareContent.getChildren().clear();
        updateAmmoCardImage();
        fillPlayers(players, squareContent);
        overlayRoot.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        overlayRoot.setGraphic(squareContent);
        overlayRoot.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
    }

    /**
     * Update the ammo card shown in the ImageView node
     */
    private void updateAmmoCardImage() {
        squareContent.getChildren().remove(ammoCardImage);
        String ammoCardPath = ammoCard == null ? "/images/ammo/ammo_null.png" : String.format("/images/ammo/ammo_%d.png", ammoCard.getId());
        ammoCardImage.setImage(new Image(ammoCardPath, 36, 36, true, true));
        squareContent.getChildren().add(ammoCardImage);
    }

    /**
     * Set the ammo card contained in the square
     *
     * @param ammoCard The object representing the ammo card
     */
    public void setAmmoCard(AmmoCard ammoCard) {
        this.ammoCard = ammoCard;
        updateAmmoCardImage();
    }
}
