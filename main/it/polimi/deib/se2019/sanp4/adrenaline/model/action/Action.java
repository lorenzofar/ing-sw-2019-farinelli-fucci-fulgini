package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

import java.util.ArrayList;

public class Action {
    private ArrayList<BasicAction> basicActions;

    Action(ArrayList<BasicAction> basicActions){

    }

    public BasicAction getNextBasicAction(){
        return null;
    }

    @Override
    public Object clone(){
        return null;
    };
}
