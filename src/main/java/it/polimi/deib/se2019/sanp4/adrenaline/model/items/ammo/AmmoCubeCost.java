package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;

/** Describes the possible costs in terms of ammo cube colors*/
public enum AmmoCubeCost implements ColoredObject {

    /** Red cube */
    RED("Red"),
    /** Yellow cube */
    YELLOW("Yellow"),
    /** Blue cube */
    BLUE("Blue"),
    /** Can be any of the cubes*/
    ANY("Any");

    private String message;

    AmmoCubeCost(String message){
        this.message = message;
    }

    @Override
    public String toString(){
        return this.message;
    }


    /**
     * Determines whether the provided cube can suffice as a payment method
     * @param cube The object representing the cube, not null
     * @return {@code true} if the cube is sufficient, {@code false} otherwise
     */
    public boolean canPayFor(AmmoCubeCost cube){
        if(cube == null){
            throw new NullPointerException("Cube cost cannot be null");
        }
        return this == AmmoCubeCost.ANY || this == cube;
    }

    /**
     * Determines whether the provided cube can suffice as a payment method
     * @param cube The object representing the cube
     * @return {@code true} if the cube is sufficient, {@code false} otherwise
     */
    public boolean canPayFor(AmmoCube cube){
        if(cube == null){
            throw new NullPointerException("Cube cannot be null");
        }
        return this == AmmoCubeCost.ANY || this.toString().equals(cube.toString());
    }

    public AmmoCube getCorrespondingCube(){
        switch(this){
            case RED:
                return AmmoCube.RED;
            case BLUE:
                return AmmoCube.BLUE;
            case YELLOW:
                return AmmoCube.YELLOW;
            default:
                return null;
        }
    }

    @Override
    public String getAnsiCode() {
        AmmoCube correspondingCube = this.getCorrespondingCube();
        return correspondingCube != null ? correspondingCube.getAnsiCode() : "";
    }

    @Override
    public String getHexCode() {
        AmmoCube correspondingCube = this.getCorrespondingCube();
        return correspondingCube != null ? correspondingCube.getHexCode() : "";
    }
}