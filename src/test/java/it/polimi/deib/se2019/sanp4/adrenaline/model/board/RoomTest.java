package it.polimi.deib.se2019.sanp4.adrenaline.model.board;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import org.junit.Test;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RoomTest {

    @Test(expected = NullPointerException.class)
    public void createRoom_NullRoomColorProvided_ShouldThrowNullPointerException(){
        List<Square> squares = null;
        RoomColor color = null;
        Room room = new Room(squares, color);
    }

    @Test(expected = NullPointerException.class)
    public void createRoom_NullSquareListProvided_ShouldThrowNullPointerException(){
        List<Square> squares = null;
        RoomColor color = RoomColor.BLUE;
        Room room = new Room(squares, color);
    }

    //TODO: test getPlayers()

    @Test
    public void createRoom_ShouldReturnSameColor(){
        CoordPair coordPair = new CoordPair(5,5);
        Square square = new Square(coordPair);
        List<Square> squares = new ArrayList<Square>();
        squares.add(square);
        RoomColor color = RoomColor.BLUE;
        Room room = new Room(squares, color);
        assertEquals(color, room.getColor());
    }




}