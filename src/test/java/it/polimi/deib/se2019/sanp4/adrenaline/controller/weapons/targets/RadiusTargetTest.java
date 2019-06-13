package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeaponStub;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
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

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.ANY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RadiusTargetTest {

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
        validNames.add("durian");
        validNames.add("circoletto");

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
            views.put(n, v);
        });

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);

        /* Create a stub of the abstract weapon */
        weapon = new AbstractWeaponStub(weaponCard, match, views, factory);
    }

    @Test
    public void execute_visibilityAny_shouldNotPassThroughWalls() {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other players around the shooter at distance 1 */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,0));
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,1));
        Player t3 = match.getPlayerByName("durian");
        board.movePlayer(t3, board.getSquare(1,2));
        Player t4 = match.getPlayerByName("circoletto");
        board.movePlayer(t4, board.getSquare(0,1));

        /* Set up the target */
        RadiusTarget target = new RadiusTarget("radius", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Execute */
        target.execute(view);

        /* Check that all the players except the one behind the wall (t4) got the damage */
        for (Player p : new Player[]{t1, t2, t3}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(1));
        }

        /* Check that that t4 and the shooter got no damage */
        for (Player p : new Player[]{t4, shooter}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(0));
        }
    }

    @Test
    public void execute_minDist_shouldExcludeTooClose() {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other players around the shooter */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,0)); /* Distance 1 */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,1)); /* Distance 1 */
        Player t4 = match.getPlayerByName("circoletto");
        board.movePlayer(t4, board.getSquare(0,1)); /* Distance 1, behind wall */

        /* Set t3 in the same square as the shooter */
        Player t3 = match.getPlayerByName("durian");
        board.movePlayer(t3, shooter.getCurrentSquare()); /* Distance 0 */

        /* Set up the target */
        RadiusTarget target = new RadiusTarget("radius", weapon, match, factory);
        target.setVisibility(ANY);
        /* Squares exactly one move away */
        target.setMinDist(1);
        target.setMaxDist(1);
        target.setDamage(1);

        /* Execute */
        target.execute(view);

        /* Check that targetable players got the damage */
        for (Player p : new Player[]{t1, t2}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(1));
        }

        /* Check that that t3, t4 and the shooter got no damage */
        for (Player p : new Player[]{t3, t4, shooter}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(0));
        }
    }

    @Test
    public void execute_noneTargetable_shouldTerminate() {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other players around the shooter at distance 1 */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,0));
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(2,1));
        Player t3 = match.getPlayerByName("durian");
        board.movePlayer(t3, board.getSquare(1,2));
        Player t4 = match.getPlayerByName("circoletto");
        board.movePlayer(t4, board.getSquare(0,1));

        /* Set up the target */
        RadiusTarget target = new RadiusTarget("radius", weapon, match, factory);
        target.setVisibility(ANY);
        target.setMaxDist(0); /* No one can be targeted */
        target.setDamage(1);

        /* Execute */
        assertFalse(target.execute(view));

        /* Check that that all the players got no damage */
        for (Player p : new Player[]{t1, t2, t3, t4, shooter}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(0));
        }
    }

    @Test
    public void execute_excludeSquares_shouldBeExcluded() {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other players around the shooter */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,0)); /* Distance 1 */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(3,1)); /* Distance 2 */
        Player t3 = match.getPlayerByName("durian");
        board.movePlayer(t3, board.getSquare(1,2)); /* Distance 1, saved in weapon */
        weapon.saveSquare("sqr", t3.getCurrentSquare());
        Player t4 = match.getPlayerByName("circoletto");
        board.movePlayer(t4, board.getSquare(0,1)); /* Distance 1, behind wall */

        /* Set up the target */
        RadiusTarget target = new RadiusTarget("radius", weapon, match, factory);
        target.setVisibility(ANY);
        target.setExcludeSquares(Collections.singleton("sqr"));
        target.setMaxDist(2);
        target.setDamage(1);

        /* Execute */
        target.execute(view);

        /* Check that targetable players got the damage */
        for (Player p : new Player[]{t1, t2}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(1));
        }

        /* Check that that t3, t4 and the shooter got no damage */
        for (Player p : new Player[]{t3, t4, shooter}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(0));
        }
    }

    @Test
    public void execute_excludePlayers_shouldBeExcluded() {
        /* Set the shooter in a desired square */
        Player shooter = match.getPlayerByName("bzoto");
        board.movePlayer(shooter, board.getSquare(1,1));
        PersistentView view = views.get("bzoto");

        /* Set the other players around the shooter */
        Player t1 = match.getPlayerByName("loSqualo");
        board.movePlayer(t1, board.getSquare(1,0)); /* Distance 1 */
        Player t2 = match.getPlayerByName("zoniMyLord");
        board.movePlayer(t2, board.getSquare(3,1)); /* Distance 2 */
        Player t3 = match.getPlayerByName("durian");
        board.movePlayer(t3, board.getSquare(1,2)); /* Distance 1, saved in weapon */
        weapon.savePlayer("red", t3);
        Player t4 = match.getPlayerByName("circoletto");
        board.movePlayer(t4, board.getSquare(0,1)); /* Distance 1, behind wall */

        /* Set up the target */
        RadiusTarget target = new RadiusTarget("radius", weapon, match, factory);
        target.setVisibility(ANY);
        target.setExcludePlayers(Collections.singleton("red"));
        target.setMaxDist(2);
        target.setDamage(1);

        /* Execute */
        target.execute(view);

        /* Check that targetable players got the damage */
        for (Player p : new Player[]{t1, t2}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(1));
        }

        /* Check that that t3, t4 and the shooter got no damage */
        for (Player p : new Player[]{t3, t4, shooter}) {
            assertThat(p.getPlayerBoard().getDamageCount(), is(0));
        }
    }
}