package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.OptionalEffectsWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.MovementEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.PlayerTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class StandardControllerFactoryTest {

    @BeforeClass
    public static void setUpClass() {
        /* Set up weapon creator */
        JSONUtils.loadWeaponPackSchema("/schemas/weapon_pack.schema.json");
        JSONUtils.loadWeaponSchema("/schemas/weapon.schema.json");
        WeaponCreator.loadWeaponPack("/assets/standard_weapons.json");
    }

    @Mock
    private static Map<String, PersistentView> views;

    @Mock
    private static Match match;

    @Test(expected = NullPointerException.class)
    public void create_nullMatch_shouldThrow() {
        new StandardControllerFactory(null, views);
    }

    @Test(expected = NullPointerException.class)
    public void create_nullViews_shouldThrow() {
        new StandardControllerFactory(match, null);
    }

    @Test
    public void create_properParameters_shouldCreate() {
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        assertEquals(match, factory.getMatch());
        assertEquals(views, factory.getViews());
    }

    /* =================== WEAPONS ===================== */

    @Test
    public void createWeaponController_cyberblade_shouldSucceed() throws IOException {
        /* Set up the factory */
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        /* Create the weapon card */
        WeaponCard card = WeaponCreator.createWeaponCard("cyberblade");

        /* Create the controller for the weapon */
        OptionalEffectsWeapon weapon = (OptionalEffectsWeapon) factory.createWeaponController(card);

        /* Check weapon properties */
        assertEquals(card, weapon.getWeaponCard());
        assertEquals(ShootingDirectionEnum.ANY, weapon.getShootingDirection());

        /* Check basic effect */
        TargetingEffect basic = (TargetingEffect) weapon.getEffects().get("basic");
        assertEquals("basic", basic.getId());
        assertTrue(basic.getCost().isEmpty());
        assertTrue(basic.getDependsOnEffects().isEmpty());
        assertFalse(basic.isOptional());

        /* First target */
        PlayerTarget red = (PlayerTarget) basic.getTargets().get(0);
        assertEquals("red", red.getId());
        assertFalse(red.isOptional());
        assertEquals(2, red.getDamage());
        assertEquals(0, red.getMarks());
        assertEquals(VisibilityEnum.ANY, red.getVisibility());
        assertNull(red.getMinDist());
        assertEquals(0, (int) red.getMaxDist());
        assertTrue(red.getExcludePlayers().isEmpty());
        assertTrue(red.getExcludeSquares().isEmpty());
        /* ---- */
        assertNull(red.getChooseBetweenTargets());
        assertNull(red.getVisibleFromPlayer());
        assertFalse(red.isMoveShooterHere());
        assertNull(red.getSquareRef());
        /* ---- */
        assertEquals(0, red.getMoveTargetBefore());
        assertEquals(0, red.getMoveTargetAfter());

        /* Check shadowstep effect */
        MovementEffect shadowstep = (MovementEffect) weapon.getEffects().get("shadowstep");
        assertEquals("shadowstep", shadowstep.getId());
        assertTrue(shadowstep.getCost().isEmpty());
        assertTrue(shadowstep.getDependsOnEffects().isEmpty());
        assertTrue(shadowstep.isOptional());
        assertEquals(1, shadowstep.getMaxMoves());

        /* Check slice and dice effect */
        TargetingEffect sliceAndDice = (TargetingEffect) weapon.getEffects().get("slice_and_dice");
        assertEquals("slice_and_dice", sliceAndDice.getId());
        assertTrue(sliceAndDice.getCost().contains(AmmoCubeCost.YELLOW));
        assertTrue(sliceAndDice.getDependsOnEffects().contains("basic"));
        assertTrue(sliceAndDice.isOptional());

        /* First target */
        PlayerTarget blue = (PlayerTarget) sliceAndDice.getTargets().get(0);
        assertEquals("blue", blue.getId());
        assertFalse(blue.isOptional());
        assertEquals(2, blue.getDamage());
        assertEquals(0, blue.getMarks());
        assertEquals(VisibilityEnum.ANY, blue.getVisibility());
        assertNull(blue.getMinDist());
        assertEquals(0, (int) red.getMaxDist());
        assertTrue(blue.getExcludePlayers().contains("red"));
        assertTrue(blue.getExcludeSquares().isEmpty());
        /* ---- */
        assertNull(blue.getChooseBetweenTargets());
        assertNull(blue.getVisibleFromPlayer());
        assertFalse(blue.isMoveShooterHere());
        assertNull(blue.getSquareRef());
        /* ---- */
        assertEquals(0, blue.getMoveTargetBefore());
        assertEquals(0, blue.getMoveTargetAfter());
    }

    @Test
    public void createWeaponController_standardPack_shouldSucceed() throws IOException {
        /* Set up the factory */
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        /* Create all the weapon cards */
        Collection<WeaponCard> cards = WeaponCreator.createWeaponCardDeck();
        assertEquals(21, cards.size());

        /* Create all the controllers */
        Collection<AbstractWeapon> weapons = cards.stream()
                .map(factory::createWeaponController)
                .collect(Collectors.toList());
        assertEquals(21, weapons.size());
    }
}