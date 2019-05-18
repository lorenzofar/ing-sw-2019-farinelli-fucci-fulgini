package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

/** A class describing the character of a player */
public class PlayerCharacter{
    /** The username of the player */
    private String name;

    /** A description of the character */
    private String description;

    /** The color of the character */
    private PlayerColor color;

    /**
     * Creates a new character for the specified player
     * @param name The username of the player, not null and not empty
     * @param description A description of the character, not null
     * @param color The object representing the color of the character, not null
     */
    public PlayerCharacter(String name, String description, PlayerColor color) {
        if(name == null || description == null || color == null){
            throw new NullPointerException("Found null parameters");
        }
        if(name.isEmpty()){
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        this.name = name;
        this.description = description;
        this.color = color;
    }

    /**
     * Retrieves the player associated to the character
     * @return The username of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the description of the character
     * @return The description of the character
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the color of the character
     * @return The object representing the color of the character
     */
    public PlayerColor getColor() {
        return color;
    }
}