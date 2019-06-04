package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.AmmoSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AmmoSquareTest {

    @Mock
    private static SquareVisitor visitor;

    private static AmmoSquare ammoSquare;
    private static AmmoCard ammoCard;
    private static Match mockMatch;
    private static Set<String> playersNames = new HashSet<>();
    private static CoordPair location = new CoordPair(1,1);

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
        /* Create a new ammosquare before each test */
        ammoSquare = new AmmoSquare(new CoordPair(5,5));
    }

    @Test (expected = IllegalStateException.class)
    public void grabAmmo_EmptyAmmoSquare_ShouldThrowIllegalStateException(){
        ammoSquare.grabAmmo();
    }

    @Test (expected = NullPointerException.class)
    public void insertAmmo_NullCardProvided_ShouldThrowNullPointerException(){
        ammoSquare.insertAmmo(null);
    }

    @Test
    public void grabAmmo_ValidCardProvided_ShouldReturnSameCard(){
        ammoSquare.insertAmmo(ammoCard);
        assertEquals(ammoCard, ammoSquare.grabAmmo());
    }

    @Test
    public void getAmmoCard_ShouldReturnSameCard(){
        ammoSquare.insertAmmo(ammoCard);
        assertEquals(ammoCard, ammoSquare.getAmmoCard());
    }

    @Test
    public void isFull_emptySquare_shouldReturnFalse() {
        assertFalse(ammoSquare.isFull());
    }

    @Test
    public void isFull_cardInserted_shouldReturnTrue() {
        ammoSquare.insertAmmo(ammoCard);
        assertTrue(ammoSquare.isFull());
    }

    @Test
    public void generateView_ShouldSucceed() {
        Square square = mockMatch.getBoard().getSquare(location);
        square.addPlayer(mockMatch.getPlayerByName("Sabbio"));
        AmmoSquareView view = (AmmoSquareView) square.generateView();
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
        ammoSquare.accept(visitor);

        verify(visitor).visit(ammoSquare);
    }
}