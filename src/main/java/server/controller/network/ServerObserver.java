package server.controller.network;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

import client.controller.network.Observer;
import client.controller.network.ObserverType;
import netobject.action.Action;

/**
 * This interface provide methods for server observers
 */
public interface ServerObserver {

    /**
     * Event raised whenever the server encounters an error
     * @param server The server
     */
    void onError(Server server);

    /**
     * Event raised whenever a client connects
     * @param server The server
     * @param handler The handler
     */
    void onAuthentication(Server server, ClientHandler handler);

    /**
     * Event raised whenever a client disconnects
     * @param server The server
     * @param handler The handler
     */
    void onDisconnection(Server server, ClientHandler handler);

    void onAction(Server server, ClientHandler handler, Action action);

    void onObserverReady(Server server, ClientHandler handler, ObserverType observerType);


}
