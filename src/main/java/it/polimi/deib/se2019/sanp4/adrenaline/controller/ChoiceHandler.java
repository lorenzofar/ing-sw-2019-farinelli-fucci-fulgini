package it.polimi.deib.se2019.sanp4.adrenaline.controller;

/**
 * Describes an object that can handle a choice made by a player
 * @param <T> The type of objects representing choices it can handle
  */
public interface ChoiceHandler<T> {

    /**
     * Handles the provided choice
     * @param choice The object representing the choice, not null
     */
    void handleChoice(T choice);

    /**
     * Handles failures in retrieving the state and stops waiting for the input
     */
    void cancel();
}
