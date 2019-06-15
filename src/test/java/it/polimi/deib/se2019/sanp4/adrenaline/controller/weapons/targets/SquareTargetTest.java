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
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum;
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
public class SquareTargetTest {

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
        weapon = new AbstractWeaponStub(weaponCard, match, factory);
    }

    /* ======== DETERMINE SELECTABLE SQUARES ======== */

    @Test
    public void determineSelectableSquares_defaultConstraints_shouldReturnAllVisibleSquares() {
        Square start = board.getSquare(2,1);

        /* Call the method */
        SquareTarget target = new SquareTarget("red", weapon, match, factory);
        Set<Square> selectableSquares = target.determineSelectableSquares(start);

        /* Check results */
        Set<Square> expected = new HashSet<>();
        expected.add(board.getSquare(1,0));
        expected.add(board.getSquare(2,0));
        expected.add(board.getSquare(1,1));
        expected.add(board.getSquare(2,1));
        expected.add(board.getSquare(3,1));
        expected.add(board.getSquare(3,2));

        assertTrue(selectableSquares.containsAll(expected));
        assertTrue(expected.containsAll(selectableSquares));
    }

    @Test
    public void determineSelectableSquares_directionalWeapon_directionNotSet_shouldReturnInAllDirections() {
        Square start = board.getSquare(1,1);

        /* Make the weapon directional */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);
        /* Do not set direction */

        /* Create target */
        SquareTarget target = new SquareTarget("red", weapon, match, factory);
        target.setVisibility(ANY);

        /* Call the method */
        Set<Square> selectableSquares = target.determineSelectableSquares(start);

        /* Check results */
        Set<Square> expected = new HashSet<>();
        expected.add(board.getSquare(1,1));
        expected.add(board.getSquare(1,0));
        expected.add(board.getSquare(1,2));
        expected.add(board.getSquare(2,1));
        expected.add(board.getSquare(3,1));

        assertTrue(selectableSquares.containsAll(expected));
        assertTrue(expected.containsAll(selectableSquares));
    }

    @Test
    public void determineSelectableSquares_directionalWeapon_directionSet_shouldReturnInSingleDirection() {
        Square start = board.getSquare(1,1);

        /* Make the weapon directional */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);
        weapon.selectCardinalDirection(N);

        /* Create target */
        SquareTarget target = new SquareTarget("red", weapon, match, factory);
        target.setVisibility(ANY);

        /* Call the method */
        Set<Square> selectableSquares = target.determineSelectableSquares(start);

        /* Check results */
        Set<Square> expected = new HashSet<>();
        expected.add(board.getSquare(1,1));
        expected.add(board.getSquare(1,0));

        assertTrue(selectableSquares.containsAll(expected));
        assertTrue(expected.containsAll(selectableSquares));
    }

    @Test
    public void determineSelectableSquares_excludeSquares_shouldBeExcluded() {
        Square start = board.getSquare(2,1);

        /* Add saved squares */
        weapon.saveSquare("1", board.getSquare(2,0)); /* Will be excluded */
        weapon.saveSquare("2", board.getSquare(2,2)); /* Not in query */
        weapon.saveSquare("3", board.getSquare(3,2)); /* Will not be picked */

        /* Create target */
        SquareTarget target = new SquareTarget("red", weapon, match, factory);
        target.setVisibility(VISIBLE);
        target.setExcludeSquares(new HashSet<>(Arrays.asList("1","2")));

        /* Call the method */
        Set<Square> selectableSquares = target.determineSelectableSquares(start);

        /* Check results */
        Set<Square> expected = new HashSet<>();
        expected.add(board.getSquare(1,0));
        expected.add(board.getSquare(1,1));
        expected.add(board.getSquare(2,1));
        expected.add(board.getSquare(3,1));
        expected.add(board.getSquare(3,2));

        assertTrue(selectableSquares.containsAll(expected));
        assertTrue(expected.containsAll(selectableSquares));
    }

    @Test
    public void determineSelectableSquares_visibleFromPlayer_shouldLimitSelection() {
        Square start = board.getSquare(2,1);

        /* Place a player from which the selectable squares must be visible */
        Player other = match.getPlayerByName("loSqualo");
        board.movePlayer(other, board.getSquare(3,2));
        weapon.savePlayer("blue", other);

        /* Create target */
        SquareTarget target = new SquareTarget("red", weapon, match, factory);
        target.setVisibility(VISIBLE);
        target.setVisibleFromPlayer("blue");

        /* Call the method */
        Set<Square> selectableSquares = target.determineSelectableSquares(start);

        /* Check results */
        Set<Square> expected = new HashSet<>();
        expected.add(board.getSquare(3,1));
        expected.add(board.getSquare(3,2));

        assertTrue(selectableSquares.containsAll(expected));
        assertTrue(expected.containsAll(selectableSquares));
    }

    @Test
    public void determineSelectableSquares_visibleFromPlayer_playerNotSaved_shouldSelectNone() {
        Square start = board.getSquare(2,1);

        /* Do not place the player from which the selectable squares must be visible */

        /* Create target */
        SquareTarget target = new SquareTarget("red", weapon, match, factory);
        target.setVisibility(VISIBLE);
        target.setVisibleFromPlayer("blue");

        /* Call the method */
        Set<Square> selectableSquares = target.determineSelectableSquares(start);

        assertTrue(selectableSquares.isEmpty());
    }

    /* ================================= EXECUTE =================================*/

    @Test
    public void execute_alreadySaved_shouldAutoSelect() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Create the square that has to be targeted and put a player in there */
        Square sqr = board.getSquare(1,0);
        Player finalTarget = match.getPlayerByName("loSqualo");
        board.movePlayer(finalTarget, sqr);

        /* Set up the weapon */
        weapon.saveSquare("sqr", sqr); /* Save the square which has to be auto-selected */

        /* Create target with the same id */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);

        /* Call the method */
        target.execute(view);

        /* Check that there has been no interaction with the shooter */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());

        /* Check that the player in that square received damage and marks */
        List<Player> targetDamages = finalTarget.getPlayerBoard().getDamages();
        for (int i = 0; i < 2; i++) {
            assertThat(targetDamages.get(i), is(shooter));
        }
        assertThat(targetDamages.size(), is(2));

        assertThat(finalTarget.getPlayerBoard().getMarksByPlayer(shooter), is(1));

        /* Check that the damaged player has been added to the weapon */
        assertTrue(weapon.getDamagedPlayers().contains(finalTarget));
    }

    @Test
    public void execute_onlyOneSelectable_mandatory_shouldAutoSelect() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Create the square that has to be targeted and put a player in there */
        Square sqr = board.getSquare(1,0);
        Player finalTarget = match.getPlayerByName("loSqualo");
        board.movePlayer(finalTarget, sqr);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);
        target.setMarks(1);

        /* The visibility is VISIBLE; since there is only one visible square with a target in it,
        the selection is automatic */
        target.execute(view);

        /* Check that there has been no interaction with the shooter */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());

        /* Check that the player in that square received damage and marks */
        List<Player> targetDamages = finalTarget.getPlayerBoard().getDamages();
        for (int i = 0; i < 2; i++) {
            assertThat(targetDamages.get(i), is(shooter));
        }
        assertThat(targetDamages.size(), is(2));

        assertThat(finalTarget.getPlayerBoard().getMarksByPlayer(shooter), is(1));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(sqr));
    }

    @Test
    public void execute_onlyOneSelectable_optional_shouldAskPlayer() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Create the square that has to be targeted and put a player in there */
        Square sqr = board.getSquare(1,0);
        Player finalTarget = match.getPlayerByName("loSqualo");
        board.movePlayer(finalTarget, sqr);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setOptional(true);
        target.setDamage(2);
        target.setMarks(1);

        /* Set up user's answer: select no square */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer(new ChooseNoneAnswer<>());

        /* Call method */
        target.execute(view);

        /* Check interaction with player */
        verify(view).sendChoiceRequest(any(SquareRequest.class));

        /* Check that the player in that square received no damage and marks */
        assertThat(finalTarget.getPlayerBoard().getDamageCount(), is(0));
        assertThat(finalTarget.getPlayerBoard().getMarksByPlayer(shooter), is(0));

        /* Check that the target has not been saved */
        assertNull(weapon.getSavedSquare("sqr"));
    }

    @Test
    public void execute_multipleSelectable_shouldAskPlayer() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Set up another targetable square with a player */
        Square s2 = board.getSquare(2,1);
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, s2);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);

        /* Set up user's answer: select the second square */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer((SendChoiceRequestAnswer<CoordPair>) req ->
                new CompletableChoice<>(req).complete(new CoordPair(2,1))
        );

        /* Call method */
        target.execute(view);

        /* Check interaction with player */
        verify(view).sendChoiceRequest(any(SquareRequest.class));

        /* Check that the first player has not been damaged */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(0));

        /* Check that the second player received damage and marks */
        List<Player> targetDamages = t2.getPlayerBoard().getDamages();
        for (int i = 0; i < 2; i++) {
            assertThat(targetDamages.get(i), is(shooter));
        }
        assertThat(targetDamages.size(), is(2));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(s2));
    }

    @Test
    public void execute_chooseBetweenTargets_shouldRestrictChoice() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Set up another targetable square with a player */
        Square s2 = board.getSquare(2,1);
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, s2);

        /* Set up the weapon */
        weapon.saveSquare("s2", s2);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);
        target.setChooseBetweenTargets(new HashSet<>(Collections.singleton("s2")));

        /* The square s2 is the only one targetable, hence it will automatically be selected */

        /* Call method */
        target.execute(view);

        /* Check interaction with player */
        verify(view, never()).sendChoiceRequest(any(SquareRequest.class));

        /* Check that the first player has not been damaged */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));
        assertThat(t1.getPlayerBoard().getMarksByPlayer(shooter), is(0));

        /* Check that the second player received damage and marks */
        List<Player> targetDamages = t2.getPlayerBoard().getDamages();
        for (int i = 0; i < 2; i++) {
            assertThat(targetDamages.get(i), is(shooter));
        }
        assertThat(targetDamages.size(), is(2));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(s2));
    }

    @Test
    public void execute_directionalWeapon_directionNotSet_shouldSetAfterShooting() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Set up the weapon */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);

        /* The square s1 is the only one targetable, hence it will automatically be selected */

        /* Call method */
        target.execute(view);

        /* Check that the direction has been selected */
        assertThat(weapon.getSelectedDirection(), is(N));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(s1));
    }

    @Test
    public void execute_directionalWeapon_directionAlreadySet_shouldOnlySelectInThatDirection() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square at North with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Set up another targetable square at East with a player */
        Square s2 = board.getSquare(2,1);
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, s2);

        /* Set up the weapon */
        weapon.setShootingDirection(ShootingDirectionEnum.CARDINAL);
        weapon.selectCardinalDirection(N);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);

        /* The square s1 is the only one targetable, hence it will automatically be selected */

        /* Call method */
        target.execute(view);

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(s1));
    }

    @Test
    public void execute_targetsOwnSquare_shooterShouldNotReceiveDamage() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        Square s = board.getSquare(1,1);
        board.movePlayer(shooter, s);
        PersistentView view = views.get("bzoto");

        /* Set up two targetable players in the same square */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s);
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, s);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(1);
        target.setMarks(2);

        /* The square s is the only one targetable, hence it will automatically be selected */

        /* Call method */
        target.execute(view);

        /* Check that both the players received the damage */
        for (Player p : new Player[]{t1, t2}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(1));
            assertThat(p.getPlayerBoard().getMarksByPlayer(shooter), is(2));
        }

        /* Check that the shooter got no marks or damages */
        assertThat(shooter.getPlayerBoard().getDamageCount(), is(0));
        assertThat(shooter.getPlayerBoard().getMarksByPlayer(shooter), is(0));
    }

    @Test
    public void execute_moveShooterHere_shouldBeMoved() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setMoveShooterHere(true);

        /* The square s1 is the only one targetable, hence it will automatically be selected */

        /* Call method */
        target.execute(view);

        /* Check that the shooter has been moved */
        assertThat(shooter.getCurrentSquare(), is(s1));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(s1));
    }

    @Test
    public void execute_squareRef_shouldBeSaved() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Create a target */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setSquareRef("ref");

        /* The square s1 is the only one targetable, hence it will automatically be selected */

        /* Call method */
        target.execute(view);

        /* Check that the additional reference has been saved */
        assertThat(weapon.getSavedSquare("ref"), is(s1));

        /* Check that the target has been saved */
        assertThat(weapon.getSavedSquare("sqr"), is(s1));
    }

    @Test
    public void execute_noneSelectable_shouldReturnFalse() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Exclude that square from being targetable */
        weapon.saveSquare("s1", s1);
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setDamage(2);
        target.setExcludeSquares(new HashSet<>(Collections.singleton("s1")));

        /* Call the method */
        assertFalse(target.execute(view));

        /* Check that the target has not been saved */
        assertNull(weapon.getSavedSquare("sqr"));
    }

    @Test
    public void execute_userCancelsSelection_shouldThrow() throws InterruptedException {
        /* Set the player in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set up a targetable square with a player */
        Square s1 = board.getSquare(1,0);
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, s1);

        /* Make the target optional to trigger the request */
        SquareTarget target = new SquareTarget("sqr", weapon, match, factory);
        target.setOptional(true);
        target.setDamage(2);

        /* Set up user's answer */
        when(view.sendChoiceRequest(any(SquareRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Call the method */
        try {
            target.execute(view);
            fail();
        } catch (CancellationException e) {
            /* That's ok */
        }

        /* Check that t1 has not been damaged */
        assertThat(t1.getPlayerBoard().getDamageCount(), is(0));

        /* Check that the target has not been registered */
        assertNull(weapon.getSavedSquare("sqr"));
    }
}