package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerState;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

/**
 * A class implementing the {@link ModelUpdateVisitor} interface that is responsible of updating the rendered game screen according to the received update
 *
 * @author Lorenzo Farinelli
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
    public void handle(LobbyUpdate update) {
        if (clientView.getScene() == ViewScene.LOBBY) {
            clientView.getRenderer().updateLobby(update.getWaitingPlayers(), update.isStarting());
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
                clientView.getRenderer().refreshOwnedPowerups();
                if (update.getPlayer().getState().equals(PlayerState.SUSPENDED)) {
                    clientView.getRenderer().showPreemptionScreen(
                            "Suspended",
                            "You have been suspended from the match, to rejoin open the client and log in with the same username");
                }
            }
        }
    }

    @Override
    public void handle(ActionCardUpdate update) {
        // We do not refresh anything, since the information is not shown
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
        }
    }

    @Override
    public void handle(MatchOperationalStateUpdate update) {
        // We do not refresh anything, since this information is not shown
    }

    @Override
    public void handle(LeaderboardUpdate update) {
        if (clientView.getScene().equals(ViewScene.FINAL_SCORES)) {
            clientView.getRenderer().updateLeaderBoard(update.getLeaderboard());
        }
    }
}
