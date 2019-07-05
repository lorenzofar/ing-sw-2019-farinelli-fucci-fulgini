package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;

/**
 * An update sent when a {@link Square} changes its status
 *
 * @author Tiziano Fucci
 */
public class SquareUpdate extends ModelUpdate {

    private static final long serialVersionUID = 6683102487494748072L;
    private SquareView square;

    @JsonCreator
    public SquareUpdate(
            @JsonProperty("square") SquareView square) {
        super();
        this.square = square;
    }

    /**
     * Retrieves the view of the square
     *
     * @return The object representing the view
     */
    public SquareView getSquare() {
        return square;
    }

    /**
     * Sets the view of the square
     *
     * @param square The object representing the view
     */
    public void setSquare(SquareView square) {
        this.square = square;
    }

    @Override
    public void accept(ModelUpdateVisitor visitor) {
        visitor.handle(this);
    }
}
