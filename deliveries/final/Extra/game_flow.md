## Game flow description
The game flow is controlled by the `MatchController` and the `TurnController`.
Take in mind that this description only gives an overview of the game flow,
but may lack some details of the actual implementation.

### Turn preparation
1. The board is prepared for the turn, by calling `Match.refillBoard()`.

2. Then a call to `MatchController.selectNextTurn()` will:
 - determine the next player (not suspended) and create its turn
 - the state of the turn is `INITIAL_SPAWN` if the player has not spawned
 yet (this is his first turn) or `SELECTING` if he is already on a square

### Turn execution

3. If the state of the turn is `INITIAL_SPAWN` the controller will draw two
powerups from the deck and ask the player to choose one for is spawn point, then
he is spawned and the state turn is set to `SELECTING` (by the controller)
and (the controller) starts the timer to wait for user selection

4. Now the player can either slect to perform an action, use a powerup or
end the turn. The actions are determined by a call to
`PlayerTurn.getAvailableActions()`.

5. When the user has performed his final action, or explicitly ended his turn,
the turn state is set to `OVER`.

### Reconnection
6. After the turn has been executed, players waiting to rejoin the match
will rejoin.
7. Then a call to `MatchController.checkIfMatchIsFinished()` will set a flag
which says if this is the last turn.

### Post-turn procedure

8. This procedure is started by calling `Match.endCurrentTurn()`, it performs
the following actions:
 - score the boards of dead players (which includes updating the killshot track)
 - reset the player boards and action cards of the players
 - set up frenzy mode, if necessary: assign new action cards, flip
 player boards, set the last player
 - if it detects that the match is over, it calls `MatchController.endMatch()`,
 else it proceeds with (9) and (10).

9. The controller will detect dead players and ask them to respawn

10. We turn back to point (1) and prepare for the next turn

### End of the match

11. The match is always ended by calling `MatchController.endMatch()`, which
performs the final scoring, generates the leaderboard and sends it as an update
to the players.

The match can end for 3 reasons:

1. **Normal ending:** the final player performs his turn and it is detected
by `MatchController.checkIfMatchIsFinished()`.
2. **End for lack of players:** `MatchController.checkIfMatchIsFinished()`
detects that there are too few players to continue.
3. **Disconnected final player**: the final player has disconnected before
completing the last round, so `MatchController.selectNextTurn()` goes through
the players and detects that he's the final player but he cannot play;
so it stops iterating and sets the match as finished.
