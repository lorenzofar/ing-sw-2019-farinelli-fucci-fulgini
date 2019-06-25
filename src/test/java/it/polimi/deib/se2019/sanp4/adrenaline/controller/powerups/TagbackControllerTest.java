package it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.PlayerTurn;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagbackControllerTest {

    private static Match match;
    private static Player currentPlayer;
    private static PlayerTurn currentTurn;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;

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
        view = views.get("bzoto");

        /* Set up the current turn */
        currentPlayer = match.getPlayerByName("loSqualo");
        currentTurn = new PlayerTurn(currentPlayer);
        match.setCurrentTurn(currentTurn);
    }

    @Test
    public void use_notDamaged_shouldReturnFalseAndNotify() {
        /* By default all the players have no damage */
        Player player = match.getPlayerByName("bzoto");

        /* Create the controller */
        TagbackController controller = new TagbackController(match);

        /* Use it */
        assertFalse(controller.use(view));

        /* Check that the user received no requests, but a warning */
        verify(view, never()).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the current player received no marks by the user of the powerup */
        assertEquals(0, currentPlayer.getPlayerBoard().getMarksByPlayer(player));
    }

    @Test
    public void use_damaged_shouldGiveMarkToCurrentPlayer() {
        /* Set the player as damaged */
        Player player = match.getPlayerByName("bzoto");
        player.getPlayerBoard().addDamage(currentPlayer,1);
        currentTurn.addDamagedPlayer(player);

        /* Create the controller */
        TagbackController controller = new TagbackController(match);

        /* Use it */
        assertTrue(controller.use(view));

        /* Check no interaction with the user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the current player received the mark */
        assertEquals(1, currentPlayer.getPlayerBoard().getMarksByPlayer(player));
    }
}