package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.model.items.effects.Effect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.Weapon;

import java.util.List;

public class ShootCommand {
    private List<Player> targets;
    private List<Effect> effects;
    private Weapon usedWeapon;

    public Weapon getWeapon() {
        return usedWeapon;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
