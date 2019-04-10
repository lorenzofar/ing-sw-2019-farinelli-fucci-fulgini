package it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo;

/** Describes the possible costs in terms of ammo cube colors*/
public enum AmmoCubeCost{

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



}