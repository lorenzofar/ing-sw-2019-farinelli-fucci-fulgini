package it.polimi.deib.se2019.sanp4.adrenaline.controller.choice;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ChoiceHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.Controller;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/** A specialized choice handler to handle the choice of a weapon card to grab from the square the player is on */
public class GrabWeaponChoiceHandler implements ChoiceHandler<WeaponCard> {

    private static final Logger logger = Logger.getLogger(GrabWeaponChoiceHandler.class.getName());

    @Override
    public void handleChoice(WeaponCard weaponCard, String playerUsername, Controller controller) {

        if(weaponCard == null || controller == null){
            throw new NullPointerException("Found null parameters");
        }

        // When reaching this, the user has initiated a grab action while being on a spawn square.
        // It can hence choose among a list of weapons to grab from it.
        // Here the user has chosen one weapon and we then try to grab it and put in his hands

        // We get the object representing the player
        Player player = controller.getModel().getMatch().getPlayerByName(playerUsername);

        // And check whether it exists in the match
        if(player == null){
            throw new IllegalStateException("Player does not exist in the match");
        }

        // We get the square the player is on
        SpawnSquare playerSquare = (SpawnSquare)player.getCurrentSquare();
        // Then we try to grab it from the square
        WeaponCard grabbedWeaponCard = null;
        try {
            grabbedWeaponCard = playerSquare.grabWeaponCard(weaponCard.getId());
            player.addWeapon(grabbedWeaponCard);
        } catch (CardNotFoundException e) {
            logger.log(Level.FINE, "Player tried to grab non-existent weapon {0} from square", weaponCard.getId());
        } catch (FullCapacityException e) {
            // The player tried to grab an ammo but does not have enough room
            try {
                // We put the weapon back in the square
                playerSquare.insertWeaponCard(grabbedWeaponCard);
            } catch (FullCapacityException ex) {
                // This should never throw an exception, since we are putting back a card we previously removed
            }
            logger.log(Level.FINE, "Player tried to add a weapon card exceeding capacity");
        }
        //TODO: Finish implementing this method
    }

    @Override
    public void cancel() {
        //TODO: Implement this method
    }
}
