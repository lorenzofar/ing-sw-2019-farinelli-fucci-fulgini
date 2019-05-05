package it.polimi.deib.se2019.sanp4.adrenaline.common.network;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;

import java.io.IOException;
import java.rmi.Remote;

/** Describes a server based on RMI for communication */
public interface RemoteServer extends Remote {
    //TODO: Finish adding methods

    /**
     * Request to log in a user.
     * The client tries to log in with the required username and also passes a stub of his view.
     * The cases are:
     * <ol>
     *     <li>The player is completely unknown to the server => he will be put in the waiting room</li>
     *     <li>The player belongs to an active/suspended match => he will join the match</li>
     *     <li>
     *         There is a logged in player with the same username => the request will be rejected with
     *         {@link LoginException}
     *     </li>
     * </ol>
     * @param username name of the player who wants to get logged in
     * @param view stub of the view exported on the RMI registry
     * @throws IOException if the remote call fails
     * @throws LoginException if the username is associated to an already logged in player
     */
    void playerLogin(String username, RemoteView view) throws IOException, LoginException;

    /**
     * Request to log out a user.
     * If the user is logged in, it gets logged out and all operations to suspend him are taken care of.
     * If it is not logged in, nothing happens.
     * @param username name of the player to be logged out
     * @throws IOException if the remote call fails
     */
    void playerLogout(String username) throws IOException;


    /**
     * Checks connectivity to the server
     * @throws IOException If there is no connectivity
     */
    void ping() throws IOException;
}
