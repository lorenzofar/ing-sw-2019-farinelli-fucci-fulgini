package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.CancelRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.ChooseNoneAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CancellationException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GrabActionControllerWeaponTest {

    private static Match match;
    private static String name = "bzoto";
    private static Player player;
    private static SpawnSquare spawnSquare;

    @Mock
    private static PersistentView view;
    @Mock
    private static ControllerFactory factory;

    @BeforeClass
    public static void classSetup() throws NotEnoughAmmoException {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        MatchConfiguration validConfig = new MatchConfiguration(0, 5);

        /* Create a match */
        match = MatchCreator.createMatch(Collections.singleton(name), validConfig);

        /* Save some static variables */
        player = match.getPlayerByName(name);
        Board board = match.getBoard();
        spawnSquare = (SpawnSquare) match.getBoard().getSquare(2,0); /* Blue spawn */

        /* Move the player to the spawn square */
        board.movePlayer(player, spawnSquare);

        /* Remove initial ammo from the player */
        player.payAmmo(player.getAmmo());
    }

    @Before
    public void setUp() {
        /* The view of the player must respond with his name */
        when(view.getUsername()).thenReturn(name);
        /* Return the payment handler when needed */
        when(factory.createPaymentHandler()).thenReturn(new PaymentHandler(match));
    }

    @After
    public void tearDown() throws Exception {
        /* Revert the changes */
        player.payAmmo(player.getAmmo()); /* Put back the ammo */

        /* Put back the powerups */
        player.getPowerups().forEach(p -> {
            match.getPowerupStack().discard(p);
            player.removePowerup(p);
        });

        /* Remove any weapon from the square */
        for (WeaponCard w : spawnSquare.getWeaponCards()) {
            spawnSquare.grabWeaponCard(w.getId());
        }

        /* Remove any weapon from the player */
        for (WeaponCard w : player.getWeapons()) {
            player.removeWeapon(w);
        }
    }

    @Test
    public void execute_noWeapons_shouldNotifyPlayer() throws Exception {
        /* The square is empty */

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the user has been notified */
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the user has not been asked to perform any choice */
        verify(view, never()).performRequest(any());

        /* Check that he still has no cards */
        assertTrue(player.getWeapons().isEmpty());
    }

    @Test
    public void execute_playerSelectsNone_shouldEnd() throws Exception {
        /* Put a card in the square */
        WeaponCard weaponCard = WeaponCreator.createWeaponCard("cyberblade");
        spawnSquare.insertWeaponCard(weaponCard);

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class))).thenAnswer(new ChooseNoneAnswer<>());

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the user didn't get any cards */
        assertTrue(player.getWeapons().isEmpty());
    }

    @Test
    public void execute_multipleWeapons_playerCantPayAnyCard_shouldGetNoCard() throws Exception {
        /* Put some cards in the square */
        WeaponCard cyberblade = WeaponCreator.createWeaponCard("cyberblade"); /* Load cost: RED */
        WeaponCard furnace = WeaponCreator.createWeaponCard("furnace"); /* Load cost: NONE */
        spawnSquare.insertWeaponCard(cyberblade);
        spawnSquare.insertWeaponCard(furnace);

        /* The player has no ammo or powerups */

        /* Set up user's answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                /* First answer: cyberblade -> cannot pay */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(cyberblade)
                )
                /* Second answer: furnace -> cannot pay */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(furnace)
                );

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the user didn't get any cards */
        assertTrue(player.getWeapons().isEmpty());

        /* Check that there have been exactly two interactions with the user */
        verify(view, times(2)).sendChoiceRequest(any());
    }

    @Test
    public void execute_multipleWeapons_playerCantPayFirstCard_canPaySecondCard_shouldGetSecondCard() throws Exception {
        /* Put some cards in the square */
        WeaponCard cyberblade = WeaponCreator.createWeaponCard("cyberblade"); /* Load cost: RED */
        WeaponCard furnace = WeaponCreator.createWeaponCard("furnace"); /* Load cost: BLUE */
        spawnSquare.insertWeaponCard(cyberblade);
        spawnSquare.insertWeaponCard(furnace);

        /* The player has no ammo or powerups */
        player.addAmmo(AmmoCube.BLUE);

        /* Set up his answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                /* First answer: cyberblade -> cannot pay */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(cyberblade)
                )
                /* Second answer: furnace -> can pay (no cost) */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(furnace)
                );

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player got the card */
        Optional<WeaponCard> card = player.getWeapons().stream().filter(w -> w.getId().equals("furnace")).findFirst();
        assertTrue(card.isPresent());
        assertTrue(card.get().isUsable());

        /* Check that he paid the load cost */
        assertEquals(0, (int) player.getAmmo().getOrDefault(AmmoCube.BLUE, 0));

        /* The card is not in the square anymore */
        assertFalse(spawnSquare.getWeaponCards().contains(furnace));
        assertFalse(spawnSquare.isFull());

        /* Check that there have been exactly two interactions with the user */
        verify(view, times(2)).sendChoiceRequest(any());
    }

    @Test
    public void execute_pickRequestCanceled_shouldGetNoWeapon() throws Exception {
        /* Put a card in the square */
        WeaponCard weaponCard = WeaponCreator.createWeaponCard("cyberblade");
        spawnSquare.insertWeaponCard(weaponCard);

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class))).thenAnswer(new CancelRequestAnswer());

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        try {
            controller.execute();
            fail();
        } catch (CancellationException e) {
            /* The exception has been correctly thrown */
        }

        /* Check that the user didn't get any cards */
        assertTrue(player.getWeapons().isEmpty());
    }

    @Test
    public void execute_playerHasMaxWeapons_shouldAskToDiscard() throws Exception {
        /* Put some cards in the square */
        WeaponCard cyberblade = WeaponCreator.createWeaponCard("cyberblade"); /* Load cost: RED */
        WeaponCard furnace = WeaponCreator.createWeaponCard("furnace"); /* Load cost: BLUE */
        WeaponCard electroscythe = WeaponCreator.createWeaponCard("electroscythe"); /* Load cost: NONE */
        spawnSquare.insertWeaponCard(cyberblade);
        spawnSquare.insertWeaponCard(furnace);
        spawnSquare.insertWeaponCard(electroscythe);

        /* The player has ammo to pay for Furnace */
        player.addAmmo(AmmoCube.BLUE);

        /* But he already has other cards */
        WeaponCard gl = WeaponCreator.createWeaponCard("grenade_launcher");
        WeaponCard hs = WeaponCreator.createWeaponCard("heatseeker");
        WeaponCard lr = WeaponCreator.createWeaponCard("lock_rifle");
        player.addWeapon(gl);
        player.addWeapon(hs);
        player.addWeapon(lr);

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                /* First answer: pick furnace */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(furnace)
                )
                /* Second answer: discard heatseeker */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(hs)
                );

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that the player got the card */
        Optional<WeaponCard> card = player.getWeapons().stream().filter(w -> w.getId().equals("furnace")).findFirst();
        assertTrue(card.isPresent());
        assertTrue(card.get().isUsable());

        /* Check that the spawn square has the discarded weapon in the correct state */
        Optional<WeaponCard> discarded = spawnSquare.getWeaponCards().stream()
                .filter(w -> w.getId().equals("heatseeker")).findFirst();
        assertTrue(discarded.isPresent());
        assertFalse(discarded.get().isUsable());

        /* Check that the user paid his ammo */
        assertEquals(0, (int) player.getAmmo().getOrDefault(AmmoCube.BLUE, 0));
    }

    @Test
    public void execute_discardRequestCancelled_shouldKeepCurrentCards() throws Exception {
        /* Put some cards in the square */
        WeaponCard cyberblade = WeaponCreator.createWeaponCard("cyberblade"); /* Load cost: RED */
        WeaponCard furnace = WeaponCreator.createWeaponCard("furnace"); /* Load cost: BLUE */
        WeaponCard electroscythe = WeaponCreator.createWeaponCard("electroscythe"); /* Load cost: NONE */
        spawnSquare.insertWeaponCard(cyberblade);
        spawnSquare.insertWeaponCard(furnace);
        spawnSquare.insertWeaponCard(electroscythe);

        /* The player has ammo to pay for Furnace */
        player.addAmmo(AmmoCube.BLUE);

        /* But he already has other cards */
        WeaponCard gl = WeaponCreator.createWeaponCard("grenade_launcher");
        WeaponCard hs = WeaponCreator.createWeaponCard("heatseeker");
        WeaponCard lr = WeaponCreator.createWeaponCard("lock_rifle");
        player.addWeapon(gl);
        player.addWeapon(hs);
        player.addWeapon(lr);

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                /* First answer: pick furnace */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(furnace)
                )
                /* Second answer: cancel the discard request */
                .thenAnswer(new CancelRequestAnswer());

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        try {
            controller.execute();
            fail();
        } catch (CancellationException e) {
            /* The exception has been correctly thrown */
        }

        /* Check that the player still has his weapons */
        assertTrue(player.getWeapons().containsAll(Arrays.asList(gl, hs, lr)));

        /* Check that the spawn square still has its weapons */
        assertTrue(spawnSquare.getWeaponCards().containsAll(Arrays.asList(cyberblade, furnace, electroscythe)));

        /* Check that the user still has his ammo */
        assertEquals(1, (int) player.getAmmo().getOrDefault(AmmoCube.BLUE, 0));
    }

    @Test
    public void execute_cardWithNoAdditionalCost_shouldBeAdded() throws Exception {
        /* Put a card in the square */
        WeaponCard electroscythe = WeaponCreator.createWeaponCard("electroscythe"); /* Load cost: NONE */
        spawnSquare.insertWeaponCard(electroscythe);

        /* The player has no ammo */

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                /* First answer: pick furnace */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(electroscythe)
                );

        /* Execute the action */
        GrabActionController controller = new GrabActionController(match, view, factory);
        controller.execute();

        /* Check that there has been exactly one interaction with the user */
        verify(view).sendChoiceRequest(any());

        /* Check that the player got the card */
        Optional<WeaponCard> card = player.getWeapons().stream()
                .filter(w -> w.getId().equals("electroscythe")).findFirst();
        assertTrue(card.isPresent());
        assertTrue(card.get().isUsable());
    }
}
