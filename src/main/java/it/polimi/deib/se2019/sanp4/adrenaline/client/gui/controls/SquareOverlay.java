package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;

public abstract class SquareOverlay extends SelectableOverlay {

    private CoordPair location;

    SquareOverlay(String resource, CoordPair location) {
        super(resource);
        this.location = location;
    }

    public CoordPair getLocation() {
        return this.location;
    }
}
