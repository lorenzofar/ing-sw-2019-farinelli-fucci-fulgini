package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SpawnSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpawnSquareTest {

    @Mock
    private static SquareVisitor visitor;

    private static SpawnSquare square;
    private static AmmoCard ammoCard;
    private static Match mockMatch;
    private static Set<String> playersNames = new HashSet<>();
    private static CoordPair location = new CoordPair(0,1);

    @BeforeClass
    public static void classSetUp() {
        /* Load resources */
        ModelTestUtil.loadCreatorResources();
        playersNames.add("Sabbio");
        playersNames.add("Andonio");
        mockMatch = MatchCreator.createMatch(playersNames, new MatchConfiguration(0, 1));

        Map<AmmoCube, Integer> map = new HashMap<>();
        map.put(AmmoCube.BLUE, 3);
        ammoCard = new AmmoCard(1, map, false);
    }

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

    @Test
    public void isFull_fullCapacity_shouldReturnTrue() throws FullCapacityException {
        /* Fill the square */
        for (int i = 0; i < SpawnSquare.MAX_WEAPON_CARDS; i++) {
            square.insertWeaponCard(buildWeaponCard(i));
        }

        /* Check that it is full */
        assertTrue(square.isFull());
    }

    @Test
    public void isFull_newSquare_shouldReturnFalse() {
        assertFalse(square.isFull());
    }

    @Test
    public void generateView_ShouldSucceed() {
        Square square = mockMatch.getBoard().getSquare(location);
        square.addPlayer(mockMatch.getPlayerByName("Sabbio"));
        SpawnSquareView view = (SpawnSquareView) square.generateView();
        assertEquals(square.getLocation(), view.getLocation());
        assertEquals(square.getRoom().getColor(), view.getRoomColor());
        assertEquals(square.getLocation(), view.getLocation());
        assertTrue(view.getPlayers().containsAll(
                square.getPlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.toSet())));
    }

    @Test
    public void accept_shouldAcceptVisitor() {
        square.accept(visitor);

        verify(visitor).visit(square);
    }
}