package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoomTest {

    @Test(expected = NullPointerException.class)
    public void createRoom_NullRoomColorProvided_ShouldThrowNullPointerException(){
        RoomColor color = null;
        Room room = new Room(color);
    }

    //TODO: test methods to add/remove squares

    //TODO: test getPlayers()

    @Test
    public void createRoom_ShouldReturnSameColor(){
        Square square = new AmmoSquare(new CoordPair(5,5));
        RoomColor color = RoomColor.BLUE;
        Room room = new Room(color);
        assertEquals(color, room.getColor());
    }
}