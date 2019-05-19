package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.RemoteRoutingObservable;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

public class ModelImpl extends RemoteRoutingObservable<ModelUpdate> implements Model {

    /** The match associated to the model */
    private Match match;

    /** The operational state of the match */
    private MatchOperationalState operationalState;

    /**
     * Creates an empty model.
     * The initial operational state is {@link MatchOperationalState#WAITING_PLAYERS}
     */
    ModelImpl(){
        //TODO: Finish the constructor
        operationalState = MatchOperationalState.WAITING_PLAYERS;
    }

    /**
     * Returns match instance associated to this model.
     *
     * @return match instance
     */
    @Override
    public Match getMatch() {
        return match;
    }

    /**
     * Sets the current match to given match
     * @param match match instance
     */
    void setMatch(Match match) {
        if(match == null){
            throw new NullPointerException("Match cannot be null");
        }
        this.match = match;
    }

    /**
     * Returns the operational state of the match
     *
     * @return the operational state of the match
     */
    @Override
    public MatchOperationalState getOperationalState() {
        return operationalState;
    }
}
