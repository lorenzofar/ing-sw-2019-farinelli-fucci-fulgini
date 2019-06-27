package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.AmmoSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.SquareUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.CardStack;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.EmptyStackException;
import java.util.Map;
import java.util.stream.Collectors;

/** A specialized class representing a square containing ammo cards */
public class AmmoSquare extends Square {

    /** The ammo card contained in the square */
    private AmmoCard ammoCard;

    /** Default constructor only to be used by Jackson */
    protected AmmoSquare(){
        super();
        this.ammoCard = null;
    }

    /**
     * Creates a new ammo square at the specified location
     * @param location The cartesian coordinates of the location
     */
    public AmmoSquare(CoordPair location){
        super(location);
        this.ammoCard = null;
    }

    /**
     * Takes the ammo card that is currently placed in the square
     * @return The object representing the ammo card
     * @throws IllegalStateException if there is no ammo on the square
     */
    public AmmoCard grabAmmo() {
        if(this.ammoCard != null) {
            AmmoCard picked = this.ammoCard;
            this.ammoCard = null;
            this.notifyObservers(new SquareUpdate(this.generateView()));
            return picked;
        } else {
            throw new IllegalStateException("Currently no ammo on this square");
        }
    }

    public AmmoCard getAmmoCard() {
        return ammoCard;
    }

    /**
     * Puts an ammo card on the square
     * @param ammo The object representing the ammo card, not null
     */
    public void insertAmmo(AmmoCard ammo){
        if(ammo == null){
            throw new NullPointerException("Ammo cannot be null");
        }
        this.ammoCard = ammo;
        this.notifyObservers(new SquareUpdate(this.generateView()));
    }

    /**
     * If there's is no ammo card in the square, draws an ammo card from the stack
     * and fills in the square.
     *
     * @param match instance of the match this square belongs to
     */
    @Override
    public void refill(Match match) {
        try {
            if (ammoCard == null) {
                /* Get the ammo card stack */
                CardStack<AmmoCard> stack = match.getAmmoStack();
                insertAmmo(stack.draw());
            }
        } catch (EmptyStackException e) {
            /* No more ammo cards to draw */
        }

        this.notifyObservers(new SquareUpdate(this.generateView()));
    }

    /**
     * Checks whether there is an ammo card in this square or not
     *
     * @return {@code true} if there is an ammo card, {@code false} if not
     */
    @Override
    public boolean isFull() {
        return ammoCard != null;
    }

    /**
     * Accepts to be visited by a {@link SquareVisitor}.
     *
     * @param visitor The visitor who wants to visit this square
     */
    @Override
    public void accept(SquareVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Generates the {@link AmmoSquareView} of the ammo square.
     * @return the ammo square view.
     */
    @Override
    public AmmoSquareView generateView() {
        /* Assign a random color if room is null in test cases */
        RoomColor color = getRoom() == null ? RoomColor.BLUE : getRoom().getColor();
        AmmoSquareView view = new AmmoSquareView(this.getLocation(), color);
        view.setPlayers(this.getPlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.toSet()));
        /* From AdjacentMap, create a Map in which the entry is the same and the value is the SquareConnectionType
        contained by the SquareConnection */
        Map<CardinalDirection, SquareConnectionType> adjacentMap =
                        getAdjacentSquares().entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getConnectionType()));
        view.setAdjacentMap(adjacentMap);
        view.setAmmoCard(ammoCard);
        return view;
    }
}
