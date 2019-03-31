package it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCube;

import java.util.List;

public abstract class Effect {
    private String id;
    private List<AmmoCube> cost; //TODO: Check whether to use a list or not
}
