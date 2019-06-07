package it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SquareView;

public class OverlaysFactory {

    private static OverlaysFactory instance = new OverlaysFactory();

    public static OverlaysFactory getInstance() {
        return instance;
    }

    /**
     * Creates an overlay representing the provided square view
     *
     * @param squareView The object representing the square view
     * @return The created overlay, null if the provided square is null
     */
    public SquareOverlay createSquareOverlay(SquareView squareView) {
        if (squareView == null) {
            return null;
        }
        String squareTypeMarker = squareView.printTypeMarker();
        if (squareTypeMarker.equals("S")) {
            return new SpawnSquareOverlay(squareView.getLocation());
        } else {
            return new AmmoSquareOverlay(squareView.getLocation());
        }
    }
}
