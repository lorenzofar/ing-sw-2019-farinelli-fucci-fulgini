package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.targets;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.AbstractWeapon;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.TargetingEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CancellationException;

import static it.polimi.deib.se2019.sanp4.adrenaline.model.board.VisibilityEnum.VISIBLE;

/**
 * Abstract class representing a target of a {@link TargetingEffect}.
 * <p>
 * It represents a player or a group of players (e.g. in the same room, square)
 * that receive the same amount of damage and marks.
 * </p>
 * <p>
 * Provides various attributes and methods which can be used by subclasses.
 * </p>
 * The filters which have a common meaning between subclasses are:
 * <ul>
 * <li><b>optional</b>: an optional target might be executed or not, this won't impact on the correct execution
 * of the effect</li>
 * <li><b>damage</b>: The amount of damage that will be received by all targeted players</li>
 * <li><b>marks</b>: The amount of marks that will be received by all targeted players</li>
 * </ul>
 */
public abstract class AbstractTarget {

    protected final AbstractWeapon weapon;

    protected final Match match;

    protected final ControllerFactory factory;

    protected PersistentView view;

    protected final String id;

    protected boolean optional;

    protected int damage;

    protected int marks;

    protected VisibilityEnum visibility;

    protected Integer minDist;

    protected Integer maxDist;

    protected Set<String> excludePlayers;

    protected Set<String> excludeSquares;

    /**
     * Creates a new abstract target associated to the given weapon.
     * While the id is set on initialization, the other properties receive their default values,
     * which can be changed later by using setters.
     *
     * @param id      The unique identifier of this target in its effect, not null
     * @param weapon  The weapon controller associated to this target, not null
     * @param match   The match which has to be controlled, not null
     * @param factory The factory to create needed controllers, not null
     */
    public AbstractTarget(String id, AbstractWeapon weapon, Match match, ControllerFactory factory) {
        this.id = id;
        this.weapon = weapon;
        this.match = match;
        this.factory = factory;

        /* Set defaults */
        this.optional = false; /* Mandatory */
        this.damage = this.marks = 0; /* No damages nor marks */
        this.visibility = VISIBLE; /* Only visible squares */
        this.minDist = this.maxDist = null; /* Minimum and maximum distance not checked */
        this.excludePlayers = Collections.emptySet(); /* Do not exclude players */
        this.excludeSquares = Collections.emptySet(); /* Do noy exclude squares */
    }

    /* ====================== GETTERS AND SETTERS ========================= */

    /**
     * Returns the identifier of this target
     *
     * @return The identifier of this target
     */
    public String getId() {
        return id;
    }

    /**
     * Returns whether this target is optional or not
     *
     * @return {@code true} if this target is optional, {@code false} otherwise
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Sets this weapon's optional attribute.
     * If set to {@code true}, then the user won't be obliged to shoot this
     * target while using the weapon.
     * Default value is {@code false}.
     *
     * @param optional The desired value of the attribute
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * Returns the number of damage tokens which will be applied to the targeted players.
     *
     * @return The number of damage tokens which will be applied to the targeted players.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Sets the number of damage tokens which will be applied to the targeted players.
     *
     * @param damage The number of damage tokens, non-negative
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Returns the number of marks which will be applied to the targeted players.
     *
     * @return The number of marks which will be applied to the targeted players.
     */
    public int getMarks() {
        return marks;
    }

    /**
     * Sets the number of damage tokens which will be applied to the targeted players.
     *
     * @param marks The number of damage tokens which will be applied to the targeted players.
     */
    public void setMarks(int marks) {
        this.marks = marks;
    }

    /**
     * Returns the visibility constraint applied for square selection
     *
     * @return The visibility constraint applied for square selection
     */
    public VisibilityEnum getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility constraint applied for square selection
     *
     * @param visibility The visibility constraint applied for square selection, not null
     */
    public void setVisibility(VisibilityEnum visibility) {
        this.visibility = visibility;
    }

    /**
     * Returns the minimum distance constraint for square selection
     *
     * @return The minimum distance, null if not set
     */
    public Integer getMinDist() {
        return minDist;
    }

    /**
     * Sets the minimum distance constraint for square selection
     *
     * @param minDist The minimum distance for square selection.
     *                A null value means no constraint, an integer value must be positive.
     */
    public void setMinDist(Integer minDist) {
        this.minDist = minDist;
    }

    /**
     * Returns the maximum distance constraint for square selection
     *
     * @return The maximum distance, null if not set
     */
    public Integer getMaxDist() {
        return maxDist;
    }

    /**
     * Sets the maximum distance constraint for square selection
     *
     * @param maxDist The maximum distance for square selection.
     *                A null value means no constraint, an integer value must be positive.
     */
    public void setMaxDist(Integer maxDist) {
        this.maxDist = maxDist;
    }

    /**
     * Returns the set of players which have to be excluded from selection for this target.
     * The players are represented by the ids of their targets in the current weapon.
     *
     * @return A set of ids representing the players to exclude
     */
    public Set<String> getExcludePlayers() {
        return excludePlayers;
    }

    /**
     * Sets the set of players which have to be excluded from selection for this target.
     * The players are represented by the ids of their targets in the current weapon.
     *
     * @param excludePlayers The set of ids representing the players to exclude, not null
     */
    public void setExcludePlayers(Set<String> excludePlayers) {
        this.excludePlayers = excludePlayers;
    }

    /**
     * Returns the set of squares which have to be excluded from selection for this target.
     * The squares are represented by the ids of their targets in the current weapon.
     *
     * @return A set of ids representing the squares to exclude
     */
    public Set<String> getExcludeSquares() {
        return excludeSquares;
    }

    /**
     * Sets the set of squares which have to be excluded from selection for this target.
     * The squares are represented by the ids of their targets in the current weapon.
     *
     * @param excludeSquares The set of ids representing the squares to exclude, not null
     */
    public void setExcludeSquares(Set<String> excludeSquares) {
        this.excludeSquares = excludeSquares;
    }

    /* ===================== UTILITIES ======================== */

    /**
     * Returns a set of players who can't be targeted for the following reasons:
     * explicitly excluded, or because he's the shooter.
     *
     * @param shooter The player using the weapon, not null
     * @return The set of players who can't be targeted
     */
    protected Set<Player> determineUntargetablePlayers(Player shooter) {
        Set<Player> set = weapon.getSavedPlayers(excludePlayers);
        set.add(shooter);
        return set;
    }


    /**
     * Applies the damages and marks to the selected target
     * and saves it among damaged players in the weapon.
     * Does not check if the target is targetable or not.
     *
     * @param target  The target player, not null
     * @param shooter The player using the weapon, not null
     */
    protected void applyDamageAndMarks(Player target, Player shooter) {
        /* Apply damage and marks */
        target.getPlayerBoard().addDamage(shooter, damage);
        target.getPlayerBoard().addMark(shooter, marks);

        /* Save in the weapon for later use */
        if (damage > 0) {
            weapon.addDamagedPlayer(target);
        }
    }

    /* ====================== EXECUTE ========================= */

    /**
     * Executes this target given the user of the weapon
     *
     * @param view The view of the player using the weapon
     * @return {@code true} if the player chose his target and was able to perform damage/marks
     * {@code false} if the player chose not to use the target
     * @throws CancellationException if a request to the user gets cancelled
     * @throws InterruptedException  if the thread gets interrupted
     */
    public abstract boolean execute(PersistentView view) throws InterruptedException;
}
