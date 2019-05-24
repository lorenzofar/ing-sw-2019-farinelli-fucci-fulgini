package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

/**
 * An interface describing an object using the visitor pattern to handle model updates
 */
public interface ModelUpdateVisitor {
    void handle(AddedWeaponUpdate update);
    void handle(DamageUpdate update);
    void handle(KillUpdate update);
    void handle(LobbyUpdate update);
    void handle(OverkillUpdate update);
    void handle(PlayerMoveUpdate update);
    void handle(ReloadUpdate update);
    void handle(RemovedWeaponUpdate update);
    void handle(PlayerUpdate update);
    void handle(ActionCardUpdate update);
}
