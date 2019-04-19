package it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoadedState.class, name = "loaded"),
        @JsonSubTypes.Type(value = PickupState.class, name = "pickup"),
        @JsonSubTypes.Type(value = UnloadedState.class, name = "unloaded") })
public abstract class WeaponCardState {
    private String type;

    protected WeaponCardState(String type){
        this.type = type;
    }

    @JsonIgnore
    abstract boolean isUsable();

    abstract void reload(Player player, WeaponCard weapon);
    public void unload(WeaponCard weapon){
        weapon.setState(new UnloadedState());
    }
    public void reset(WeaponCard weapon){
        weapon.setState(new PickupState());
    }

    @Override
    public String toString(){
        return type;
    }
}