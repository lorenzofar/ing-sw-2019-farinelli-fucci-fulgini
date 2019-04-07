package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import java.util.Collections;
import java.util.Stack;

/**
 * A generic class implementing the CardStack interface.
 * It automatically performs shuffling of discarded cards when there are no available cards.
 * @param <T> The type of cards contained in the stack
 */
public class AutoShufflingStack<T> implements CardStack<T> {

    /** The stack of discarded cards*/
    private Stack<T> discarded;

    /** The stack of cards available to be drawed */
    private Stack<T> available;

    /** Shuffles the stack of discarded cards and put them in the available stack */
    private void shuffle(){
        if(available.size() == 0) { // Check anyway if the available stack is empty
            available.addAll(discarded); // Move the discarded cards in the available stack
            Collections.shuffle(available); // Shuffle the newly available cards
            discarded.removeAllElements(); // Empty the discarded stack
        }
    }

    /**
     * Discard a card, putting it in the discarded stack
     * @param card The card that has to be discarded
     */
    @Override
    public void discard(T card) {
        discarded.push(card);
    }

    /**
     * Draws a card from the stack of the available cards.
     * If, after the drawing, the stack is empty, it class the shuffle method.
     * @return The drawn card
     */
    @Override
    public T draw() {
        T extractedCard = available.pop();
        if(available.size() == 0) shuffle();
        return extractedCard;
    }
}
