package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.BoardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.CardNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.Board;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.BoardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerUpCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper static class used by {@code ModelImpl} to create a new {@link Match}
 * This class uses
 * <ul>
 *     <li>{@link BoardCreator}</li>
 *     <li>{@link ActionCardCreator}</li>
 *     <li>{@link AmmoCardCreator}</li>
 *     <li>{@link PowerupCreator}</li>
 *     <li>{@link WeaponCreator}</li>
 * </ul>
 * Using the methods in this class without loading JSON resources in these creators first
 * might lead to unexpected behavior (e.g. empty card stacks)
 */
public class MatchCreator {

    /** The class is static, cannot be instantiated */
    private  MatchCreator() {}

    /**
     * Creates a new match in its initial state, according to provided configuration
     * <p>
     * This method uses the following creators to get needed resources
     * <ul>
     *     <li>{@link BoardCreator}</li>
     *     <li>{@link ActionCardCreator}</li>
     *     <li>{@link AmmoCardCreator}</li>
     *     <li>{@link PowerupCreator}</li>
     *     <li>{@link WeaponCreator}</li>
     * </ul>
     * Make sure they have loaded their data from file before calling this method,
     * because it may lead to unexpected behavior, such as empty card stacks.
     * </p>
     * <p>
     *     This method does not select the first player and does not fill
     *     the board with ammo and weapons, this has to be done later
     * </p>
     *
     * If the match could not be created for some reason, returns null
     * @param usernames set with the names of the players who will play in the match
     * @param configuration configuration of the match, usually obtained by first player,
     *                      which is supposed to be valid
     * @return the created match if creation was successful, null if unable to create it
     * @throws NullPointerException if null parameters are provided
     * @throws IllegalArgumentException if there are too many players (more than the colors),
     *                                  if the provided skulls are negative
     *                                  if the board id provided is invalid
     * @throws IllegalStateException if the regular action card cannot be obtained by {@link ActionCardCreator}
     * @throws UncheckedIOException if a weapon card that was correctly loaded on startup
     * cannot be loaded from file anymore
     */
    public static Match createMatch(Set<String> usernames, MatchConfiguration configuration) {
        /* Check parameters */
        if (usernames == null || configuration == null) {
            throw new NullPointerException("Found null parameters");
        }
        if (usernames.size() > PlayerColor.values().length) {
            throw new IllegalArgumentException(String.format("Too many players (%d)", usernames.size()));
        }

        /* Create the game board */
        Board board = createBoard(configuration.getBoardId());

        /* Create the list of players from usernames */
        List<Player> players = createPlayers(usernames);

        /* Now load the card stacks */
        CardStack<AmmoCard> ammoCardCardStack =
                new AutoShufflingStack<>(AmmoCardCreator.getAmmoCardDeck());
        CardStack<WeaponCard> weaponCardCardStack =
                new AutoShufflingStack<>(createWeaponCardsDeck());
        CardStack<PowerUpCard> powerUpCardCardStack =
                new AutoShufflingStack<>(PowerupCreator.createPowerupDeck());

        /* Finally create the match */
        Match match = new Match(configuration.getSkulls());
        match.setPlayers(players);
        match.setBoard(board);
        match.setAmmoStack(ammoCardCardStack);
        match.setPowerupStack(powerUpCardCardStack);
        match.setWeaponStack(weaponCardCardStack);

        return match;
    }

    /* ===== PRIVATE METHODS ===== */

    private static Board createBoard(int boardId) {
        try {
            return BoardCreator.createBoard(boardId);
        } catch (BoardNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static ActionCard createPlayerActionCard() {
        try {
            return ActionCardCreator.createActionCard(ActionCardEnum.REGULAR);
        } catch (CardNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Player> createPlayers(Set<String> usernames) {
        /* Since the same action card is shared among all players, get it in advance */
        ActionCard actionCard = createPlayerActionCard();

        /* Create an iterator with the player colors */
        Iterator<PlayerColor> colors = Arrays.stream(PlayerColor.values()).iterator();

        /* Now create the players from the user names, the colors are assigned sequentially */
        /* The number of players has been checked in advance, so the iterator won't end */
        return usernames.stream()
                .map(username -> new Player(username, actionCard, colors.next()))
                .collect(Collectors.toList());
    }

    private static Collection<WeaponCard> createWeaponCardsDeck() {
        try {
            return WeaponCreator.createWeaponCardDeck();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
