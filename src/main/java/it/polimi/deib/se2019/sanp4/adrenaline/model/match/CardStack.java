package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import java.util.NoSuchElementException;

/**
 * A generic interface representing a stack of cards that can be drawn and discarded
 * @param <T> The type of cards contained in the stack
 */
public interface CardStack<T>{

    /**
     * Discard a card
     * @param card The card that has to be discarded, not null
     */
    void discard(T card);

    /**
     * Draw a card from the stack
     * @return The drawn card
     * @throws NoSuchElementException if the stack is empty
     */
    T draw();
}