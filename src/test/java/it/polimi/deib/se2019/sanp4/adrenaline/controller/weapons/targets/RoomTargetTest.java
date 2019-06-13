package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.ChooseNoneAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeaponStub;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.concurrent.CancellationException;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class RoomTargetTest {

    private static Match match;
    private static Board board;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static ControllerFactory factory;
    private static AbstractWeapon weapon;
    private static WeaponCard weaponCard;

    @BeforeClass
    public static void classSetup() throws Exception {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, 5);

        /* Create list of unique users */
        validNames = new HashSet<>();
        validNames.add("bzoto");
        validNames.add("loSqualo");
        validNames.add("zoniMyLord");

        /* Create a test weapon card */
        weaponCard = WeaponCreator.createWeaponCard("furnace");
    }

    @Before
    public void setUp() {
        /* Create a match */
        match = MatchCreator.createMatch(validNames, validConfig);
        board = match.getBoard();

        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n, v);
        });

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);

        /* Create a stub of the abstract weapon */
        weapon = new AbstractWeaponStub(weaponCard, match, views, factory);
    }

    /* ========== SELECTION CASES: NONE, ONE, MULTIPLE, AUTOMATIC ================ */

    @Test
    public void execute_onlyOneSelectable_mandatory_shouldAutoSelect() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the desired target player in a targetable room */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(0,2));

        /* Set the other player in a room far away */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,0));

        /* Set up the target: max distance of the nearest square of the room is 1 */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(2);
        target.setMarks(1);

        /* Execute */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target received the damage and marks */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(2));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(1));
    }

    @Test
    public void execute_onlyOneSelectable_optional_shouldAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the desired target player in a targetable room */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(0,2));

        /* Set the other player in a room far away */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,0));

        /* Set up the target: max distance of the nearest square of the room is 1 */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setOptional(true);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(2);
        target.setMarks(1);

        /* Set up user's answer: choose not to shoot */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer(new ChooseNoneAnswer<>());

        /* Execute */
        assertFalse(target.execute(view));

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the targets received no damage and marks */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(0));
        assertThat(t2.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t2.getPlayerBoard().getMarksByPlayer(shooter), is(0));
    }

    @Test
    public void execute_noneSelectable_shouldTerminate() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set both targets in rooms out of the visibility scope */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,1));


        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(3,2));

        /* Set up the target: the nearest square of the room must be visible */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);

        /* Execute */
        assertFalse(target.execute(view));

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the targets received no damage and marks */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(0));
        assertThat(t2.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t2.getPlayerBoard().getMarksByPlayer(shooter), is(0));
    }

    @Test
    public void execute_multipleSelectable_shouldAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the desired target player in a targetable room */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(0,2));

        /* Set the other player in another targetable room */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,0));

        /* Set up the target: the nearest square of the room must be visible */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);

        /* Prepare answer: select the room with t1 */
        Set<CoordPair> givenChoices = new HashSet<>(5);
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req -> {
            givenChoices.addAll(req.getChoices());
            return new CompletableChoice<>(req).complete(new CoordPair(2,2));
        });

        /* Execute */
        target.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the given choices were as expected */
        Set<CoordPair> expected = new HashSet<>();
        expected.add(new CoordPair(1,0));
        expected.add(new CoordPair(2,0));
        expected.add(new CoordPair(0,2));
        expected.add(new CoordPair(1,2));
        expected.add(new CoordPair(2,2));
        assertTrue(givenChoices.containsAll(expected));
        assertEquals(expected.size(), givenChoices.size());

        /* Check that the target received the damage and marks */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(2));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(1));
    }

    /* ======================= EXCLUSIONS ====================== */

    @Test
    public void execute_selectsOwnRoom_shouldNotReceiveDamage() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,0));
        PersistentView view = views.get("bzoto");

        /* Set the desired target player in the same room */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,0));

        /* The other player is not spawned, so he'll be ignored */

        /* Set up the target: the nearest square of the room must be visible */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);

        /* The room is auto-selected */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target received the damage and marks while the shooter didn't */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(2));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(1));
        assertThat(shooter.getPlayerBoard().getDamageCount(), is(0));
        assertThat(shooter.getPlayerBoard().getMarksByPlayer(shooter), is(0));
    }

    @Test
    public void execute_excludeSquare_shouldNotReceiveDamage() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,2));
        PersistentView view = views.get("bzoto");

        /* Set the other players in the adjacent room, in different squares */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,1));
        weapon.saveSquare("sqr", t1.getCurrentSquare()); /* Save the square in the weapon */

        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,1));

        /* Set up the target: the nearest square of the room must be visible */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setExcludeSquares(Collections.singleton("sqr")); /* Exclude square where t1 is */
        target.setDamage(2);
        target.setMarks(1);

        /* The room is auto-selected */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target received the damage and marks */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(2));
        assertThat(t2.getPlayerBoard().getMarksByPlayer(shooter), is(1));
    }

    @Test
    public void execute_excludePlayer_shouldNotReceiveDamage() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,2));
        PersistentView view = views.get("bzoto");

        /* Set the other players in the adjacent room, in different squares */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,1));
        weapon.savePlayer("red", t1); /* Save the player in the weapon */

        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,1));

        /* Set up the target: the nearest square of the room must be visible */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setExcludePlayers(Collections.singleton("red")); /* Exclude t1 */
        target.setDamage(2);
        target.setMarks(1);

        /* The room is auto-selected */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target received the damage and marks */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(2));
        assertThat(t2.getPlayerBoard().getMarksByPlayer(shooter), is(1));
    }

    @Test
    public void execute_cancellation_shouldThrow() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the both players in different visible rooms */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,0));
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,2));

        /* Set up the target: the nearest square of the room must be visible */
        RoomTarget target = new RoomTarget("room", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);

        /* Set up user's answer: cancel */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Execute */
        try {
            target.execute(view);
            fail();
        } catch (CancellationException e) {
            /* Ok */
        }

        /* Check that the players received no damage and marks */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(0));
        assertThat(t2.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t2.getPlayerBoard().getMarksByPlayer(shooter), is(0));
    }
}