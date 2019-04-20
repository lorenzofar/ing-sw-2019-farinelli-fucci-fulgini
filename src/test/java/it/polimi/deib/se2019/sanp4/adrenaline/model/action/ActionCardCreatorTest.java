package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.everit.json.schema.ValidationException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class ActionCardCreatorTest {

    @BeforeClass
    public static void setUpClass(){
        /* Load needed schemas for validation */
        JSONUtils.loadActionCardPackSchema("/schemas/action_card_pack.schema.json");
    }

    @After
    public void tearDown() throws Exception {
        /* Bring the class back to its original state */
        ActionCardCreator.reset();
    }

    @Test
    public void loadActionCardPack_validPack_shouldSucceed() throws CardNotFoundException {
        /* Load the pack, should not throw (loads REGULAR and FRENZY1 */
        ActionCardCreator.loadActionCardPack("/assets/action_card_pack_valid.json");

        /* Check the loaded cards*/
        ActionCard card = ActionCardCreator.createActionCard(ActionCardEnum.REGULAR);
        assertEquals(ActionCardEnum.REGULAR, card.getType());

        card = ActionCardCreator.createActionCard(ActionCardEnum.FRENZY1);
        assertEquals(ActionCardEnum.FRENZY1, card.getType());
    }

    @Test(expected = ValidationException.class)
    public void loadActionCardPack_invalidPack_shouldThrow() {
        ActionCardCreator.loadActionCardPack("/assets/action_card_pack_invalid.json");
    }

    @Test
    public void createActionCard_existentCard_shouldBeCorrect() throws CardNotFoundException {
        /* Load the pack with the card(s) */
        ActionCardCreator.loadActionCardPack("/assets/action_card_pack_valid.json");

        /* Get the card to check */
        ActionCard card  = ActionCardCreator.createActionCard(ActionCardEnum.REGULAR);
        assertEquals(ActionCardEnum.REGULAR, card.getType());
        assertEquals(2, card.getMaxActions());
        assertEquals(ActionEnum.RELOAD, card.getFinalAction());
        Collection<ActionEnum> actions = new ArrayList<>();
        actions.add(ActionEnum.RUN);
        actions.add(ActionEnum.GRAB);
        actions.add(ActionEnum.SHOOT);
        /* Check that the actions are exactly the ones defined in the JSON */
        assertTrue(card.getActions().containsAll(actions));
    }

    @Test(expected = CardNotFoundException.class)
    public void createActionCard_inexistentCard_shouldThrow() throws CardNotFoundException {
        /* Load the pack with the card(s) */
        ActionCardCreator.loadActionCardPack("/assets/action_card_pack_valid.json");

        /* Ask for card that has not been loaded */
        ActionCardCreator.createActionCard(ActionCardEnum.ADRENALINE1);
    }

    @Test(expected = NullPointerException.class)
    public void createActionCard_nullCard_shouldThrow() throws CardNotFoundException {
        /* Ask for card that has not been loaded */
        ActionCardCreator.createActionCard(null);
    }
}