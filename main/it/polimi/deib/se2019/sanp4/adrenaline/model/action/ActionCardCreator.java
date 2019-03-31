package it.polimi.deib.se2019.sanp4.adrenaline.model.action;

public interface ActionCardCreator {
    public ActionCard createRegularActionCard();

    public ActionCard createFrenzyActionCardBeforeFirst();

    public ActionCard createFrenzyActionCardAfterFirst();
}
