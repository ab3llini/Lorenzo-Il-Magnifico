package server.controller.network;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */
public interface ClientHandlerObserver {

    /**
     * Event raised whenever a client disconnects
     * @param handler The handler for the client
     */
    void onDisconnect(ClientHandler handler);

}
