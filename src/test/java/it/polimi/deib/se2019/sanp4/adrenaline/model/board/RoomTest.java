package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class RoomTest {

    private static CoordPair validLocation = new CoordPair(1, 1);

    @Test(expected = NullPointerException.class)
    public void createRoom_NullRoomColorProvided_ShouldThrowNullPointerException(){
        new Room(null);
    }

    @Test
    public void createRoom_ShouldReturnSameColor(){
        RoomColor color = RoomColor.BLUE;
        Room room = new Room(color);
        assertEquals(color, room.getColor());
        assertNotNull(room.getColor().toString());
        assertNotNull(room.getColor().getAnsiCode());
        assertNotNull(room.getColor().getHexCode());
        assertEquals(0, room.getSquares().size());
        assertEquals(0, room.getPlayers().size());
    }

    @Test(expected = NullPointerException.class)
    public void addSquare_nullSquareProvided_shouldThrowNullPointerException(){
        Room room = new Room(RoomColor.BLUE);
        room.addSquare(null);
    }

    @Test
    public void addSquare_validSquareProvided_shouldNotThrowException(){
        Room room = new Room(RoomColor.BLUE);
        Square square = new AmmoSquare(validLocation);
        room.addSquare(square);
        assertTrue(room.getSquares().contains(square));
        assertEquals(room, square.getRoom());
    }

    @Test(expected = NullPointerException.class)
    public void removeSquare_nullSquareProvided_shouldThrowNullPointerException(){
        Room room = new Room(RoomColor.BLUE);
        room.removeSquare(null);
    }

    @Test
    public void removeSquare_squareNotBelongingToRoomProvided_roomSquaresShouldNotChange(){
        Room room = new Room(RoomColor.BLUE);
        Square square = new AmmoSquare(validLocation);
        Collection<Square> roomSquares = room.getSquares();
        room.removeSquare(square);
        assertTrue(roomSquares.containsAll(room.getSquares()));
        assertTrue(room.getSquares().containsAll(roomSquares));
    }

    @Test
    public void removeSquare_squareBelongsToRoom_squareShouldBeRemoved(){
        Room room = new Room(RoomColor.BLUE);
        Square square = new AmmoSquare(validLocation);
        room.addSquare(square);
        room.removeSquare(square);
        assertFalse(room.getSquares().contains(square));
        assertNull(square.getRoom());
    }
}