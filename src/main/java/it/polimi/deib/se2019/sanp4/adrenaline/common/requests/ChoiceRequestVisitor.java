package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

/**
 * An interface describing an object using the visitor pattern to handle choice requests
 */

public interface ChoiceRequestVisitor {

    void handle(ActionRequest request);
    void handle(BoardRequest request);
    void handle(PlayerOperationRequest request);
    void handle(PlayerRequest request);
    void handle(PowerupCardRequest request);
    void handle(SkullCountRequest request);
    void handle(SquareRequest request);
    void handle(WeaponCardRequest request);

}
