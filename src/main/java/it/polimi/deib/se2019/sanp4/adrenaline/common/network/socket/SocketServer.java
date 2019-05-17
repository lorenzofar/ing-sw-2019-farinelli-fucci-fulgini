package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.network.RemoteView;

/** Describes the interface of the server for a view stub communicating with Socket */
public interface SocketServer {
    /**
     * Request to log in a user.
     * The client tries to log in with the required username and also passes a stub of his view.
     * The cases are:
     * <ol>
     *     <li>The player is completely unknown to the server =&gt; he will be put in the waiting room</li>
     *     <li>The player belongs to an active/suspended match =&gt; he will join the match</li>
     *     <li>
     *         There is a logged in player with the same username =&gt; the request will be rejected with
     *         {@link LoginException}
     *     </li>
     * </ol>
     * @param username name of the player who wants to get logged in
     * @param view stub of the view exported on the RMI registry
     * @throws LoginException if the username is associated to an already logged in player
     */
    void playerLogin(String username, RemoteView view) throws LoginException;

    /**
     * Request to log out a user.
     * If the user is logged in, it gets logged out and all operations to suspend him are taken care of.
     * If it is not logged in, nothing happens.
     * @param username name of the player to be logged out
     */
    void playerLogout(String username);
}
