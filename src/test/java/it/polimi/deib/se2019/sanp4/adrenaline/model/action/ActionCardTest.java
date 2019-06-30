package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ActionCardTest {

    private static int validMaxActions = 2;
    private static ActionCardEnum validType = ActionCardEnum.ADRENALINE1;
    private static Collection<ActionEnum> validActions;
    private static ActionEnum validFinalAction;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void setup(){
        /* Set up the resources */
        JSONUtils.loadActionCardPackSchema("/schemas/action_card_pack.schema.json");
        ActionCardCreator.loadActionCardPack("/assets/standard_actioncards.json");

        validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_GRAB);
        validActions.add(ActionEnum.ADRENALINE_SHOOT);

        validFinalAction = ActionEnum.RELOAD;

        mapper = JSONUtils.getObjectMapper();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCard_negativeMaxActionsProvided_shouldThrowIllegalArgumentException(){
        new ActionCard(-1, validType, validActions, validFinalAction);
    }

    @Test(expected =IllegalArgumentException.class)
    public void createCard_emptyActionsListProvided_shouldThrowIllegalArgumentException(){
        new ActionCard(validMaxActions, validType, Collections.emptyList(), validFinalAction);
    }

    @Test(expected = NullPointerException.class)
    public void createCard_nullTypeProvided_shouldThrowNullPointerException(){
        new ActionCard(validMaxActions, null, validActions, validFinalAction);
    }

    @Test(expected = NullPointerException.class)
    public void createCard_nullActionsListProvided_shouldThrowNullPointerException(){
        new ActionCard(validMaxActions, validType, null, validFinalAction);
    }

    @Test
    public void createCard_properParametersProvided_shouldNotThrowException(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertEquals(validType, actionCard.getType());
        assertTrue(validActions.containsAll(actionCard.getActions()));
        assertTrue(actionCard.getActions().containsAll(validActions));
        assertEquals(validFinalAction, actionCard.getFinalAction());
        assertTrue(validActions.containsAll(actionCard.getActions()));
        assertTrue(actionCard.getActions().containsAll(validActions));
    }

    @Test
    public void getActions_shouldReturnListContainingProvidedActions(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertTrue(validActions.containsAll(actionCard.getActions()));
        assertTrue(actionCard.getActions().containsAll(validActions));
    }

    @Test
    public void getMaxActions_shouldReturnProvidedMaxActions(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertEquals(actionCard.getMaxActions(), validMaxActions);
    }

    @Test
    public void getFinalAction_shouldReturnProvidedFinalAction(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertEquals(actionCard.getFinalAction(), validFinalAction);
    }

    @Test
    public void getType_shouldReturnProvidedType(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertEquals(actionCard.getType(), validType);
    }

    @Test(expected = NullPointerException.class)
    public void checkHasAction_nullActionProvided_shouldThrowNullPointerException(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        actionCard.hasAction(null);
    }

    @Test
    public void checkHasAction_actionContained_shouldReturnTrue(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertTrue(actionCard.hasAction(validActions.iterator().next()));
    }

    @Test
    public void checkHasFinalAction_finalActionProvided_shouldReturnTrue(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertTrue(actionCard.hasFinalAction());
    }

    @Test
    public void checkHasFinalAction_finalActionNotProvided_shouldReturnFalse(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, null);
        assertFalse(actionCard.hasFinalAction());
    }

    @Test
    public void checkEquals_selfPassed_shouldReturnTrue(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertTrue(actionCard.equals(actionCard));
    }

    @Test
    public void checkEquals_anotherClassPassed_shouldreturnFalse(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertFalse(actionCard.equals(new Object()));
    }

    @Test
    public void checkEquals_ammoCardWithSameTypeProvided_shouldReturnTrue(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertTrue(actionCard.equals(new ActionCard(validMaxActions + 1, ActionCardEnum.ADRENALINE1, validActions, validFinalAction)));
    }

    @Test
    public void checkEquals_ammoCardWithDifferentTypeProvided_shouldReturnFalse(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        assertFalse(actionCard.equals(new ActionCard(validMaxActions + 1, ActionCardEnum.ADRENALINE2, validActions, validFinalAction)));
    }

    @Test
    public void getHashCode_compareWithAnotherCard_ShouldBeSame(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        ActionCard anotherCard = new ActionCard(2, ActionCardEnum.FRENZY1, validActions, null);
        assertNotEquals(actionCard.hashCode(), anotherCard.hashCode());
    }

    @Test
    public void serializeCard_shouldContainAllInformation(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        try {
            final String serializedActionCard = mapper.writeValueAsString(actionCard);
            assertThat(serializedActionCard, containsString(String.format("\"maxActions\":%d", validMaxActions)));
            assertThat(serializedActionCard, containsString(String.format("\"type\":\"%s\"", validType.name())));
            assertThat(serializedActionCard, containsString("\"actions\":"));
            validActions.forEach((action) -> assertThat(serializedActionCard, containsString(action.name())));
            assertThat(serializedActionCard, containsString(String.format("\"finalAction\":\"%s\"", validFinalAction.name())));
        } catch (JsonProcessingException e) {
            Assert.fail();
        }
    }

    @Test
    public void serializeCard_noFinalAction_shouldContainAllInformationAndNullFinalAction(){
        ActionCard actionCard = new ActionCard(validMaxActions, validType, validActions, null);
        try {
            final String serializedActionCard = mapper.writeValueAsString(actionCard);
            assertThat(serializedActionCard, containsString(String.format("\"maxActions\":%d", validMaxActions)));
            assertThat(serializedActionCard, containsString(String.format("\"type\":\"%s\"", validType.name())));
            assertThat(serializedActionCard, containsString("\"actions\":"));
            validActions.forEach((action) -> assertThat(serializedActionCard, containsString(action.name())));
            assertThat(serializedActionCard, containsString("\"finalAction\":null"));
        } catch (JsonProcessingException e) {
            Assert.fail();
        }
    }

    /* ======= THE FOLLOWING USE ACTION CARD CREATOR ======== */

    @Test
    public void initial_shouldReturnRegular() {
        assertThat(ActionCard.initial().getType(), is(ActionCardEnum.REGULAR));
    }

    @Test
    public void next_regularMode_noDamage_shouldReturnRegular() {
        ActionCard current = ActionCardCreator.createActionCard(ActionCardEnum.ADRENALINE1);
        ActionCard next = current.next(0);
        assertThat(next.getType(), is(ActionCardEnum.REGULAR));
    }

    @Test
    public void next_regularMode_enoughDamage_shouldReturnAdrenaline1() {
        ActionCard current = ActionCardCreator.createActionCard(ActionCardEnum.REGULAR);
        ActionCard next = current.next(3);
        assertThat(next.getType(), is(ActionCardEnum.ADRENALINE1));
    }

    @Test
    public void next_regularMode_enoughDamage_shouldReturnAdrenaline2() {
        ActionCard current = ActionCardCreator.createActionCard(ActionCardEnum.REGULAR);
        ActionCard next = current.next(8);
        assertThat(next.getType(), is(ActionCardEnum.ADRENALINE2));
    }

    @Test
    public void next_frenzy1_shouldKeepSameCard() {
        ActionCard current = ActionCardCreator.createActionCard(ActionCardEnum.FRENZY1);
        ActionCard next = current.next(3);
        assertThat(next.getType(), is(ActionCardEnum.FRENZY1));
    }

    @Test
    public void next_frenzy2_shouldKeepSameCard() {
        ActionCard current = ActionCardCreator.createActionCard(ActionCardEnum.FRENZY2);
        ActionCard next = current.next(3);
        assertThat(next.getType(), is(ActionCardEnum.FRENZY2));
    }

    @Test
    public void generateView_ShouldSucceed() {
        ActionCardView view = (new ActionCard(validMaxActions, validType, validActions, validFinalAction)
                                .generateView());
        assertEquals(validType, view.getType());
        assertTrue(view.getActions().containsAll(view.getActions()));
        assertEquals(validFinalAction, view.getFinalAction());
    }
}
