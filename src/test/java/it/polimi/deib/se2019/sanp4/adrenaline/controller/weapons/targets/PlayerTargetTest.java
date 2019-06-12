package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.SquareRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.ChooseNoneAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeaponStub;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum;
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

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class PlayerTargetTest {

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

    /* ========== SELECTION CASES: NONE, ONE, MULTIPLE, AUTOMATIC ================ */

    @Test
    public void execute_alreadySaved_shouldAutoSelect() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Select the desired target player */
        Player t = match.getPlayerByName("loSqualo");
        board.movePlayer(t, board.getSquare(2,1));
        weapon.savePlayer("red", t);

        /* Execute the target: player loSqualo will be automatically be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target received the damage and marks */
        assertThat(t.getPlayerBoard().getDamageCount(), is(2));
        assertThat(t.getPlayerBoard().getMarksByPlayer(shooter), is(1));
    }

    @Test
    public void execute_onlyOneSelectable_mandatory_shouldAutoSelect() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the desired target player */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));

        /* Set another player out of scope (default is visible) */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,0));

        /* Execute the target: player loSqualo will be automatically be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);
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

        /* Set the desired target player */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));

        /* Set another player out of scope (default is visible) */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,0));

        /* Set up user's answer: choose no target */
        when(view.sendChoiceRequest(any(PlayerRequest.class))).thenAnswer(new ChooseNoneAnswer<>());

        /* Execute the target: player loSqualo will be automatically be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setOptional(true);
        target.setDamage(2);
        target.setMarks(1);
        target.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the possible target did not receive the damage and marks */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(0));
    }

    @Test
    public void execute_noneSelectable_shouldTerminate() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other player out of scope */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(0,0));

        /* Execute */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        assertFalse(target.execute(view));

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that no target has been saved */
        assertNull(weapon.getSavedPlayer("red"));
    }

    @Test
    public void execute_multipleSelectable_shouldAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));

        /* Set the second player in a visible square */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,0));

        /* Set up user's answer */
        when(view.sendChoiceRequest(any(PlayerRequest.class))).thenAnswer((SendChoiceRequestAnswer<String>) req ->
                new CompletableChoice<>(req).complete("zoniMyLord")
        );

        /* Execute the target: both players can be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setOptional(true);
        target.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t2));
    }

    /* ================== MOVE TARGET BEFORE ==================== */

    @Test
    public void execute_moveTargetBefore_notEnoughStepsToBeShot_shouldTerminate() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,0));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a square far away */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));

        /* The other player is not in a square, so he'll be discarded */

        /* Set up the target */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setMoveTargetBefore(1);

        /* The player t1 cannot be moved to a square visible from (1,0) in just one step */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that no target has been saved */
        assertNull(weapon.getSavedPlayer("red"));
    }

    @Test
    public void execute_moveTargetBefore_onlyOneDestination_shouldAutoMove() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,0));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a square far away */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));

        /* The other player is not in a square, so he'll be discarded */

        /* Set up the target */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setMoveTargetBefore(2);

        /* The player t1 can only be moved to (2,1) to be visible from (1,0) */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target has been moved */
        assertThat(t1.getCurrentSquare(), is(board.getSquare(2,1)));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t1));
    }

    @Test
    public void execute_moveTargetBefore_directional_shouldSaveDirectionAfterMoving() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a square far away */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(3,2));

        /* The other player is not in a square, so he'll be discarded */

        /* Set up the weapon and the target */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setMoveTargetBefore(2);

        /* The player t1 can be moved to (2,1) or (1,2) to be visible from (1,1) in a straight line */

        /* Set up answer: select the square to the south */
        final List<CoordPair> receivedChoices = new ArrayList<>();
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req -> {
            receivedChoices.addAll(req.getChoices());
            return new CompletableChoice<>(req).complete(new CoordPair(1,2));
        });

        /* The target is auto-selected */
        target.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the target has been moved */
        assertThat(t1.getCurrentSquare(), is(board.getSquare(1,2)));

        /* Check that the choices given to the user are correct */
        List<CoordPair> expected = new ArrayList<>(2);
        expected.add(new CoordPair(2,1));
        expected.add(new CoordPair(1,2));
        assertTrue(receivedChoices.containsAll(expected));
        assertTrue(expected.containsAll(receivedChoices));

        /* Check that the direction of the weapon has been selected */
        assertThat(weapon.getSelectedDirection(), is(S));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t1));
    }

    /* ================== MOVE TARGET AFTER ==================== */

    @Test
    public void execute_moveTargetAfter_shouldBeMoved() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,0));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(0,0));

        /* The other player is not in a square, so he'll be discarded */

        /* Set up the target */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setSquareRef("sqr");
        target.setMoveTargetAfter(2);

        /* Set up user's response */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req ->
                new CompletableChoice<>(req).complete(new CoordPair(0,2))
        );

        /* The target is auto-selected */
        target.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the target has been moved */
        assertThat(t1.getCurrentSquare(), is(board.getSquare(0,2)));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t1));

        /* Check that the saved reference is the square where the player has been shot */
        assertThat(weapon.getSavedSquare("sqr"), is(board.getSquare(0,0)));
    }

    @Test
    public void execute_moveTargetAfter_canOnlyBeMovedToSameSquare_shouldNotAskPlayer() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,0));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(0,0));

        /* The other player is not in a square, so he'll be discarded */

        /* Set up the weapon and the target */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);
        weapon.selectCardinalDirection(W); /* The target can't be moved because of the wall */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setMoveTargetAfter(2);

        /* The target is auto-selected */
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target has not been moved */
        assertThat(t1.getCurrentSquare(), is(board.getSquare(0,0)));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t1));
    }

    @Test
    public void execute_moveTargetAfter_directional_shouldOnlyMoveInGivenDirection() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(0,2));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,2));

        /* Set up the weapon and the target */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL); /* Do not select direction */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setMoveTargetAfter(2);

        /* Set up user's answer: the direction is EAST */
        final List<CoordPair> receivedChoices = new ArrayList<>();
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req -> {
            receivedChoices.addAll(req.getChoices());
            return new CompletableChoice<>(req).complete(new CoordPair(3,2));
        });

        /* The target is auto-selected, the direction is inferred */
        target.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check that the target has been moved */
        assertThat(t1.getCurrentSquare(), is(board.getSquare(3,2)));

        /* Check that the choices given to the user are correct */
        List<CoordPair> expected = new ArrayList<>(2);
        expected.add(new CoordPair(1,2));
        expected.add(new CoordPair(2,2));
        expected.add(new CoordPair(3,2));
        assertTrue(receivedChoices.containsAll(expected));
        assertTrue(expected.containsAll(receivedChoices));

        /* Check that the direction of the weapon has been selected */
        assertThat(weapon.getSelectedDirection(), is(E));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t1));
    }

    /* ============= EXCLUSIONS AND CHOOSE BETWEEN TARGETS =============== */

    @Test
    public void execute_excludePlayers_shouldBeExcluded() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square, save it as previous target */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));
        weapon.savePlayer("blue", t1);

        /* Set the second player in a visible square */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,0));

        /* Execute the target: only t2 can be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setExcludePlayers(Collections.singleton("blue"));
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t2));
    }

    @Test
    public void execute_excludeSquares_shouldBeExcluded() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square, save its square previous target */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));
        weapon.saveSquare("sqr", t1.getCurrentSquare());

        /* Set the second player in a visible square */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,0));

        /* Execute the target: only t2 can be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setExcludeSquares(Collections.singleton("sqr"));
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t2));
    }

    @Test
    public void execute_chooseBetweenTargets_shouldLimitSelection() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square, save it as a previous target */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));
        weapon.savePlayer("blue", t1);

        /* Set the second player in a visible square, save it as a previous target */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,0));
        weapon.savePlayer("green", t2);

        /* Execute the target: only t2 can be selected */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setChooseBetweenTargets(new HashSet<>(Arrays.asList("green", "notSaved")));
        target.execute(view);

        /* Check no interaction with shooter */
        verify(view, never()).sendChoiceRequest(any());

        /* Check that the target has been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t2));
    }

    /* ============ MOVE SHOOTER AND SQUARE REF ================ */

    @Test
    public void execute_moveShooterHere_shouldMove() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));

        /* The other player will be discarded */

        /* Execute the target */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setMoveShooterHere(true);
        target.execute(view);

        /* Check that the shooter has been moved */
        assertThat(shooter.getCurrentSquare(), is(board.getSquare(2,1)));
    }

    @Test
    public void execute_squareRef_shouldSave() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the first player in a visible square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));

        /* The other player will be discarded */

        /* Execute the target */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);
        target.setSquareRef("sqr");
        target.execute(view);

        /* Check that the shooter has been moved */
        assertThat(weapon.getSavedSquare("sqr"), is(board.getSquare(2,1)));
    }

    /* =============== VISIBILITY ================= */

    @Test
    public void execute_directional_ignoreWalls_multipleTargets_shouldShootBoth() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(3,1));
        PersistentView view = views.get("bzoto");

        /* Set first target behind a wall */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(0,1));

        /* Set second player in the same direction, but not behind a wall */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,1));

        /* Set up the weapon */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);

        /* Set up user's answer */
        when(view.sendChoiceRequest(any(PlayerRequest.class)))
                /* First choice: player behind wall */
                .thenAnswer((SendChoiceRequestAnswer<String>) req ->
                        new CompletableChoice<>(req).complete("loSqualo")
                )
                /* Second choice: player in front of him */
                .thenAnswer((SendChoiceRequestAnswer<String>) req ->
                        new CompletableChoice<>(req).complete("zoniMyLord")
                );

        /* Execute the first target */
        PlayerTarget red = new PlayerTarget("red", weapon, match, factory);
        red.setVisibility(IGNORE_WALLS);
        red.execute(view);

        /* Execute the second target */
        PlayerTarget blue = new PlayerTarget("blue", weapon, match, factory);
        blue.setVisibility(IGNORE_WALLS);
        blue.execute(view);

        /* Check that the targeted players have been saved */
        assertThat(weapon.getSavedPlayer("red"), is(t1));
        assertThat(weapon.getSavedPlayer("blue"), is(t2));
    }

    @Test
    public void execute_cancellation_shouldThrow() throws InterruptedException {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the both players in visible squares */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(2,1));
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(1,1));

        /* Set up user's answer: cancel */
        when(view.sendChoiceRequest(any(PlayerRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Set up target */
        PlayerTarget target = new PlayerTarget("red", weapon, match, factory);

        /* Call the method */
        try {
            target.execute(view);
            fail();
        } catch (CancellationException e) {
            /* That's ok */
        }

        /* Check that no target has been saved */
        assertNull(weapon.getSavedPlayer("red"));
    }
}