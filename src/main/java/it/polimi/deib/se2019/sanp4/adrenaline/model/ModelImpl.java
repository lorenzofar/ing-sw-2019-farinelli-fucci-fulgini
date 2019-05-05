package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteRoutingObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

public class ModelImpl extends RemoteRoutingObservable<ModelUpdate> implements Model {

    /** The match associated to the model */
    private Match match;

    ModelImpl(){
        //TODO: Finish the constructor
    }

    @Override
    public Match getMatch() {
        return match;
    }

    @Override
    public void setMatch(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }
        this.match = match;
    }
}
