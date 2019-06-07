package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PaymentHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.FirstChoiceAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.LoadedState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.UnloadedState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReloadActionControllerTest {

    private static Match match;
    private static String name = "bzoto";
    private static Player player;

    @Mock
    private static PersistentView view;
    @Mock
    private static ControllerFactory factory;

    /* NOTE TO MAINTAINER: WE USE A SINGLE INSTANCE OF MATCH, SO CLEAN THE SIDE EFFECTS AFTER THE TESTS */
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

        /* Remove any weapon from the player */
        for (WeaponCard w : player.getWeapons()) {
            player.removeWeapon(w);
        }
    }

    @Test
    public void execute_noWeaponsToReload_shouldNotPromptUser() throws Exception {
        /* Add some loaded weapons to the player */
        String[] wIds = {"shotgun", "thor"};
        for (String id : wIds) {
            WeaponCard w = WeaponCreator.createWeaponCard(id);
            w.setState(new LoadedState());
            player.addWeapon(w);
        }

        /* Execute the reload action */
        ReloadActionController controller = new ReloadActionController(match, factory);
        controller.execute(view);

        /* Check no interaction with the user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());
    }

    @Test
    public void execute_weaponToReload_playerCanPay_shouldBeReloaded() throws Exception {
        /* Add an unloaded weapon to the player */
        String[] wIds = {"thor"};
        for (String id : wIds) {
            WeaponCard w = WeaponCreator.createWeaponCard(id);
            w.setState(new UnloadedState());
            player.addWeapon(w);
        }

        /* Give ammo to pay the reload cost */
        player.addAmmo(AmmoCube.RED);
        player.addAmmo(AmmoCube.BLUE);

        /* Set up the user's answer: choose the first and only card */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class))).thenAnswer(new FirstChoiceAnswer());

        /* Execute the reload action */
        ReloadActionController controller = new ReloadActionController(match, factory);
        controller.execute(view);

        /* Check that the weapon has been reloaded */
        Optional<WeaponCard> w = player.getWeapons().stream()
                .filter(c -> c.getId().equals("thor")).findFirst();
        assertTrue(w.isPresent());
        assertTrue(w.get().isUsable());

        /* Check that the player paid the cost */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(0, (int) count));
    }

    @Test
    public void execute_weaponsToReload_playerCantPayFirst_shouldBeAskedToSelectSecond() throws Exception {
        /* Add two unloaded weapons to the player */
        WeaponCard thor = WeaponCreator.createWeaponCard("thor"); /* Cost: RB */
        WeaponCard tb = WeaponCreator.createWeaponCard("tractor_beam"); /* Cost: B */
        thor.setState(new UnloadedState());
        tb.setState(new UnloadedState());
        player.addWeapon(thor);
        player.addWeapon(tb);

        /* Give ammo to pay the reload cost of TB */
        player.addAmmo(AmmoCube.BLUE);

        /* Set up the user's answer: choose the first and only card */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                /* First answer: choose thor -> cannot reload */
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(thor)
                ).thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req ->
                        new CompletableChoice<>(req).complete(tb)
                );

        /* Execute the reload action */
        ReloadActionController controller = new ReloadActionController(match, factory);
        controller.execute(view);

        /* Check that the weapon has been reloaded */
        Optional<WeaponCard> w = player.getWeapons().stream()
                .filter(c -> c.getId().equals("tractor_beam")).findFirst();
        assertTrue(w.isPresent());
        assertTrue(w.get().isUsable());

        /* Check that the player paid the cost */
        Map<AmmoCube, Integer> playerAmmo = player.getAmmo();
        playerAmmo.forEach((cube, count) -> assertEquals(0, (int) count));
    }
}