package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

/**
 * A generic interface representing a stack of cards that can be drawn and discarded
 * @param <T> The type of cards contained in the stack
 */
public interface CardStack<T>{

    /**
     * Discard a card
     * @param card The card that has to be discarded
     */
    public void discard(T card);

    /**
     * Draw a card from the stack
     * @return The drawn card
     */
    T draw();
}