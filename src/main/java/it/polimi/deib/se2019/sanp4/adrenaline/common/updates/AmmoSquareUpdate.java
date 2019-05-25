package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.AmmoSquareView;

public class AmmoSquareUpdate extends ModelUpdate {

    private static final long serialVersionUID = 6683102487494748072L;
    private AmmoSquareView ammoSquare;

    @JsonCreator
    public AmmoSquareUpdate (
            @JsonProperty("ammoSquare") AmmoSquareView ammoSquare) {
        super();
        this.ammoSquare = ammoSquare;
    }

    public AmmoSquareView getAmmoSquare() {
        return ammoSquare;
    }

    public void setAmmoSquare(AmmoSquareView ammoSquare) {
        this.ammoSquare = ammoSquare;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}
