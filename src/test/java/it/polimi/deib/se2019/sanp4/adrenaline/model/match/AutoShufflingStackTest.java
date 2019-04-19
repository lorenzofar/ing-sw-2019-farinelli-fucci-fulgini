package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoShufflingStackTest {

    @Test(expected = NullPointerException.class)
    public void createStack_emptyCardSetProvided_ShouldThrowNullPointerException(){
        List<Object> list = null;
        new AutoShufflingStack<>(list);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createStack_emptyCardSetProvided_ShouldThrowIllegalArgumentException(){
        List<Object> list = Collections.emptyList();
        new AutoShufflingStack<>(list);
    }

    @Test
    public void createStack_properCardSetProvided_ShouldNotThrowException(){
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        new AutoShufflingStack<>(list);
    }

    @Test
    public void createStack_singleCardProvided_ShouldDrawCard(){
        List<Object> list = new ArrayList<>();
        Object card = new Object();
        list.add(card);
        AutoShufflingStack<Object> autoShufflingStack = new AutoShufflingStack<>(list);
        assertSame(card, autoShufflingStack.draw());
    }

    @Test
    public void drawCard_onlyOneCardLeft_ShouldNotThrowException(){
        List<Object> list = new ArrayList<>();
        Object card1 = new Object();
        Object card2 = new Object();
        list.add(card1);
        list.add(card2);
        AutoShufflingStack<Object> autoShufflingStack = new AutoShufflingStack<>(list);
        autoShufflingStack.draw();
        autoShufflingStack.draw();

    }

    @Test (expected = NullPointerException.class)
    public void discardCard_nullCardProvided_ShouldThrowNullPointerException(){
        List<Object> list = new ArrayList<>();
        Object card = new Object();
        list.add(card);
        AutoShufflingStack autoShufflingStack = new AutoShufflingStack<>(list);
        autoShufflingStack.discard(null);
    }

    @Test
    public void discardCard_properCardProvided_ShouldNotThrowException(){
        List<Object> list = new ArrayList<>();
        Object card = new Object();
        list.add(card);
        AutoShufflingStack autoShufflingStack = new AutoShufflingStack<>(list);
        autoShufflingStack.discard(new Object());
    }

    @Test
    public void draw_noAvailableCardsAfter_discard_returnsCard(){
        List<Object> list = new ArrayList<>();
        Object card = new Object();
        list.add(card);
        AutoShufflingStack autoShufflingStack = new AutoShufflingStack<>(list);
        Object drawnCard = autoShufflingStack.draw();
        autoShufflingStack.discard(drawnCard);
        assertNotNull(autoShufflingStack.draw());
    }

    @Test
    public void discard_singleCardStack_drawShouldReturnSameCard(){
        List<Object> list = new ArrayList<>();
        Object card = new Object();
        list.add(card);
        AutoShufflingStack autoShufflingStack = new AutoShufflingStack<>(list);
        Object drawnCard = autoShufflingStack.draw();
        assertSame(drawnCard, card);
        autoShufflingStack.discard(drawnCard);
        assertSame(card, autoShufflingStack.draw());
    }

    @Test (expected = IllegalArgumentException.class)
    public void discard_availableCardProvided_SholdThrowIllegalArgumentException(){
        List<Object> list = new ArrayList<>();
        Object card1 = new Object();
        Object card2 = new Object();
        list.add(card1);
        list.add(card2);
        AutoShufflingStack autoShufflingStack = new AutoShufflingStack<>(list);
        Object drawnCard = autoShufflingStack.draw();
        list.remove(drawnCard);
        autoShufflingStack.discard(list.get(0));
    }
}
