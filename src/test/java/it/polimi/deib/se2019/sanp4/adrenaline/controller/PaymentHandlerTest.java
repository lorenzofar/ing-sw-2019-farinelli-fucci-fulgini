package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.NotEnoughAmmoException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentHandlerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PaymentHandler paymentHandler;

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
    }

    @Before
    public void setUp() {
        /* Create a match */
        match = MatchCreator.createMatch(validNames, validConfig);
        paymentHandler = new PaymentHandler(match);

        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n,v);
        });
    }

    /* ========== PAY AMMO COST ============= */

    @Test
    public void payAmmoCost_cannotPay_noPowerups_shouldReturnFalse() throws InterruptedException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.BLUE, 1);
        cost.put(AmmoCubeCost.RED, 3);

        /* Try to pay it */
        boolean paid = paymentHandler.payAmmoCost(view, cost, true);

        assertFalse(paid);

        /* Check that the ammo are unchanged */
        for (AmmoCube value : AmmoCube.values()) {
            assertEquals(1, (int) player.getAmmo().get(value));
        }

        /* Check that the powerups are unchanged */
        assertTrue(player.getPowerups().isEmpty());
    }

    @Test
    public void payAmmoCost_cannotPay_notEnoughPowerups_shouldReturnFalse()
            throws InterruptedException, FullCapacityException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.BLUE, 1);
        cost.put(AmmoCubeCost.RED, 3);

        /* Add a RED powerup */
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));

        /* Try to pay */
        boolean paid = paymentHandler.payAmmoCost(view, cost, true);

        assertFalse(paid);

        /* Check that the ammo are unchanged */
        for (AmmoCube value : AmmoCube.values()) {
            assertEquals(1, (int) player.getAmmo().get(value));
        }

        /* Check that the powerups are unchanged */
        assertEquals(1, player.getPowerups().size());
    }

    @Test
    public void payAmmoCost_canPayWithOnlyAmmo_shouldNotUsePowerups()
            throws InterruptedException, FullCapacityException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.BLUE, 1);
        cost.put(AmmoCubeCost.RED, 3);

        /* Add a RED powerup */
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));

        /* Add enough ammo to pay the cost */
        player.addAmmo(AmmoCube.RED);
        player.addAmmo(AmmoCube.RED);

        /* Try to pay */
        boolean paid = paymentHandler.payAmmoCost(view, cost, true);

        assertTrue(paid);

        /* Check remaining ammo */
        Map<AmmoCube, Integer> remainingAmmo = player.getAmmo();
        assertEquals(0, (int) remainingAmmo.get(AmmoCube.RED));
        assertEquals(0, (int) remainingAmmo.get(AmmoCube.BLUE));
        assertEquals(1, (int) remainingAmmo.get(AmmoCube.YELLOW));

        /* Check that the powerups are unchanged */
        assertEquals(1, player.getPowerups().size());
    }

    @Test
    public void payAmmoCost_canPayWithAutomaticPowerups_shouldNotPromptUser() throws
            InterruptedException, FullCapacityException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.RED, 2);
        cost.put(AmmoCubeCost.BLUE, 1);
        cost.put(AmmoCubeCost.YELLOW, 2);

        /* Add powerups to pay the remaining cost */
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.YELLOW));
        PowerupCard spare = new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.BLUE);
        player.addPowerup(spare);

        /* Try to pay */
        boolean paid = paymentHandler.payAmmoCost(view, cost, true);

        assertTrue(paid);

        /* Check remaining ammo */
        for (AmmoCube value : AmmoCube.values()) {
            assertEquals(0, (int) player.getAmmo().get(value));
        }

        /* Check powerups */
        assertTrue(player.getPowerups().contains(spare));
        assertEquals(1, player.getPowerups().size());

        /* Check no interaction */
        verify(view, never()).sendChoiceRequest(any());
    }

    @Test
    public void payAmmoCost_cannotPayWithAutomaticPowerups_shouldPromptUser()
            throws InterruptedException, FullCapacityException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.RED, 2);
        cost.put(AmmoCubeCost.BLUE, 2);
        cost.put(AmmoCubeCost.ANY, 1); /* Should be covered by yellow */

        /* Add powerups to pay the remaining cost */
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.BLUE));
        /* The BLUE one will be picked up automatically, while the RED one will be chosen by the player */

        /* Set up the user's answer */
        doAnswer((SendChoiceRequestAnswer<PowerupCard>) req ->
            new CompletableChoice<>(req).complete(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.RED))
        ).when(view).sendChoiceRequest(any(PowerupCardRequest.class));

        /* Try to pay */
        boolean paid = paymentHandler.payAmmoCost(view, cost, true);

        assertTrue(paid);

        /* Check remaining ammo */
        for (AmmoCube value : AmmoCube.values()) {
            assertEquals(0, (int) player.getAmmo().get(value));
        }

        /* Check powerups */
        assertTrue(player.getPowerups().contains(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED)));
        assertEquals(1, player.getPowerups().size());

        /* Check interaction */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));
    }

    @Test
    public void payAmmoCost_cannotPayWithAutomaticPowerups_doNotRemoveAmmo_shouldPromptUser()
            throws InterruptedException, FullCapacityException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.RED, 2);
        cost.put(AmmoCubeCost.BLUE, 2);
        cost.put(AmmoCubeCost.ANY, 1); /* Should be covered by yellow */

        /* Add powerups to pay the remaining cost */
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.BLUE));
        /* The BLUE one will be picked up automatically, while the RED one will be chosen by the player */

        /* Set up the user's answer */
        doAnswer((SendChoiceRequestAnswer<PowerupCard>) req ->
                new CompletableChoice<>(req).complete(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.RED))
        ).when(view).sendChoiceRequest(any(PowerupCardRequest.class));

        /* Try to pay */
        boolean paid = paymentHandler.payAmmoCost(view, cost, false);

        assertTrue(paid);

        /* Check that the ammo have not been removed, but the powerups have been converted */
        Map<AmmoCube, Integer> remainingAmmo = player.getAmmo();
        assertEquals(2, (int) remainingAmmo.get(AmmoCube.RED));
        assertEquals(2, (int) remainingAmmo.get(AmmoCube.BLUE));
        assertEquals(1, (int) remainingAmmo.get(AmmoCube.YELLOW));

        /* Check powerups */
        assertTrue(player.getPowerups().contains(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED)));
        assertEquals(1, player.getPowerups().size());

        /* Check interaction */
        verify(view).sendChoiceRequest(any(PowerupCardRequest.class));
    }

    @Test
    public void payAmmoCost_noAmmo_onlyAny_shouldOnlyUsePowerups()
            throws InterruptedException, FullCapacityException, NotEnoughAmmoException {
        /* The player has one ammo for each color and no powerups */
        PersistentView view = views.get("bzoto");
        Player player = match.getPlayerByName("bzoto");

        /* Generate the cost */
        Map<AmmoCubeCost, Integer> cost = new EnumMap<>(AmmoCubeCost.class);
        cost.put(AmmoCubeCost.ANY, 2);

        /* Remove all Ammo from the player */
        Map<AmmoCube, Integer> removeInitial = new EnumMap<>(AmmoCube.class);
        for (AmmoCube color : AmmoCube.values()) {
            removeInitial.put(color, 1);
        }
        player.payAmmo(removeInitial);

        /* Add powerups to pay the cost */
        player.addPowerup(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.RED));
        player.addPowerup(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.YELLOW));
        /* The user will be asked to select a powerup twice */

        /* Set up the user's answer */
        when(view.sendChoiceRequest(any(PowerupCardRequest.class)))
                /* First response */
                .thenAnswer((SendChoiceRequestAnswer<PowerupCard>) req ->
                        new CompletableChoice<>(req)
                                .complete(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.RED))
                )
                /* Second response */
                .thenAnswer((SendChoiceRequestAnswer<PowerupCard>) req ->
                        new CompletableChoice<>(req)
                                .complete(new PowerupCard(PowerupEnum.TARGETING_SCOPE, AmmoCube.YELLOW))
                );

        /* Try to pay */
        boolean paid = paymentHandler.payAmmoCost(view, cost, true);

        assertTrue(paid);

        /* Check that the ammo have not been removed, but the powerups have been converted */
        /* Check remaining ammo */
        for (AmmoCube value : AmmoCube.values()) {
            assertEquals(0, (int) player.getAmmo().get(value));
        }

        /* Check powerups */
        assertTrue(player.getPowerups().contains(new PowerupCard(PowerupEnum.TELEPORTER, AmmoCube.RED)));
        assertEquals(1, player.getPowerups().size());

        /* Check interaction */
        verify(view, times(2)).sendChoiceRequest(any(PowerupCardRequest.class));
    }
}