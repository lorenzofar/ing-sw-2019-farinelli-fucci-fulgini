package it.polimi.deib.se2019.sanp4.adrenaline.controller.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.PowerupCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.ChooseNoneAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.FirstChoiceAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.powerups.PowerupController;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.LoadedState;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShootActionControllerTest {

    private static Match match;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static ControllerFactory factory;
    private static AbstractWeapon weaponController;
    private static PowerupController powerupController;
    private static Player shooter;

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
    public void setUp() throws Exception {
        /* Create a match */
        match = MatchCreator.createMatch(validNames, validConfig);
        Board board = match.getBoard();

        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            when(v.getUsername()).thenReturn(n);
            views.put(n,v);
        });
        view = views.get("bzoto");

        /* Create a stub of the weapon controller that will be returned by the factory */
        weaponController = mock(AbstractWeapon.class);
        when(weaponController.getDamagedPlayers())
                .thenReturn(Collections.emptySet()); /* Return no damaged players by default */

        /* Create a stub of the powerup controller that will be returned by the factory */
        powerupController = mock(PowerupController.class);
        when(powerupController.use(any())).thenReturn(true);

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);
        when(factory.createWeaponController(any())).thenReturn(weaponController);
        when(factory.createPowerupController(PowerupEnum.TAGBACK)).thenReturn(powerupController);

        /* Set up the shooter and his turn */
        shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(0,0));
        match.setCurrentTurn(new PlayerTurn(shooter));
    }

    @Test
    public void execute_shooterHasNoWeapons_shouldBeNotified() throws Exception {
        /* Set up a shooter with some weapons, but they're not loaded */
        WeaponCard w1 = WeaponCreator.createWeaponCard("flamethrower");
        WeaponCard w2 = WeaponCreator.createWeaponCard("shockwave");
        Set<WeaponCard> weaponCards = new HashSet<>();
        weaponCards.add(w1);
        weaponCards.add(w2);
        for (WeaponCard w : weaponCards) {
            w.unload();
            shooter.addWeapon(w);
        }

        /* Set up the action controller */
        ShootActionController controller = new ShootActionController(match, views, factory);

        /* Execute the action */
        controller.execute(view);

        /* Check that the user has been notified */
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));
    }

    @Test
    public void execute_shooterHasLoadedWeapon_shouldBeAbleToSelect() throws Exception {
        /* Set up a shooter with two weapons: one is loaded and one is not */
        WeaponCard w1 = WeaponCreator.createWeaponCard("flamethrower");
        w1.unload();
        WeaponCard w2 = WeaponCreator.createWeaponCard("shockwave");
        w2.setState(new LoadedState());
        shooter.addWeapon(w1);
        shooter.addWeapon(w2);

        /* Set up the user's answer: select the only weapon available */
        List<WeaponCard> givenChoices = new ArrayList<>(1);
        when(view.sendChoiceRequest(any(WeaponCardRequest.class)))
                .thenAnswer((SendChoiceRequestAnswer<WeaponCard>) req -> {
                    givenChoices.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(w2);
                });

        /* Set up the action controller */
        ShootActionController controller = new ShootActionController(match, views, factory);

        /* Execute the action */
        controller.execute(view);

        /* Check single interaction with user */
        verify(view).sendChoiceRequest(any());

        /* Check that the choices were as expected */
        assertEquals(1, givenChoices.size());
        assertTrue(givenChoices.contains(w2));

        /* Check that the used weapon has been unloaded */
        assertFalse(w2.isUsable());

        /* Check that *use* has been called on the controller of the weapon */
        verify(weaponController).use(view);
    }

    @Test
    public void execute_damagedPlayer_usesTagback_shouldBeAbleToRevenge() throws Exception {
        Player p1 = match.getPlayerByName("loSqualo");
        Player p2 = match.getPlayerByName("zoniMyLord");
        PersistentView v1 = views.get(p1.getName());
        PersistentView v2 = views.get(p2.getName());
        PowerupCard tagback1 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.RED);
        PowerupCard tagback2 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.BLUE);

        /* Set up a shooter with only one loaded weapon */
        WeaponCard w1 = WeaponCreator.createWeaponCard("flamethrower");
        w1.setState(new LoadedState());
        shooter.addWeapon(w1);

        /* Set up the shooter's answer: select the only weapon available */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class))).thenAnswer(new FirstChoiceAnswer());

        /* Set up the damaged player's answer: use the powerup */
        when(v1.sendChoiceRequest(any(PowerupCardRequest.class))).thenAnswer(new FirstChoiceAnswer());

        /* Set up the revenge: player p1 has been damaged and he has a tagback */
        when(weaponController.getDamagedPlayers()).thenReturn(Collections.singleton(p1));
        p1.addPowerup(tagback1);
        p1.addPowerup(tagback2);
        p2.addPowerup(tagback1); /* He has a tagback, but no damage */

        /* Set up the action controller */
        ShootActionController controller = new ShootActionController(match, views, factory);

        /* Execute the action */
        controller.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check no interaction with p2 */
        verify(v2, never()).sendChoiceRequest(any());

        /* Check single interaction with p1 */
        verify(v1).sendChoiceRequest(any(PowerupCardRequest.class));

        verify(powerupController).use(v1);

        /* Check that p1 doesn't have that powerup anymore */
        assertFalse(p1.getPowerups().contains(tagback1));
        assertTrue(p1.getPowerups().contains(tagback2));

        /* Check that the damaged players have been saved into the turn */
        assertEquals(1, match.getCurrentTurn().getDamagedPlayers().size());
        assertTrue(match.getCurrentTurn().getDamagedPlayers().contains(p1));
    }

    @Test
    public void execute_damagedPlayer_choosesNotToRevenge_shouldNotRevenge() throws Exception {
        Player p1 = match.getPlayerByName("loSqualo");
        Player p2 = match.getPlayerByName("zoniMyLord");
        Set<Player> victims = new HashSet<>(Arrays.asList(p1, p2));
        PersistentView v1 = views.get(p1.getName());
        PersistentView v2 = views.get(p2.getName());
        PowerupCard tagback1 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.RED);
        PowerupCard tagback2 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.BLUE);

        /* Set up a shooter with only one loaded weapon */
        WeaponCard w1 = WeaponCreator.createWeaponCard("flamethrower");
        w1.setState(new LoadedState());
        shooter.addWeapon(w1);

        /* Set up the shooter's answer: select the only weapon available */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class))).thenAnswer(new FirstChoiceAnswer());

        /* Set up the damaged player's answer: don't use the powerup */
        when(v1.sendChoiceRequest(any(PowerupCardRequest.class))).thenAnswer(new ChooseNoneAnswer<>());

        /* Set up the revenge: player p1 has been damaged and he has a tagback */
        when(weaponController.getDamagedPlayers()).thenReturn(victims);
        p1.addPowerup(tagback1);
        p1.addPowerup(tagback2);
        p2.addPowerup(new PowerupCard(PowerupEnum.NEWTON, AmmoCube.RED));

        /* Set up the action controller */
        ShootActionController controller = new ShootActionController(match, views, factory);

        /* Execute the action */
        controller.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check no interaction with p2 */
        verify(v2, never()).sendChoiceRequest(any());

        /* Check single interaction with p1 */
        verify(v1).sendChoiceRequest(any(PowerupCardRequest.class));

        verify(powerupController, never()).use(v1);

        /* Check that p1 still has both powerups */
        assertTrue(p1.getPowerups().contains(tagback1));
        assertTrue(p1.getPowerups().contains(tagback2));

        /* Check that the damaged players have been saved into the turn */
        assertEquals(2, match.getCurrentTurn().getDamagedPlayers().size());
        assertTrue(match.getCurrentTurn().getDamagedPlayers().containsAll(victims));
    }

    @Test
    public void execute_damagedPlayers_firstThrows_secondShouldStillRevenge() throws Exception {
        Player p1 = match.getPlayerByName("loSqualo");
        Player p2 = match.getPlayerByName("zoniMyLord");
        Set<Player> victims = new HashSet<>(Arrays.asList(p1, p2));
        PersistentView v1 = views.get(p1.getName());
        PersistentView v2 = views.get(p2.getName());
        PowerupCard tagback1 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.RED);
        PowerupCard tagback2 = new PowerupCard(PowerupEnum.TAGBACK, AmmoCube.BLUE);

        /* Set up a shooter with only one loaded weapon */
        WeaponCard w1 = WeaponCreator.createWeaponCard("flamethrower");
        w1.setState(new LoadedState());
        shooter.addWeapon(w1);

        /* Set up the shooter's answer: select the only weapon available */
        when(view.sendChoiceRequest(any(WeaponCardRequest.class))).thenAnswer(new FirstChoiceAnswer());

        /* Set up the damaged players' answers: first throws and second uses the powerup */
        final AtomicReference<Player> first = new AtomicReference<>();
        final AtomicReference<Player> second = new AtomicReference<>();

        SendChoiceRequestAnswer<PowerupCard> smartAnswer = req -> {
            if (first.get() == null) {
                first.set(req.getChoices().size() == 2 ? p1 : p2);
                return new CompletableChoice<>(req).cancel();
            } else {
                second.set(req.getChoices().size() == 2 ? p1 : p2);
                return new CompletableChoice<>(req).complete(tagback1);
            }
        };

        when(v1.sendChoiceRequest(any(PowerupCardRequest.class))).thenAnswer(smartAnswer);
        when(v2.sendChoiceRequest(any(PowerupCardRequest.class))).thenAnswer(smartAnswer);

        /* Set up the revenge: player p1 has been damaged and he has a tagback */
        when(weaponController.getDamagedPlayers()).thenReturn(victims);
        p1.addPowerup(tagback1);
        p1.addPowerup(tagback2);
        p2.addPowerup(tagback1); /* He has a tagback, but no damage */

        /* Set up the action controller */
        ShootActionController controller = new ShootActionController(match, views, factory);

        /* Execute the action */
        controller.execute(view);

        /* Check single interaction with shooter */
        verify(view).sendChoiceRequest(any());

        /* Check single interaction with p2 */
        verify(v2).sendChoiceRequest(any(PowerupCardRequest.class));

        /* Check single interaction with p1 */
        verify(v1).sendChoiceRequest(any(PowerupCardRequest.class));

        /* TODO: Check call to powerup controller */

        /* Check that the first still has the powerup which is chosen */
        assertTrue(first.get().getPowerups().contains(tagback1));

        /* Check that the second doesn't have the used powerup */
        assertFalse(second.get().getPowerups().contains(tagback1));

        /* Check that the damaged players have been saved into the turn */
        assertEquals(2, match.getCurrentTurn().getDamagedPlayers().size());
        assertTrue(match.getCurrentTurn().getDamagedPlayers().containsAll(victims));
    }
}