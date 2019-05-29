package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class AmmoCardCreatorTest {
    @BeforeClass
    public static void setUp() throws Exception {
        /* Load needed schemas for validation */
        JSONUtils.loadAmmoCardPackSchema("/schemas/ammo_card_pack.schema.json");
    }

    @After
    public void tearDown() throws Exception {
        AmmoCardCreator.reset();
    }

    @Test(expected = CardNotFoundException.class)
    public void getAmmoCard_notExists_shouldThrow() throws CardNotFoundException {
        AmmoCardCreator.loadAmmoCardPack("/assets/ammo_card_pack_valid.json");
        AmmoCardCreator.getAmmoCard(15);
    }

    @Test
    public void loadAmmoCardPack_validPack_shouldSucceed() throws CardNotFoundException {
        /* Load the pack, should not throw */
        AmmoCardCreator.loadAmmoCardPack("/assets/ammo_card_pack_valid.json");

        /* Check if loaded succesfully */
        Collection<AmmoCard> cards = AmmoCardCreator.getAmmoCardDeck();

        /* Request the first card */
        AmmoCard actual = AmmoCardCreator.getAmmoCard(0);
        assertEquals(0, actual.getId());
        assertEquals(1, (int) actual.getCubes().get(AmmoCube.RED));
        assertEquals(1, (int) actual.getCubes().get(AmmoCube.BLUE));
        assertEquals(1, (int) actual.getCubes().get(AmmoCube.YELLOW));

        /* Request the second card */
        actual = AmmoCardCreator.getAmmoCard(5);
        assertEquals(5, actual.getId());
        assertEquals(3, (int) actual.getCubes().get(AmmoCube.RED));
        assertEquals(0, (int) actual.getCubes().get(AmmoCube.YELLOW));
        assertEquals(0, (int) actual.getCubes().get(AmmoCube.BLUE));
    }

    @Test(expected = ValidationException.class)
    public void loadAmmoCardPack_invalidPack_shouldThrow() {
        AmmoCardCreator.loadAmmoCardPack("/assets/ammo_card_pack_invalid_missingcolor.json");
    }

    @Test
    public void loadAmmoCardPack_StandardPack_ShouldSucceed() {
        AmmoCardCreator.loadAmmoCardPack("/assets/standard_ammocards.json");
        /* Just check the count */
        assertEquals(36, AmmoCardCreator.getAmmoCardDeck().size());
    }
}