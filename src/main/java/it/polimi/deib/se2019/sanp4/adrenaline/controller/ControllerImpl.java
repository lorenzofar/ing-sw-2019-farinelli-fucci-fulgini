package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ViewEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.Model;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.HashMap;
import java.util.Map;

public class ControllerImpl implements Controller {

    /** Associated model instance */
    private Model model;

    /** A map of the view corresponding to each player */
    private Map<Player, RemoteView> views;

    /** The helper class to compute and assign scores */
    private ScoreManager scoreManager;

    public ControllerImpl(Model model) {
        this.model = model;
        this.views = new HashMap<>(); // Create a new empty map for the views
        this.scoreManager = new StandardScoreManager();
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void update(ViewEvent event) {
        //TODO: Implement this method
    }
}
