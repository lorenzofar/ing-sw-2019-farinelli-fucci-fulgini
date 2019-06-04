package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

/**
 * Interface for visiting subclasses of {@link Square} using the <i>Visitor</i> pattern.
 */
public interface SquareVisitor {

    /**
     * Visits a square of type {@link AmmoSquare}
     * @param square The square to be visited
     */
    void visit(AmmoSquare square);

    /**
     * Visits a square of type {@link SpawnSquare}
     * @param square The square to be visited
     */
    void visit(SpawnSquare square);
}
