package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.common.JSONUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ActionCardUpdateTest {

    private Collection<ActionEnum> actions = new HashSet<>();
    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    @Mock
    private static ModelUpdateVisitor visitor;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        actions.add(ActionEnum.FRENZY2_RUN);
        ActionCardView actionCardView = new ActionCardView(ActionCardEnum.ADRENALINE1,
                actions, ActionEnum.RELOAD);

        /* Test setters */
        actions = new ArrayList<>();
        actions.add(ActionEnum.FRENZY1_GRAB);
        actionCardView.setActions(actions);
        ActionCardEnum type = ActionCardEnum.FRENZY2;
        actionCardView.setType(type);
        ActionEnum finalAction = ActionEnum.ADRENALINE_SHOOT;
        actionCardView.setFinalAction(finalAction);
        String actionCardOwner = "player1";

        /* Serialize */
        ActionCardUpdate update = new ActionCardUpdate(actionCardView, actionCardOwner);
        String s = objectMapper.writeValueAsString(update);

        /* Read */
        ActionCardUpdate actionCardUpdate = objectMapper.readValue(s, ActionCardUpdate.class);

        assertEquals(actionCardOwner, actionCardUpdate.getPlayer());
        assertEquals(actionCardView.getActions(), actionCardUpdate.getActionCard().getActions());
        assertEquals(actionCardView.getFinalAction(), actionCardUpdate.getActionCard().getFinalAction());
        assertEquals(actionCardView.getType(), actionCardUpdate.getActionCard().getType());

    }

    @Test
    public void accept_shouldAcceptVisitor() {
        ActionCardView actionCardView = new ActionCardView(ActionCardEnum.ADRENALINE1,
                actions, ActionEnum.RELOAD);

        ActionCardUpdate update = new ActionCardUpdate(actionCardView, "player");

        update.accept(visitor);

        verify(visitor).handle(update);
    }
}