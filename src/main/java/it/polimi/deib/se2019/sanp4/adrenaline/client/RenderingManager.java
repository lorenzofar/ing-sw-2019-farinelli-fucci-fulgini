package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

/**
 * A class implementing the ModelUpdateVisitor interface that is responsible of updating the rendered game screen according to the received update
 */
public class RenderingManager implements ModelUpdateVisitor {

    /**
     * The renderer used to render the game
     */
    private ClientView clientView;

    RenderingManager(ClientView clientView) {
        this.clientView = clientView;
    }

    @Override
    public void handle(AddedWeaponUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshOwnedWeapons();
        }
    }

    @Override
    public void handle(DamageUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshPlayerBoard(update.getShot());
        }
    }

    @Override
    public void handle(KillUpdate update) {
        // Refresh the player board of the killed player
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshPlayerBoard(update.getKilled());
        }
    }

    @Override
    public void handle(LobbyUpdate update) {
        if (clientView.getScene() == ViewScene.LOBBY) {
            clientView.getRenderer().updateLobby(update.getWaitingPlayers(), update.isStarting());
        }
    }

    @Override
    public void handle(OverkillUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshPlayerBoard(update.getKilled());
        }
    }

    @Override
    public void handle(PlayerMoveUpdate update) {
        if (clientView.getScene().isGameScene()) {
            // Refresh the game board to show the
            clientView.getRenderer().refreshGameBoard(update.getStart(), update.getEnd());
        }
    }

    @Override
    public void handle(ReloadUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshOwnedWeapons();
        }
    }

    @Override
    public void handle(RemovedWeaponUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshOwnedWeapons();
        }
    }

    @Override
    public void handle(PlayerUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshPlayerBoard(update.getPlayer().getName());
            // Also refresh the list of weapons (a player could have grabbed or discarded a weapon)
            clientView.getRenderer().refreshOwnedWeapons();
            // Then if the updated player is our user, we refresh the match info pane to update its score
            if (update.getPlayer().getName().equals(clientView.getUsername())) {
                clientView.getRenderer().refreshMatchInfo();
                clientView.getRenderer().refreshAmmoInfo();
            }
        }
    }

    @Override
    public void handle(ActionCardUpdate update) {
        if (clientView.getScene().isGameScene()) {
            //First check whether the update refers to the user
            if (!update.getPlayer().equals(clientView.getUsername())) {
                return;
            }
        }
        // TODO: Understand what to render, since the action card is not explicitly shown
    }

    @Override
    public void handle(SquareUpdate update) {
        if (clientView.getScene().isGameScene()) {
            // First refresh the game board
            clientView.getRenderer().refreshGameBoard(update.getSquare().getLocation());
            // Then check whether the square is a spawn square and if yes refresh the spawn weapons
            if (clientView.getModelManager().getBoard().getSpawnPoints().containsValue(update.getSquare().getLocation())) {
                clientView.getRenderer().refreshSpawnWeapons();
            }
        }
    }

    @Override
    public void handle(BoardUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshGameBoard();
        }
    }

    @Override
    public void handle(MatchUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshKillshotsTrack();
        }
        // We do not explicitly refresh player boards,
        // since when they turn to frenzy they fire an update themselves
    }

    @Override
    public void handle(PlayerBoardUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshPlayerBoard(update.getPlayer());
        }
    }

    @Override
    public void handle(DrawnPowerupUpdate update) {
        if (clientView.getScene().isGameScene()) {
            if (!update.getPlayer().equals(clientView.getUsername())) {
                return;
            }
            clientView.getRenderer().showDrawnPowerup(update.getPowerupCard());
        }
    }

    @Override
    public void handle(DrawnWeaponUpdate update) {
        if (clientView.getScene().isGameScene()) {
            if (!update.getPlayer().equals(clientView.getUsername())) {
                return;
            }
            clientView.getRenderer().showDrawnWeapon(update.getWeaponCard());
        }
    }

    @Override
    public void handle(InitialUpdate update) {
        if (clientView.getScene().isGameScene() || clientView.getScene() == ViewScene.LOBBY) {
            clientView.getRenderer().showMatchScreen();
        }
    }

    @Override
    public void handle(PlayerTurnUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshMatchInfo();
        }
    }

    @Override
    public void handle(WeaponCardUpdate update) {
        if (clientView.getScene().isGameScene()) {
            clientView.getRenderer().refreshOwnedWeapons();
            //TODO: Check whether to refresh just this particular weapon
        }
    }

    @Override
    public void handle(MatchOperationalStateUpdate update) {
        if (clientView.getScene().isGameScene()) {
            //TODO: Implement this method
        }
    }
}
