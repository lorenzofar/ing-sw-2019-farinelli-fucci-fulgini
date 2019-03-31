package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;

public class PlayerCharacter{
    private String name;
    private String description;
    private RoomColor color;

    public PlayerCharacter(String name, String description, RoomColor color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
}