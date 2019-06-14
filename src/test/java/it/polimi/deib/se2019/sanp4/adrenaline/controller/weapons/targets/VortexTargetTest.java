package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.ChooseNoneAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.FirstChoiceAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeaponStub;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
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

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class VortexTargetTest {
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
            views.put(n,v);
        });

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);

        /* Create a stub of the abstract weapon */
        weapon = new AbstractWeaponStub(weaponCard, match, views, factory);
    }

    /* =============== VORTEX SELECTION ================== */

    @Test
    public void execute_noVortexSelectable_shouldTerminate() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other players far away */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));

        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(3,2));

        /* Set up the target */
        VortexTarget target = new VortexTarget("v", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);

        /* Execute */
        assertFalse(target.execute(view));

        /* Check no interaction with the user */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the vortex has not been saved */
        assertNull(weapon.getSavedSquare("vortex"));
    }

    @Test
    public void execute_vortexSelectable_shouldAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player so that the vortex can be (2,1) */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,1));

        /* Set the second player so that the vortex can be (1,2) */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,2));

        /* Set up the target */
        VortexTarget target = new VortexTarget("v", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Set up the user's answer */
        Set<CoordPair> givenChoices = new HashSet<>(2);
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req -> {
            givenChoices.addAll(req.getChoices());
            return new CompletableChoice<>(req).complete(new CoordPair(1,2));
        });

        /* The player t1 is more than 1 step away from (0,2), so he'll be discarded */
        target.execute(view);

        Square vortex = board.getSquare(1,2);

        /* Check single interaction with the user */
        verify(view).sendChoiceRequest(any());

        /* Check that the choices are correct */
        Set<CoordPair> expected = new HashSet<>();
        expected.add(new CoordPair(1,2));
        expected.add(new CoordPair(2,1));
        assertTrue(givenChoices.containsAll(expected));
        assertEquals(expected.size(), givenChoices.size());

        /* Check that t2 received the damage and has been moved to the vortex */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(1));
        assertThat(t2.getCurrentSquare(), is(vortex));

        /* Check that the vortex has been saved */
        assertThat(weapon.getSavedSquare("vortex"), is(vortex));
        assertThat(weapon.getSavedPlayer("v"), is(t2));
    }

    /* =============== PLAYER SELECTION ================== */

    @Test
    public void execute_oneSelectablePlayer_mandatory_shouldAutoSelect() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player to be out of range */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));

        /* Set the second player so that the vortex can be (1,2) */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,2));

        /* Set up the target */
        VortexTarget target = new VortexTarget("v", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req ->
                new CompletableChoice<>(req).complete(new CoordPair(1,2))
        );

        /* The player t1 is more than 1 step away from (0,2), so he'll be discarded */
        target.execute(view);

        Square vortex = board.getSquare(1,2);

        /* Check single interaction with the user */
        verify(view).sendChoiceRequest(any());

        /* Check that t2 received the damage and has been moved to the vortex */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(1));
        assertThat(t2.getCurrentSquare(), is(vortex));

        /* Check that the vortex has been saved */
        assertThat(weapon.getSavedSquare("vortex"), is(vortex));
        assertThat(weapon.getSavedPlayer("v"), is(t2));
    }

    @Test
    public void execute_oneSelectablePlayer_optional_shouldAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player to be out of range */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));

        /* Set the second player so that the vortex can be (1,2) */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,2));

        /* Set up the target */
        VortexTarget target = new VortexTarget("v", weapon, match, factory);
        target.setOptional(true);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Set up the user's answer: choose none */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer(new FirstChoiceAnswer());
        when(view.sendChoiceRequest(any(PlayerRequest.class))).thenAnswer(new ChooseNoneAnswer<>());

        /* The player t1 is more than 1 step away from (0,2), so he'll be discarded */
        assertFalse(target.execute(view));

        /* Check two interactions with the user */
        verify(view, times(2)).sendChoiceRequest(any());

        /* Check that t2 did not receive the damage */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(0));

        /* Check that the vortex has been saved */
        assertThat(weapon.getSavedSquare("vortex"), is(board.getSquare(1,2)));
    }

    @Test
    public void execute_singleVortex_multiplePlayers_shouldAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set both players so that the vortex can be (1,2) */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,2));

        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,2));

        /* Set up the target */
        VortexTarget target = new VortexTarget("v", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Set up the user's answers */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req ->
                new CompletableChoice<>(req).complete(new CoordPair(1,2))
        );
        when(view.sendChoiceRequest(any(PlayerRequest.class))).thenAnswer((SendChoiceRequestAnswer<String>) req ->
                new CompletableChoice<>(req).complete("zoniMyLord")
        );

        /* Both players are taken into account */
        target.execute(view);

        Square vortex = board.getSquare(1,2);

        /* Check two interactions with the user */
        verify(view, times(2)).sendChoiceRequest(any());

        /* Check that t2 received the damage and has been moved to the vortex */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(1));
        assertThat(t2.getCurrentSquare(), is(vortex));

        /* Check that the vortex has been saved */
        assertThat(weapon.getSavedSquare("vortex"), is(vortex));
        assertThat(weapon.getSavedPlayer("v"), is(t2));
    }

    @Test
    public void execute_vortexAlreadySaved_shouldNotAskSelection() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set both players so that the vortex can be (1,2) */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,2));

        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,2));

        /* Save the vortex */
        Square vortex = board.getSquare(1,2);
        weapon.saveSquare("vortex", vortex);

        /* Set up the target */
        VortexTarget target = new VortexTarget("v", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(PlayerRequest.class))).thenAnswer((SendChoiceRequestAnswer<String>) req ->
                new CompletableChoice<>(req).complete("zoniMyLord")
        );

        /* Both players are taken into account */
        target.execute(view);

        /* Check single interaction with the user */
        verify(view).sendChoiceRequest(any());

        /* Check that t2 received the damage and has been moved to the vortex */
        assertThat(t2.getPlayerBoard().getDamageCount(), is(1));
        assertThat(t2.getCurrentSquare(), is(vortex));
    }
}