package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SpawnSquareTest {
    private static SpawnSquare square;

    @Before
    public void setUp() throws Exception {
        /* Create an empty SpawnSquare so it is available for all tests */
        square = new SpawnSquare(new CoordPair(2, 5));
    }

    /* Builds a sample weapon card, the index will differentiate the cards */
    private static WeaponCard buildWeaponCard(int index){
        EffectDescription effectDescription = new EffectDescription(
                "sample_effect_" + index,
                "Basic effect",
                "I am a basic effect",
                Collections.singletonList(AmmoCubeCost.RED)
        );

        return new WeaponCard(
                "sample_weapon_" + index,
                "Weapon",
                Arrays.asList(AmmoCubeCost.BLUE, AmmoCubeCost.RED),
                Collections.singletonList(effectDescription)
        );
    }

    @Test
    public void constructor_emptySquare_shouldContainNoCards() {
        assertTrue(square.getWeaponCards().isEmpty());
    }

    @Test (expected = NullPointerException.class)
    public void insertWeaponCard_NullWeaponProvided_ShouldThrowNullPointerException() throws FullCapacityException {
        square.insertWeaponCard(null);
    }

    @Test
    public void insertWeaponCard_emptyContainer_shouldBeInserted() throws FullCapacityException {
        /* Build a sample weapon card */
        WeaponCard card = buildWeaponCard(1);

        /* Now try to insert the card, should not throw any exception */
        square.insertWeaponCard(card);
        /* Check that the card has been added in the first place of the list */
        List<WeaponCard> cards = square.getWeaponCards();
        assertEquals(0, cards.indexOf(card));
        assertEquals(1, cards.size());
    }

    @Test
    public void insertWeaponCard_fullContainer_shouldThrow() {
        /* Create an empty square */
        SpawnSquare square = new SpawnSquare(new CoordPair(2, 5));

        /* Generate the cards to fill the square */
        List<WeaponCard> cards = IntStream.range(0, SpawnSquare.MAX_WEAPON_CARDS)
                .mapToObj(SpawnSquareTest::buildWeaponCard).collect(Collectors.toList());

        /* Fill the square with cards */
        try {
            for (WeaponCard card : cards) {
                square.insertWeaponCard(card);
            }
        } catch (FullCapacityException e) {
            /* If the container gets full here, the test should fail */
            fail();
        }
        assertEquals(SpawnSquare.MAX_WEAPON_CARDS, square.getWeaponCards().size());

        /* Now try to add another card */
        try {
            square.insertWeaponCard(buildWeaponCard(SpawnSquare.MAX_WEAPON_CARDS + 1));
        } catch (FullCapacityException e) {
            assertTrue(true);
        }

        /* Check that the square still has the old cards, in the correct order */
        assertTrue(square.getWeaponCards().containsAll(cards));
    }

    @Test(expected = IllegalStateException.class)
    public void insertWeaponCard_alreadyInserted_shouldThrow() throws FullCapacityException {
        SpawnSquare square = new SpawnSquare(new CoordPair(2, 5));
        WeaponCard card = buildWeaponCard(1);
        square.insertWeaponCard(card); /* This goes fine */
        square.insertWeaponCard(card); /* This throws */
    }

    @Test(expected = NullPointerException.class)
    public void grabWeaponCard_nullParameter_shouldThrow() throws CardNotFoundException {
        square.grabWeaponCard(null);
    }

    @Test(expected = CardNotFoundException.class)
    public void grabWeaponCard_notInserted_shouldThrow() throws CardNotFoundException, FullCapacityException {
        /* Build a sample card and insert it in the square */
        WeaponCard card = buildWeaponCard(1);
        square.insertWeaponCard(card);

        /* Request a different card */
        square.grabWeaponCard("weapon_notinserted");
    }

    @Test
    public void grabWeaponCard_inserted_shouldReturn() throws CardNotFoundException, FullCapacityException {
        /* Build two sample cards and insert them in the square */
        WeaponCard card1 = buildWeaponCard(1);
        WeaponCard card2 = buildWeaponCard(2);
        square.insertWeaponCard(card1);
        square.insertWeaponCard(card2);

        /* Retrieve the first one */
        WeaponCard grabbed =  square.grabWeaponCard(card1.getId());
        assertEquals(card1, grabbed); /* Check that it is the right card */
        assertFalse(square.getWeaponCards().contains(card1)); /* Check that it has been removed */
    }
}