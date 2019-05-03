package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.PlayerNotFoundException;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerCharacter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class SquareTest {

    private static CoordPair validLocation = new CoordPair(1, 1);
    private static Player validPlayer;

    @BeforeClass
    public static void setup(){
        int validMaxActions = 2;
        String validName = "player1";
        Collection<ActionEnum> validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_GRAB);
        validActions.add(ActionEnum.ADRENALINE_SHOOT);
        ActionCardEnum validType = ActionCardEnum.ADRENALINE1;
        ActionEnum validFinalAction = ActionEnum.RELOAD;
        String validDescription = "description1";
        RoomColor validColor = RoomColor.BLUE;

        ActionCard validActionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);
        PlayerCharacter validCharacter = new PlayerCharacter(validName, validDescription, validColor);
        validPlayer = new Player(validName, validActionCard, validCharacter);
    }

    @Test(expected = NullPointerException.class)
    public void createSquare_nullLocationProvided_shouldThrowNullPointerException(){
        new AmmoSquare(null);
    }

    @Test
    public void createSquare_validLocationProvided_shouldNotThrowException(){
        Square square = new AmmoSquare(validLocation);
        assertEquals(validLocation, square.getLocation());
        assertEquals(0, square.getPlayers().size());
        assertNull(square.getRoom());
    }

    @Test(expected = NullPointerException.class)
    public void addPlayer_nullPlayerProvided_shouldThrowNullPointerException(){
        Square square = new AmmoSquare(validLocation);
        square.addPlayer(null);
    }

    @Test
    public void addPlayer_validPlayerProvided_playerShouldBeAdded(){
        Square square = new AmmoSquare(validLocation);
        square.addPlayer(validPlayer);
        assertTrue(square.getPlayers().contains(validPlayer));

    }

    @Test(expected = NullPointerException.class)
    public void removePlayer_nullPlayerProvided_shouldThrowNullPointerException() throws PlayerNotFoundException {
        Square square = new AmmoSquare(validLocation);
        square.removePlayer(null);
    }

    @Test
    public void removePlayer_playerIsNotInSquare_shouldThrowPlayerNotFoundException(){
        Square square = new AmmoSquare(validLocation);
        try {
            square.removePlayer(validPlayer);
        } catch (PlayerNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void removePlayer_playerIsInSquare_shouldRemovePlayer() throws PlayerNotFoundException {
        Square square = new AmmoSquare(validLocation);
        square.addPlayer(validPlayer);
        square.removePlayer(validPlayer);
        assertFalse(square.getPlayers().contains(validPlayer));
    }

    @Test
    public void setRoom_roomShouldBeChanged(){
        Square square = new AmmoSquare(validLocation);
        Room room = new Room(RoomColor.BLUE);
        square.setRoom(room);
        assertEquals(room, square.getRoom());
    }

    @Test
    public void setRoom_nullRoomProvided_roomShouldBeChanged(){
        Square square = new AmmoSquare(validLocation);
        square.setRoom(null);
        assertNull(square.getRoom());
    }
}
