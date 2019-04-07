package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import java.util.Collection;
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

    /** The stack of cards available to be drawn */
    private Stack<T> available;

    /** Shuffles the stack of discarded cards and put them in the available stack */
    private void shuffle(){
        if(available.isEmpty()) { // Check anyway if the available stack is empty
            available = discarded; // Swap the stacks
            Collections.shuffle(available); // Shuffle the newly available cards
            discarded = new Stack<>();
        }
    }

    AutoShufflingStack(Collection<T> cards){
        this.discarded.addAll(cards);
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
     * If the stack is empty, it class the shuffle method.
     * @return The drawn card
     */
    @Override
    public T draw() {
        if(available.isEmpty()) shuffle();
        return available.pop();
    }
}
