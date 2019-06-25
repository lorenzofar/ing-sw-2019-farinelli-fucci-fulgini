package it.polimi.deib.se2019.sanp4.adrenaline.controller.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ActionRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PlayerOperationRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.*;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.GrabActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.MoveActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.ReloadActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.action.ShootActionController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups.PowerupController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.concurrent.CancellationException;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerOperationEnum.*;
import static it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurnState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TurnControllerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;

    @Mock
    private static ControllerFactory factory;
    @Mock
    private static SpawnController spawnController;
    @Mock
    private static MoveActionController moveActionController;
    @Mock
    private static GrabActionController grabActionController;
    @Mock
    private static ShootActionController shootActionController;
    @Mock
    private static ReloadActionController reloadActionController;
    @Mock
    private static PowerupController powerupController;

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

        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n,v);
        });

        /* Create a stub of the controller factory (only stub relevant methods) */
        when(factory.createSpawnController()).thenReturn(spawnController);
        when(factory.createMoveActionController()).thenReturn(moveActionController);
        when(factory.createGrabActionController(any())).thenReturn(grabActionController);
        when(factory.createShootActionController()).thenReturn(shootActionController);
        when(factory.createReloadActionController()).thenReturn(reloadActionController);
        when(factory.createPowerupController(any())).thenReturn(powerupController);
    }

    private static SendChoiceRequestAnswer<PlayerOperationRequest> chooseOperation(PlayerOperationEnum choice) {
        return req -> new CompletableChoice<>(req).complete(choice);
    }

    private static ActionCard actionCardWithFinalAction() {
        return new ActionCard(2, ActionCardEnum.REGULAR, Collections.singletonList(RUN), RELOAD);
    }

    private static ActionCard actionCardWithNoFinalAction() {
        return new ActionCard(2, ActionCardEnum.FRENZY1, Collections.singletonList(RUN), null);
    }

    @Test
    public void runTurn_playerIsNotSpawned_shouldAskToSpawn() throws InterruptedException {
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Setup the mock of the user */
        /* When it gets the request to select the operation, just end the turn */
        doAnswer((SendChoiceRequestAnswer<PlayerOperationEnum>) req ->
                new CompletableChoice<>(req).complete(END_TURN)
        ).when(view).sendChoiceRequest(any(PlayerOperationRequest.class));

        /* Run the turn */
        controller.runTurn();

        /* Check that the spawn controller has been asked to spawn the player */
        verify(spawnController).initialSpawn(view);

        /* Check that the turn is over */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_playerIsSpawned_shouldNotAskToSpawn() throws InterruptedException {
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");
        assertEquals("bzoto", view.getUsername());

        /* Spawn the player somewhere */
        Square s = match.getBoard().getSquare(0,0);
        match.getBoard().movePlayer(p,s);

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Setup the mock of the user */
        /* When it gets the request to select the operation, just end the turn */
        doAnswer((SendChoiceRequestAnswer<PlayerOperationEnum>) req ->
                new CompletableChoice<>(req).complete(END_TURN)
        ).when(view).sendChoiceRequest(any(PlayerOperationRequest.class));

        /* Run the turn */
        controller.runTurn();

        /* Check that the spawn controller has been asked to spawn the player */
        verify(spawnController, never()).initialSpawn(any());

        /* Check that the turn is over */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_canceledRequestDuringTurn_shouldEndNormally() throws InterruptedException {
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set the view to cancel the request */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Run the turn and check that it ends with no exceptions */
        controller.runTurn();

        assertThat(turn.getTurnState(), is(OVER));
    }

    /* ======= PERFORM ACTION ======= */

    @Test
    public void runTurn_action_regularActionCard_useAllActions_shouldComplete() throws InterruptedException {
        List<ActionEnum> mainActions = Arrays.asList(RUN, GRAB, SHOOT);
        ActionEnum finalAction = RELOAD;
        ActionCard regular = new ActionCard(3, ActionCardEnum.REGULAR, mainActions, finalAction);

        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        p.setActionCard(regular);
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(PERFORM_ACTION)); /* Always perform actions */
        /* NOTE: Actions are not really executed, but only the controllers are called */
        List<ActionRequest> capturedRequests = new ArrayList<>();
        when(view.sendChoiceRequest(any(ActionRequest.class)))
                /* First execute shoot */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(SHOOT);
                })
                /* Then grab something */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(GRAB);
                })
                /* Run somewhere */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(RUN);
                })
                /* And reload in the end */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(RELOAD);
                });

        /* Run the turn */
        controller.runTurn();

        /* Check the number of interactions with the user */
        verify(view, times(4)).sendChoiceRequest(any(PlayerOperationRequest.class));
        verify(view, times(4)).sendChoiceRequest(any(ActionRequest.class));

        /* Check that the expected choices have been proposed */
        for (ActionRequest req : capturedRequests.subList(0,2)) {
            /* Check choices for the first three requests */
            List<ActionEnum> choices = req.getChoices();
            assertEquals(4, choices.size());
            assertTrue(choices.containsAll(mainActions));
            assertTrue(choices.contains(finalAction));
        }
        /* Check the last request */
        assertEquals(1, capturedRequests.get(3).getChoices().size());
        assertTrue(capturedRequests.get(2).getChoices().contains(RELOAD)); /* Only final action */

        /* Check that the action controllers have been called in the right order */
        InOrder inOrder = Mockito.inOrder(moveActionController, grabActionController,
                reloadActionController, shootActionController);
        inOrder.verify(shootActionController).execute(view);
        inOrder.verify(moveActionController).execute(view, 1);
        inOrder.verify(grabActionController).execute();
        inOrder.verify(moveActionController).execute(view, 3);
        inOrder.verify(reloadActionController).execute(view);

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_action_adrenalineActionCard_doNotReload_shouldComplete() throws InterruptedException {
        List<ActionEnum> mainActions = Arrays.asList(RUN, ADRENALINE_GRAB, ADRENALINE_SHOOT);
        ActionEnum finalAction = RELOAD;
        ActionCard regular = new ActionCard(2, ActionCardEnum.FRENZY2, mainActions, finalAction);

        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        p.setActionCard(regular);
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(PERFORM_ACTION)); /* Always perform actions */
        /* NOTE: Actions are not really executed, but only the controllers are called */
        List<ActionRequest> capturedRequests = new ArrayList<>();
        when(view.sendChoiceRequest(any(ActionRequest.class)))
                /* First execute grab */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(ADRENALINE_GRAB);
                })
                /* Then shoot */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(ADRENALINE_SHOOT);
                })
                /* And choose not to reload */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(null);
                });

        /* Run the turn */
        controller.runTurn();

        /* Check the number of interactions with the user */
        verify(view, times(3)).sendChoiceRequest(any(PlayerOperationRequest.class));
        verify(view, times(3)).sendChoiceRequest(any(ActionRequest.class));

        /* Check that the expected choices have been proposed */
        for (ActionRequest req : capturedRequests.subList(0,1)) {
            /* Check choices for the first two requests */
            List<ActionEnum> choices = req.getChoices();
            assertEquals(4, choices.size());
            assertTrue(choices.containsAll(mainActions));
            assertTrue(choices.contains(finalAction));
        }
        /* Check the last request */
        assertEquals(1, capturedRequests.get(2).getChoices().size());
        assertTrue(capturedRequests.get(2).getChoices().contains(RELOAD)); /* Only final action */

        /* Check that the action controllers have been called in the right order */
        InOrder inOrder = Mockito.inOrder(moveActionController, grabActionController,
                reloadActionController, shootActionController);
        inOrder.verify(moveActionController).execute(view, 2);
        inOrder.verify(grabActionController).execute();
        inOrder.verify(moveActionController).execute(view, 1);
        inOrder.verify(shootActionController).execute(view);
        inOrder.verify(reloadActionController, never()).execute(view);

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_action_frenzy1Actions_useAll_shouldComplete() throws InterruptedException {
        List<ActionEnum> mainActions = Arrays.asList(FRENZY1_GRAB, FRENZY1_SHOOT);
        ActionCard regular = new ActionCard(2, ActionCardEnum.FRENZY2, mainActions, null);

        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        p.setActionCard(regular);
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(PERFORM_ACTION)); /* Always perform actions */
        /* NOTE: Actions are not really executed, but only the controllers are called */
        List<ActionRequest> capturedRequests = new ArrayList<>();
        when(view.sendChoiceRequest(any(ActionRequest.class)))
                /* Execute both actions */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(FRENZY1_SHOOT);
                })
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(FRENZY1_GRAB);
                });

        /* Run the turn */
        controller.runTurn();

        /* Check the number of interactions with the user */
        verify(view, times(2)).sendChoiceRequest(any(PlayerOperationRequest.class));
        verify(view, times(2)).sendChoiceRequest(any(ActionRequest.class));

        /* Check that the expected choices have been proposed */
        for (ActionRequest req : capturedRequests.subList(0,1)) {
            /* Check choices for the two requests */
            List<ActionEnum> choices = req.getChoices();
            assertEquals(2, choices.size());
            assertTrue(choices.containsAll(mainActions));
        }

        /* Check that the action controllers have been called in the right order */
        InOrder inOrder = Mockito.inOrder(moveActionController, grabActionController,
                reloadActionController, shootActionController);
        inOrder.verify(moveActionController).execute(view, 2);
        inOrder.verify(shootActionController).execute(view);
        inOrder.verify(moveActionController).execute(view, 3);
        inOrder.verify(grabActionController).execute();
        verify(reloadActionController, never()).execute(view);

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_action_frenzy2Actions_sameActionTwice_shouldComplete() throws InterruptedException {
        List<ActionEnum> mainActions = Arrays.asList(FRENZY2_GRAB, FRENZY2_RUN, FRENZY2_SHOOT);
        ActionCard regular = new ActionCard(3, ActionCardEnum.FRENZY2, mainActions, null);

        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        p.setActionCard(regular);
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(PERFORM_ACTION)); /* Always perform actions */
        /* NOTE: Actions are not really executed, but only the controllers are called */
        List<ActionRequest> capturedRequests = new ArrayList<>();
        when(view.sendChoiceRequest(any(ActionRequest.class)))
                /* Execute shoot twice, then run */
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(FRENZY2_SHOOT);
                })
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(FRENZY2_SHOOT);
                })
                .thenAnswer((SendChoiceRequestAnswer<ActionEnum>) req -> {
                    capturedRequests.add((ActionRequest) req);
                    return new CompletableChoice<>(req).complete(FRENZY2_RUN);
                });

        /* Run the turn */
        controller.runTurn();

        /* Check the number of interactions with the user */
        verify(view, times(3)).sendChoiceRequest(any(PlayerOperationRequest.class));
        verify(view, times(3)).sendChoiceRequest(any(ActionRequest.class));

        /* Check that the expected choices have been proposed */
        for (ActionRequest req : capturedRequests.subList(0,1)) {
            /* Check choices for the two requests */
            List<ActionEnum> choices = req.getChoices();
            assertEquals(3, choices.size());
            assertTrue(choices.containsAll(mainActions));
        }

        /* Check that the action controllers have been called in the right order */
        InOrder inOrder = Mockito.inOrder(moveActionController, grabActionController,
                reloadActionController, shootActionController);
        inOrder.verify(moveActionController).execute(view, 1);
        inOrder.verify(reloadActionController).execute(view);
        inOrder.verify(shootActionController).execute(view);
        inOrder.verify(moveActionController).execute(view, 1);
        inOrder.verify(reloadActionController).execute(view);
        inOrder.verify(shootActionController).execute(view);
        inOrder.verify(moveActionController).execute(view, 4);

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    /* ======= POWERUPS ======= */

    @Test
    public void runTurn_powerup_noPowerups_shouldNotifyPlayer() throws InterruptedException {
        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(USE_POWERUP))
                .thenAnswer(chooseOperation(END_TURN));

        /* The player has no powerups */
        controller.runTurn();

        /* Check that he got no powerup requests, but only a warning */
        verify(view, never()).sendChoiceRequest(any(PowerupCardRequest.class));
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the factory has not been asked to create a powerup controller */
        verify(factory, never()).createPowerupController(any());

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_powerup_usedSuccessfully_shouldRemoveFromPlayer() throws Exception {
        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Add two distinct powerups */
        PowerupCard pw1 = new PowerupCard(PowerupEnum.NEWTON, AmmoCube.RED);
        PowerupCard pw2 = new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.BLUE);
        p.addPowerup(pw1);
        p.addPowerup(pw2);

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(USE_POWERUP))
                .thenAnswer(chooseOperation(END_TURN));
        List<PowerupCard> givenPowerups = new ArrayList<>(2);
        when(view.sendChoiceRequest(any(PowerupCardRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<PowerupCard>) req -> {
                    givenPowerups.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(pw1);
                });

        /* Set up the mock of the powerup controller */
        when(powerupController.use(view)).thenReturn(true);

        /* Run */
        controller.runTurn();

        /* Check that he got one powerup request and no warning */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the choices were as expected */
        assertEquals(2, givenPowerups.size());
        assertTrue(givenPowerups.contains(pw1));
        assertTrue(givenPowerups.contains(pw2));

        /* Check that the powerup controller has been used */
        verify(factory).createPowerupController(pw1.getType());
        verify(powerupController).use(view);

        /* Check that the player hasn't got the powerup anymore */
        assertFalse(p.getPowerups().contains(pw1));
        assertTrue(p.getPowerups().contains(pw2));

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_powerup_failedToUse_shouldNotRemoveFromPlayer() throws Exception {
        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Add two distinct powerups */
        PowerupCard pw1 = new PowerupCard(PowerupEnum.NEWTON, AmmoCube.RED);
        PowerupCard pw2 = new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.BLUE);
        PowerupCard pw3 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.BLUE);
        p.addPowerup(pw1);
        p.addPowerup(pw2);
        p.addPowerup(pw3);

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(USE_POWERUP))
                .thenAnswer(chooseOperation(END_TURN));
        List<PowerupCard> givenPowerups = new ArrayList<>(2);
        when(view.sendChoiceRequest(any(PowerupCardRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<PowerupCard>) req -> {
                    givenPowerups.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(pw1);
                });

        /* Set up the mock of the powerup controller */
        when(powerupController.use(view)).thenReturn(false);

        /* Run */
        controller.runTurn();

        /* Check that he got one powerup request and no warning */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the choices were as expected */
        assertEquals(2, givenPowerups.size());
        assertTrue(givenPowerups.contains(pw1));
        assertTrue(givenPowerups.contains(pw2));

        /* Check that the powerup controller has been used */
        verify(factory).createPowerupController(pw1.getType());
        verify(powerupController).use(view);

        /* Check that the player still has both powerups */
        assertTrue(p.getPowerups().contains(pw1));
        assertTrue(p.getPowerups().contains(pw2));

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    @Test
    public void runTurn_powerup_cancelDuringUsage_shouldNotRemoveFromPlayer() throws Exception {
        /* Set up the player */
        Player p = match.getPlayerByName("bzoto");
        PersistentView view = views.get("bzoto");

        /* Add two distinct powerups */
        PowerupCard pw1 = new PowerupCard(PowerupEnum.NEWTON, AmmoCube.RED);
        PowerupCard pw2 = new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.BLUE);
        p.addPowerup(pw1);
        p.addPowerup(pw2);

        /* Create the turn controller */
        PlayerTurn turn = new PlayerTurn(p);
        match.setCurrentTurn(turn);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Set up the user's responses */
        when(view.sendChoiceRequest(any(PlayerOperationRequest.class)))
                .thenAnswer(chooseOperation(USE_POWERUP));
        List<PowerupCard> givenPowerups = new ArrayList<>(2);
        when(view.sendChoiceRequest(any(PowerupCardRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<PowerupCard>) req -> {
                    givenPowerups.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(pw1);
                });

        /* Set up the mock of the powerup controller */
        when(powerupController.use(view)).thenThrow(new CancellationException());

        /* Run */
        controller.runTurn();

        /* Check that he got one powerup request and no warning */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the choices were as expected */
        assertEquals(2, givenPowerups.size());
        assertTrue(givenPowerups.contains(pw1));
        assertTrue(givenPowerups.contains(pw2));

        /* Check that the powerup controller has been used */
        verify(factory).createPowerupController(pw1.getType());
        verify(powerupController).use(view);

        /* Check that the player still has both powerups */
        assertTrue(p.getPowerups().contains(pw1));
        assertTrue(p.getPowerups().contains(pw2));

        /* Check that the turn is OVER */
        assertThat(turn.getTurnState(), is(OVER));
    }

    /* ======= UPDATE TURN STATE AFTER ACTION ======= */

    @Test
    public void updateTurnStateAfterAction_mainActionExecuted_remainingActions_shouldSetSelecting() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RUN);

        assertThat(turn.getTurnState(), is(SELECTING));
        assertThat(turn.getRemainingActions(), is(1));
    }

    @Test
    public void updateTurnStateAfterAction_mainActionExecuted_noRemainingActions_hasFinalAction_shouldSetSelecting() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        turn.setRemainingActions(1);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RUN);

        assertThat(turn.getTurnState(), is(SELECTING));
        assertThat(turn.getRemainingActions(), is(0));
    }

    @Test
    public void updateTurnStateAfterAction_mainActionExecuted_noRemainingActions_hasNoFinalAction_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithNoFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        turn.setRemainingActions(1);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RUN);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0));
    }

    @Test
    public void updateTurnStateAfterAction_noActionExecuted_remainingActions_shouldSetSelecting() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithNoFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(SELECTING);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(null);

        assertThat(turn.getTurnState(), is(SELECTING));
        assertThat(turn.getRemainingActions(), is(2)); /* The remaining actions are untouched */
    }

    @Test
    public void updateTurnStateAfterAction_noActionExecuted_noRemainingActions_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithNoFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(SELECTING);
        turn.setRemainingActions(0);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(null);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0)); /* The remaining actions are untouched */
    }

    @Test
    public void updateTurnStateAfterAction_finalActionExecuted_remainingActions_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RELOAD);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0)); /* The remaining actions are untouched */
    }

    @Test
    public void updateTurnStateAfterAction_finalActionExecuted_noRemainingActions_shouldEndTurn() {
        Player p = match.getPlayerByName("bzoto");

        /* Set a proper action card (max 2 actions) */
        p.setActionCard(actionCardWithFinalAction());

        /* Prepare the turn */
        PlayerTurn turn = new PlayerTurn(p);
        turn.setTurnState(BUSY);
        turn.setRemainingActions(0);
        TurnController controller = new TurnController(turn, match, views, factory);

        /* Then test the method */
        controller.updateTurnStateAfterAction(RELOAD);

        assertThat(turn.getTurnState(), is(OVER));
        assertThat(turn.getRemainingActions(), is(0)); /* The remaining actions are untouched */
    }
}