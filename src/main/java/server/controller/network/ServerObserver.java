package server.controller.network;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

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
    void onConnection(Server server, ClientHandler handler);

    /**
     * Event raised whenever a client disconnects
     * @param server The server
     * @param handler The handler
     */
    void onDisconnection(Server server, ClientHandler handler);

    /**
     * Event raised whenever the server tries to performRegistrationRequest a new client
     * @param server The server
     * @param username The username to performRegistrationRequest the new client with
     * @return
     */
    boolean onRegistrationRequest(Server server, String username);


}
