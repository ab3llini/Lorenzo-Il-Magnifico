package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;
import netobject.Action;

public class RMIClientHandler extends AbstractClientHandler {

    private  RMIClientInterface clientRef;

    /**
     * Constructor
     * @param clientRef The proxy reference to the client
     * @param username The username of the client
     */
    public RMIClientHandler(RMIClientInterface clientRef, String username) {

        //Assign the username
        this.username = username;
        //Assign the reference to the RMI Client in order to make callbacks
        this.clientRef = clientRef;

    }

    /**
     * Interface methods
     */
    public void notifyPlayerForAction(Action action, AbstractClientHandler sender) {

    }


    /**
     * Getters and setters
     */
    public RMIClientInterface getClientRef() {
        return clientRef;
    }
}
