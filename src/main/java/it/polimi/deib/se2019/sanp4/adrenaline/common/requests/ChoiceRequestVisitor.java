package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

/**
 * An interface describing an object using the visitor pattern to handle choice requests
 * <p>
 * Classes implementing this interface are encouraged to handle all types of requests.
 * If a request cannot be handled for a specific reason, the class which performed the request
 * (and is waiting for a response) should be notified in some way.
 * </p>
 *
 * @author Alessandro Fulgini
 */
public interface ChoiceRequestVisitor {

    /**
     * Handles the request to choose an action to be executed
     *
     * @param request The request to be handled, not null
     */
    void handle(ActionRequest request);

    /**
     * Handles the request to choose a board for the match, represented by its id
     *
     * @param request The request to be handled, not null
     */
    void handle(BoardRequest request);

    /**
     * Handles the request to choose a the operation to be performed during the current turn
     *
     * @param request The request to be handled, not null
     */
    void handle(PlayerOperationRequest request);

    /**
     * Handles the request to choose a player, represented by his username
     *
     * @param request The request to be handled, not null
     */
    void handle(PlayerRequest request);

    /**
     * Handles the request to choose a powerup card
     *
     * @param request The request to be handled, not null
     */
    void handle(PowerupCardRequest request);

    /**
     * Handles the request to choose the initial number of skulls on the killshot track
     *
     * @param request The request to be handled, not null
     */
    void handle(SkullCountRequest request);

    /**
     * Handles the request to choose a square on the board
     *
     * @param request The request to be handled, not null
     */
    void handle(SquareRequest request);

    /**
     * Handles the request to choose a weapon card
     *
     * @param request The request to be handled, not null
     */
    void handle(WeaponCardRequest request);

    /**
     * Handles the request to choose a weapon effect to be executed
     *
     * @param request The request to be handled, not null
     */
    void handle(EffectRequest request);

}
