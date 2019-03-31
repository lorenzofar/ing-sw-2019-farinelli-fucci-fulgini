package it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.CubeInterface;

import java.util.List;

public class EffectCommand {

    private List<CubeInterface> paidCubes;

    public boolean validateEffect(EffectContext context){
        return false;
    };

    public void deployEffect(){};

    public void payWithVube(CubeInterface cube){};

    public void askParameters(Object userInterface){
        //TODO: Change parameter type
    }
}
