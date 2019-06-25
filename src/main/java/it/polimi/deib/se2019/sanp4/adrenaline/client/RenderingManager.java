package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.*;

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
        clientView.getRenderer().refreshOwnedWeapons();
    }

    @Override
    public void handle(DamageUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(KillUpdate update) {
        // Refresh the player board of the killed player
        clientView.getRenderer().refreshPlayerBoard(update.getKilled());
    }

    @Override
    public void handle(LobbyUpdate update) {
        clientView.getRenderer().updateLobby(update.getWaitingPlayers(), update.isStarting());
    }

    @Override
    public void handle(OverkillUpdate update) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerMoveUpdate update) {
        // Refresh the game board to show the
        clientView.getRenderer().refreshGameBoard(update.getStart(), update.getEnd());
    }

    @Override
    public void handle(ReloadUpdate update) {
        clientView.getRenderer().refreshOwnedWeapons();
    }

    @Override
    public void handle(RemovedWeaponUpdate update) {
        clientView.getRenderer().refreshOwnedWeapons();
    }

    @Override
    public void handle(PlayerUpdate update) {
        clientView.getRenderer().refreshPlayerBoard(update.getPlayer().getName());
        // Also refresh the list of weapons (a player could have grabbed or discarded a weapon)
        clientView.getRenderer().refreshOwnedWeapons();
        // Then if the updated player is our user, we refresh the match info pane to update its score
        if (update.getPlayer().getName().equals(clientView.getUsername())) {
            clientView.getRenderer().refreshMatchInfo();
            clientView.getRenderer().refreshAmmoInfo();
        }
    }

    @Override
    public void handle(ActionCardUpdate update) {
        //First check whether the update refers to the user
        if (!update.getPlayer().equals(clientView.getUsername())) {
            return;
        }
        // If yes, we refresh its actions tracks
        clientView.getRenderer().refreshActionsTrack();
    }

    @Override
    public void handle(SquareUpdate update) {
        // First refresh the game board
        clientView.getRenderer().refreshGameBoard(update.getSquare().getLocation());
        // Then check whether the square is a spawn square and if yes refresh the spawn weapons
        if (clientView.getModelManager().getBoard().getSpawnPoints().containsValue(update.getSquare().getLocation())) {
            clientView.getRenderer().refreshSpawnWeapons();
        }
    }

    @Override
    public void handle(BoardUpdate update) {
        clientView.getRenderer().refreshGameBoard();
    }

    @Override
    public void handle(MatchUpdate update) {
        clientView.getRenderer().refreshKillshotsTrack();
        // We do not explicitly refresh player boards,
        // since when they turn to frenzy they fire an update themselves
    }

    @Override
    public void handle(PlayerBoardUpdate update) {
        clientView.getRenderer().refreshPlayerBoard(update.getPlayer());
    }

    @Override
    public void handle(DrawnPowerupUpdate update) {
        if (!update.getPlayer().equals(clientView.getUsername())) {
            return;
        }
        // TODO: Show drawn card to the user
    }

    @Override
    public void handle(DrawnWeaponUpdate update) {
        if (!update.getPlayer().equals(clientView.getUsername())) {
            return;
        }
        // TODO: Show drawn card to the user
    }

    @Override
    public void handle(InitialUpdate update) {
        clientView.getRenderer().showMatchScreen();
    }

    @Override
    public void handle(PlayerTurnUpdate update) {
        clientView.getRenderer().refreshMatchInfo();
    }

    @Override
    public void handle(WeaponCardUpdate update) {
        // TODO: Refresh the component showing the cards (if present)
    }

    @Override
    public void handle(MatchOperationalStateUpdate update) {
        //TODO: Implement this method
    }
}
