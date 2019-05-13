package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.common.events.GrabEvent;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.choice.GrabWeaponChoiceHandler;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.request.WeaponCardRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.AmmoSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Square;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.List;

public class GrabHandler implements EventHandler{

    // Private constructor to hide public one
    private GrabHandler(){}

    public static void handle(GrabEvent event, Controller controller){
        // First we determine who is the player that is trying to grab something
        Player player = controller.getModel().getMatch().getPlayerByName(event.getSender());

        if(player == null){
            throw new IllegalStateException("Player does not exist");
        }

        // Then we determine on which kind of square he is on
        Square playerSquare = player.getCurrentSquare();

        //TODO: Maybe ask if the player wants to move?

        if(playerSquare instanceof AmmoSquare){
            // Here we just take the ammo card
            AmmoCard ammoCard = ((AmmoSquare) playerSquare).getAmmoCard();
            // And add the ammo to the player
            player.addAmmo(ammoCard.getCubes());
        }
        else if(playerSquare instanceof SpawnSquare){
            // Here the player can grab one of the weapons
            // Hence we have to retrieve the list of available weapons and show it to the player
            List<WeaponCard> availableWeaponCards = ((SpawnSquare)playerSquare).getWeaponCards();
            Request<WeaponCard> weaponCardRequest = new WeaponCardRequest(
                    "Select a weapon card to grab from the square",
                    availableWeaponCards,
                    false
            );
            // Create a new request context to store the request, and make it handle by the proper handler
            RequestContext<WeaponCard> weaponCardRequestContext = new RequestContext<>(weaponCardRequest, new GrabWeaponChoiceHandler());
            // Send the request to the player
            controller.sendRequest(player, weaponCardRequestContext);
        }

    }

}