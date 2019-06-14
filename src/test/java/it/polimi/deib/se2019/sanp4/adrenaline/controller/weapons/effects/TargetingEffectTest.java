package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.AbstractTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TargetingEffectTest {

    private static Match match;
    private static Board board;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static ControllerFactory factory;

    @BeforeClass
    public static void classSetup() {
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
        view = views.get("bzoto");

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);
        when(factory.createPaymentHandler()).thenReturn(new PaymentHandler(match));
    }

    public List<AbstractTarget> generateMockTargets(int count) {
        return Stream.generate(() -> mock(AbstractTarget.class)).limit(count).collect(Collectors.toList());
    }

    /* ========================= ADD TARGET ============================== */

    @Test
    public void appendTarget_shouldAddInOrder() {
        /* Create a new effect */
        TargetingEffect effect = new TargetingEffect("effect", match, factory);

        /* Check that the list is initially empty */
        assertTrue(effect.getTargets().isEmpty());

        /* Create a list of stub targets */
        List<AbstractTarget> expected = generateMockTargets(3);

        /* Add them to the effect in order */
        for (AbstractTarget target : expected) {
            effect.appendTarget(target);
        }

        /* Check that the've been added in the correct order */
        List<AbstractTarget> actual = effect.getTargets();
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    /* ======================= USE ======================================= */

    @Test
    public void use_cannotPayCost_shouldNotBeAbleToUse() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up the effect */
        TargetingEffect effect = new TargetingEffect("effect", match, factory);
        effect.setCost(Arrays.asList(AmmoCubeCost.RED, AmmoCubeCost.RED));
        AbstractTarget target = mock(AbstractTarget.class);
        effect.appendTarget(target);

        assertFalse(effect.use(view));

        /* Check that the user has been notified */
        verify(view, never()).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the target has not been executed */
        verify(target, never()).execute(any());

        /* Check that the user still has his ammo */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(1, (int) count));
    }

    @Test
    public void use_canPayCost_targetsSucceed_shouldCompleteSuccessfully() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up the effect */
        TargetingEffect effect = new TargetingEffect("effect", match, factory);
        effect.setCost(Arrays.asList(AmmoCubeCost.RED, AmmoCubeCost.YELLOW, AmmoCubeCost.BLUE));
        AbstractTarget target = mock(AbstractTarget.class);
        when(target.execute(view)).thenReturn(true);
        effect.appendTarget(target);

        assertTrue(effect.use(view));

        /* Check that the user has not been notified */
        verify(view, never()).showMessage(anyString(), any());

        /* Check that the target has been executed */
        verify(target).execute(view);

        /* Check that the user paid the cost */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(0, (int) count));
    }

    @Test
    public void use_multipleTargets_shouldExecuteInOrder() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up some targets */
        List<AbstractTarget> targets = generateMockTargets(3);
        for (AbstractTarget target : targets) {
            when(target.execute(view)).thenReturn(true);
        }

        /* Set up the effect */
        TargetingEffect effect = new TargetingEffect("effect", match, factory);
        targets.forEach(effect::appendTarget);

        /* Executes successfully */
        assertTrue(effect.use(view));

        /* Check that the targets have been executed in order */
        InOrder inOrder = inOrder(targets.toArray());
        for (AbstractTarget target : targets) {
            inOrder.verify(target).execute(view);
        }
    }

    @Test
    public void use_mandatoryTargetFails_shouldNotExecuteNextOnes() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up some targets */
        List<AbstractTarget> targets = generateMockTargets(3);
        for (int i = 0; i < targets.size(); i++) {
            AbstractTarget target = targets.get(i);

            when(target.isOptional()).thenReturn(false); /* All mandatory */
            when(target.execute(view)).thenReturn(i != 1); /* The second one will fail */
        }

        /* Set up the effect */
        TargetingEffect effect = new TargetingEffect("effect", match, factory);
        targets.forEach(effect::appendTarget);

        /* Executes the first target, then fails during the second one */
        assertFalse(effect.use(view));

        /* Check the execution order */
        InOrder inOrder = inOrder(targets.toArray());
        inOrder.verify(targets.get(0)).execute(view); /* First one executed */
        inOrder.verify(targets.get(1)).execute(view); /* Second one failed */
        inOrder.verify(targets.get(2), never()).execute(view); /* Third one not executed */
    }

    @Test
    public void use_optionalTargetFails_shouldExecuteNextOnes() throws InterruptedException {
        /* Set up the player */
        Player player = match.getPlayerByName("bzoto"); /* He has one ammo for each color */
        board.movePlayer(player, board.getSquare(0, 0));

        /* Set up some targets */
        List<AbstractTarget> targets = generateMockTargets(3);
        for (int i = 0; i < targets.size(); i++) {
            AbstractTarget target = targets.get(i);

            when(target.isOptional()).thenReturn(i != 0); /* Only first is mandatory */
            when(target.execute(view)).thenReturn(i != 1); /* The second one will fail */
        }

        /* Set up the effect */
        TargetingEffect effect = new TargetingEffect("effect", match, factory);
        targets.forEach(effect::appendTarget);

        /* Executes the first target, then fails during the second one but it's optional */
        assertTrue(effect.use(view));

        /* Check the execution order */
        InOrder inOrder = inOrder(targets.toArray());
        inOrder.verify(targets.get(0)).execute(view); /* First one executed */
        inOrder.verify(targets.get(1)).execute(view); /* Second one failed */
        inOrder.verify(targets.get(2)).execute(view); /* Third one executed */
    }
}