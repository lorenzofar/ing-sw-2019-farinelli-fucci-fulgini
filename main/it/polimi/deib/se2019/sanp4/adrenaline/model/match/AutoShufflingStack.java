package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import java.util.Collections;
import java.util.Stack;

public class AutoShufflingStack<T> implements CardStack<T> {
    private Stack<T> discarded;
    private Stack<T> available;

    @Override
    public void discard(T card) {
        discarded.push(card);
    }

    @Override
    public T draw() {
        T extractedCard = available.pop();
        if(available.size() == 0){ // Available stack is empty, we need to reshuffle
            available.addAll(discarded); // Move the discarded cards in the available stack
            Collections.shuffle(available); // Shuffle the newly available cards
            discarded.removeAllElements(); // Empty the discarded stack
        }
        return extractedCard;
    }
}
