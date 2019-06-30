package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AlternativeModesWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.OptionalEffectsWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.ShootingDirectionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.MovementEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.PlayerTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets.RadiusTarget;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
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

    @Test(expected = JSONException.class)
    public void createEffectController_invalidType_shouldThrow() {
        AbstractWeapon weapon = mock(AbstractWeapon.class);

        /* Set up the factory */
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        /* Set up config */
        JSONObject config = new JSONObject();
        config.put("id", "effect");
        config.put("type", "INVALID");

        factory.createEffectController(weapon, config);
    }

    @Test(expected = JSONException.class)
    public void createTargetController_invalidMode_shouldThrow() {
        AbstractWeapon weapon = mock(AbstractWeapon.class);

        /* Set up the factory */
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        /* Set up config */
        JSONObject config = new JSONObject();
        config.put("id", "target");
        config.put("targetMode", "INVALID");

        factory.createTargetController(weapon, config);
    }

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
    public void createWeaponController_shockwave_shouldSucceed() throws IOException {
        /* Set up the factory */
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        /* Create the weapon card */
        WeaponCard card = WeaponCreator.createWeaponCard("shockwave");

        /* Create the controller for the weapon */
        AlternativeModesWeapon weapon = (AlternativeModesWeapon) factory.createWeaponController(card);

        /* Check weapon properties */
        assertEquals(card, weapon.getWeaponCard());
        assertEquals(ShootingDirectionEnum.CARDINAL, weapon.getShootingDirection());

        /* Check basic mode */
        TargetingEffect basic = (TargetingEffect) weapon.getEffects().get("basic");
        assertEquals("basic", basic.getId());
        assertTrue(basic.getCost().isEmpty());
        assertTrue(basic.getDependsOnEffects().isEmpty());
        assertFalse(basic.isOptional());

        /* First target */
        PlayerTarget red = (PlayerTarget) basic.getTargets().get(0);
        assertEquals("red", red.getId());
        assertFalse(red.isOptional());
        assertEquals(1, red.getDamage());
        assertEquals(0, red.getMarks());
        assertEquals(VisibilityEnum.ANY, red.getVisibility());
        assertEquals(1, (int) red.getMinDist());
        assertEquals(1, (int) red.getMaxDist());
        assertTrue(red.getExcludePlayers().isEmpty());
        assertTrue(red.getExcludeSquares().isEmpty());
        /* ---- */
        assertNull(red.getChooseBetweenTargets());
        assertNull(red.getVisibleFromPlayer());
        assertFalse(red.isMoveShooterHere());
        assertEquals("sqr_red", red.getSquareRef());
        /* ---- */
        assertEquals(0, red.getMoveTargetBefore());
        assertEquals(0, red.getMoveTargetAfter());

        /* Second target */
        PlayerTarget blue = (PlayerTarget) basic.getTargets().get(1);
        assertEquals("blue", blue.getId());
        assertTrue(blue.isOptional());
        assertEquals(1, blue.getDamage());
        assertEquals(0, blue.getMarks());
        assertEquals(VisibilityEnum.ANY, blue.getVisibility());
        assertEquals(1, (int) blue.getMinDist());
        assertEquals(1, (int) blue.getMaxDist());
        assertTrue(blue.getExcludePlayers().isEmpty());
        assertTrue(blue.getExcludeSquares().contains("sqr_red"));
        /* ---- */
        assertNull(blue.getChooseBetweenTargets());
        assertNull(blue.getVisibleFromPlayer());
        assertFalse(blue.isMoveShooterHere());
        assertEquals("sqr_blue", blue.getSquareRef());
        /* ---- */
        assertEquals(0, blue.getMoveTargetBefore());
        assertEquals(0, blue.getMoveTargetAfter());

        /* Third target */
        PlayerTarget green = (PlayerTarget) basic.getTargets().get(2);
        assertEquals("green", green.getId());
        assertTrue(green.isOptional());
        assertEquals(1, green.getDamage());
        assertEquals(0, green.getMarks());
        assertEquals(VisibilityEnum.ANY, green.getVisibility());
        assertEquals(1, (int) green.getMinDist());
        assertEquals(1, (int) green.getMaxDist());
        assertTrue(green.getExcludePlayers().isEmpty());
        assertTrue(green.getExcludeSquares().contains("sqr_red"));
        assertTrue(green.getExcludeSquares().contains("sqr_blue"));
        /* ---- */
        assertNull(green.getChooseBetweenTargets());
        assertNull(green.getVisibleFromPlayer());
        assertFalse(green.isMoveShooterHere());
        assertNull(green.getSquareRef());
        /* ---- */
        assertEquals(0, green.getMoveTargetBefore());
        assertEquals(0, green.getMoveTargetAfter());

        /* Check tsunami mode */
        TargetingEffect tsunami = (TargetingEffect) weapon.getEffects().get("tsunami_mode");
        assertEquals("tsunami_mode", tsunami.getId());
        assertTrue(tsunami.getCost().contains(AmmoCubeCost.YELLOW));
        assertTrue(tsunami.getDependsOnEffects().isEmpty());
        assertFalse(tsunami.isOptional());

        /* Check radius target */
        RadiusTarget radius = (RadiusTarget) tsunami.getTargets().get(0);
        assertEquals("radius", radius.getId());
        assertFalse(radius.isOptional());
        assertEquals(1, radius.getDamage());
        assertEquals(0, radius.getMarks());
        assertEquals(VisibilityEnum.ANY, radius.getVisibility());
        assertEquals(1, (int) radius.getMinDist());
        assertEquals(1, (int) radius.getMaxDist());
        assertTrue(radius.getExcludePlayers().isEmpty());
        assertTrue(radius.getExcludeSquares().isEmpty());
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