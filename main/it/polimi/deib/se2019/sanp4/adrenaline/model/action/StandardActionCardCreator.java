package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardCreator;

public class StandardActionCardCreator implements ActionCardCreator {
    private ActionCard regularActionCard;
    private ActionCard frenzyActioncardBeforeFirst;
    private ActionCard frenzyActionCardAfterFirst;

    StandardActionCardCreator(Object config){}; //TODO: Fix config parameter

    public ActionCard createRegularActionCard(){
        return null;
    };

    public ActionCard createFrenzyActionCardBeforeFirst(){
        return null;
    }

    @Override
    public ActionCard createFrenzyActionCardAfterFirst() {
        return null;
    }

}
