package server.controller.network.Socket;

import netobject.NetObject;
import server.controller.network.ClientHandlerObserver;

/*
 * @author  ab3llini
 * @since   17/05/17.
 */
public interface SocketClientHandlerObserver extends ClientHandlerObserver {

    /**
     * Event raised whenever a client performs an Action
     * @param handler The handler for the client
     * @param object The net object received
     */
    void onObjectReceived(SocketClientHandler handler, NetObject object);


}
