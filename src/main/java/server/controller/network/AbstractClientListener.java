package server.controller.network;

import netobject.Action;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */
public interface AbstractClientListener {

    /**
     * Event raised whenever a client disconnects
     * @param handler The handler for the client
     */
    void onDisconnect(AbstractClientHandler handler);

    /**
     * Event raised whenever a client performs an action
     * @param handler The handler for the client
     * @param action The action
     */
    void onAction(AbstractClientHandler handler, Action action);

    /**
     * A method to check whether a client with the same username, either RMI or Socket, has ever connected.
     * @param username The username to check
     * @return True or false
     */
    boolean existsClientWithUsername(String username);

}
