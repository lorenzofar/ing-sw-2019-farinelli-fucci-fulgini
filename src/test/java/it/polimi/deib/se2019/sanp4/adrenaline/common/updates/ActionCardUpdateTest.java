package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.ActionCardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ActionCardUpdateTest {

    private Collection<ActionEnum> actions = new HashSet<>();
    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();



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

        /* Serialize */
        ActionCardUpdate update = new ActionCardUpdate(actionCardView);
        String s = objectMapper.writeValueAsString(update);

        /* Read */
        ActionCardUpdate actionCardUpdate = objectMapper.readValue(s, ActionCardUpdate.class);

        assertEquals(actionCardView.getActions(), actionCardUpdate.getActionCard().getActions());
        assertEquals(actionCardView.getFinalAction(), actionCardUpdate.getActionCard().getFinalAction());
        assertEquals(actionCardView.getType(), actionCardUpdate.getActionCard().getType());

    }
}