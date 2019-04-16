package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerCharacterTest {

    private static String validName = "player1";
    private static String validDescription = "description1";
    private static RoomColor validColor = RoomColor.BLUE;

    @Test(expected = NullPointerException.class)
    public void createPlayerCharacter_nullNameProvided_shouldThrowNullPointerException(){
        new PlayerCharacter(null, validDescription, validColor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPlayerCharacter_emptyNameProvided_shouldThrowIllegalArgumentException(){
        new PlayerCharacter("", validDescription, validColor);
    }

    @Test(expected = NullPointerException.class)
    public void createPlayerCharacter_nullDescriptionProvided_shouldThrowNullPointerException(){
        new PlayerCharacter(validName, null, validColor);
    }

    @Test(expected = NullPointerException.class)
    public void createPlayerCharacter_nullColorProvided_shouldThrowNullPointerException(){
        new PlayerCharacter(validName, validDescription, null);
    }

    @Test
    public void createPlayerCharacter_validParametersProvided_shouldNotThrowException(){
        new PlayerCharacter(validName, validDescription, validColor);
    }

    @Test
    public void createPlayerCharacter_validParametersProvidedGetAttributes_shouldReturnProvidedAttributes(){
        PlayerCharacter pc = new PlayerCharacter(validName, validDescription, validColor);
        assertEquals(pc.getName(), validName);
        assertEquals(pc.getDescription(), validDescription);
        assertEquals(pc.getColor(), validColor);
    }
}
