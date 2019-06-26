package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.LobbyUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.logging.LogManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LobbyTest {

    @BeforeClass
    public static void classSetup() {
        /* Disable logging */
        LogManager.getLogManager().reset();
    }

    @Test
    public void insertPlayer_shouldBeInsertedInQueue() {
        Lobby lobby = new Lobby();
        RemoteView remote = mock(RemoteView.class);

        lobby.insertPlayer("name", remote);

        Map.Entry<String, RemoteView> entry = new AbstractMap.SimpleEntry<>("name", remote);
        assertTrue(lobby.getIncomingPlayers().contains(entry));
    }

    @Test
    public void receiveIncomingPlayer_firstPlayer_shouldNotStartTimer() throws IOException {
        Lobby lobby = new Lobby();

        RemoteView remote = mock(RemoteView.class);
        Map.Entry<String, RemoteView> entry = new AbstractMap.SimpleEntry<>("name", remote);

        lobby.receiveIncomingPlayer(entry);

        /* Check that the player is in the waiting list */
        RemoteView waiting = lobby.getWaitingPlayers().get("name");
        assertEquals(remote, waiting);

        /* Check that the scene LOBBY has been selected */
        verify(remote).selectScene(ViewScene.LOBBY);

        /* Verify that it received the update with the list of players */
        verify(remote).update(any(LobbyUpdate.class));

        /* Verify that the timer is running */
        assertFalse(lobby.isTimerRunning());
    }



    @Test
    public void receiveIncomingPlayers_atLeastMinPlayers_shouldStartTimer() {
        Lobby lobby = new Lobby();

        /* Insert enough players */
        for (int i = 0; i < lobby.getMinPlayers(); i++) {
            lobby.receiveIncomingPlayer(new AbstractMap.SimpleEntry<>(Integer.toString(i), mock(RemoteView.class)));
        }

        assertTrue(lobby.isTimerRunning());
    }

    @Test
    public void receiveIncomingPlayers_maxPlayers_shouldEmptyWaitingList() {
        Lobby lobby = new Lobby();

        /* Insert enough players */
        for (int i = 0; i < lobby.getMaxPlayers(); i++) {
            lobby.receiveIncomingPlayer(new AbstractMap.SimpleEntry<>(Integer.toString(i), mock(RemoteView.class)));
        }

        assertFalse(lobby.isTimerRunning());
        assertTrue(lobby.getWaitingPlayers().isEmpty());
    }

    @Test
    public void timerCallback_enoughPlayersToStart_shouldEmptyWaitingList() {
        Lobby lobby = new Lobby();

        /* Insert enough players */
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        for (int i = 0; i < lobby.getMinPlayers(); i++) {
            waitingList.put(Integer.toString(i), mock(RemoteView.class));
        }

        lobby.timerCallback();

        assertTrue(waitingList.isEmpty());
        assertFalse(lobby.isTimerRunning());
    }

    @Test
    public void timerCallback_notEnoughPlayers_shouldPreserveWaitingList() {
        Lobby lobby = new Lobby();

        /* Insert one player */
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        waitingList.put("name", mock(RemoteView.class));

        lobby.timerCallback();

        assertTrue(waitingList.containsKey("name"));
        assertFalse(lobby.isTimerRunning());
    }

    @Test
    public void disconnectPlayer_shouldRemoveFromWaitingList() {
        Lobby lobby = new Lobby();
        RemoteView faulty = mock(RemoteView.class);
        RemoteView other = mock(RemoteView.class);

        /* Add some players to the waiting list */
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        waitingList.put("faulty", faulty);
        waitingList.put("other", other);

        /* Faulty is the player who will be disconnected */
        lobby.disconnectPlayer("faulty");

        /* Check that he's not in the waiting list anymore */
        assertFalse(waitingList.containsKey("faulty"));
    }

    @Test
    public void disconnectInactive_shouldOnlyDisconnectInactive() throws IOException {
        Lobby lobby = new Lobby();
        RemoteView faulty = mock(RemoteView.class);
        RemoteView other = mock(RemoteView.class);

        /* Add some players to the waiting list */
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        waitingList.put("faulty", faulty);
        waitingList.put("other", other);

        /* Faulty throws when pinged, other doesn't */
        doThrow(new IOException()).when(faulty).ping();

        lobby.disconnectInactive();

        /* Check that faulty has been removed from the waiting list, but not other */
        assertFalse(waitingList.containsKey("faulty"));
        assertTrue(waitingList.containsKey("other"));
    }

    @Test
    public void selectLobbyScene_shouldIgnoreDisconnected() throws IOException {
        Lobby lobby = new Lobby();
        RemoteView faulty = mock(RemoteView.class);
        doThrow(new IOException()).when(faulty).selectScene(any());

        /* Add the player to the waiting list */
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        waitingList.put("faulty", faulty);

        lobby.selectLobbyScene(faulty);

        /* Check that the player is still in the waiting list */
        assertTrue(waitingList.containsKey("faulty"));
    }

    @Test
    public void notifyWaitingList_shouldIgnoreDisconnected() throws IOException {
        Lobby lobby = new Lobby();
        RemoteView faulty = mock(RemoteView.class);
        doThrow(new IOException()).when(faulty).update(any());

        /* Add the player to the waiting list */
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        waitingList.put("faulty", faulty);

        lobby.notifyWaitingList(false);

        /* Check that the player is still in the waiting list */
        assertTrue(waitingList.containsKey("faulty"));
    }

    @Test
    public void shutdown_shouldEmptyWaitingListAndNotify() throws IOException {
        Lobby lobby = new Lobby();

        /* Artificially create a waiting list */
        RemoteView remote = mock(RemoteView.class);
        Map<String, RemoteView> waitingList = lobby.getWaitingPlayers();
        waitingList.put("name", remote);

        lobby.shutdown();

        /* Check that the waiting list has been emptied and that the player has been updated */
        verify(remote).update(any(LobbyUpdate.class));
        assertTrue(lobby.getWaitingPlayers().isEmpty());
    }
}