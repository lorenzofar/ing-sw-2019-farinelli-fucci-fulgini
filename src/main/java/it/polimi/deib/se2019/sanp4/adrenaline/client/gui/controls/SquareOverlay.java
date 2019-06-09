package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

/**
 * An abstract class describing an overlay representing a square of the game board
 */
public abstract class SquareOverlay extends SelectableOverlay<CoordPair> {

    SquareOverlay(String resource, CoordPair location) {
        super(resource);
        this.setData(location);
    }
}
