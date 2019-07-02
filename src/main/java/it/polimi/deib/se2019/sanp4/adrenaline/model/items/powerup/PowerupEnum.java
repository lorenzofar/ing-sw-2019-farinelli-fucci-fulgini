package it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup;

/**
 * Represents the types of powerup effects.
 * Each type has a textual name and description.
 */
public enum PowerupEnum {
    TARGETING_SCOPE("Targeting scope",
            "You may play this card when you are dealing damage to one or more targets. " +
                    "Pay 1 ammo cube of any color. Choose 1 of those targets and give it an extra point of damage. " +
                    "Note: You cannot use this to do 1 damage to a target that is receiving only marks."
    ),
    NEWTON("Newton",
            "You may play this card on your turn before or after any action. " +
                    "Choose any other player's figure and move it 1 or 2 squares in one direction. " +
                    "(You can't use this to move a figure after it respawns at the end of your turn. " +
                    "That would be too late.)"
    ),
    TAGBACK("Tagback grenade",
            "You may play this card when you receive damage from a player you can see. Give that player " +
                    "1 mark."
    ),
    TELEPORTER("Teleporter",
            "You may play this card on your turn before or after any action. " +
                    "Pick up your figure and set it down on any square of the board. " +
                    "(You can't use this after you see where someone respawns at the end of your turn. " +
                    "By then it is too late.)"
    );

    private final String name;
    private final String description;

    /**
     * Creates a new powerup type
     *
     * @param name        printable name of the powerup
     * @param description description of the powerup effect
     */
    PowerupEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the name of this powerup effect
     *
     * @return The name of the powerup effect
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of this powerup effect
     *
     * @return The description of the powerup effect
     */
    public String getDescription() {
        return description;
    }
}
