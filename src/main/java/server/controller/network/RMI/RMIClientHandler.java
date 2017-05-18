package server.controller.network.RMI;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;
import server.controller.network.ClientHandler;
import server.controller.network.ClientHandlerObserver;
import server.controller.network.Observable;

import java.util.ArrayList;

public class RMIClientHandler extends ClientHandler implements Observable<ClientHandlerObserver> {

    private  RMIClientInterface clientRef;

    //The observer list
    protected ArrayList<ClientHandlerObserver> observers;

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

    //Interface impl.
    public boolean addObserver(ClientHandlerObserver o) {

        //Check whether the observer is not null
        if (o != null) {
            this.observers.add(o);
            return true;
        }
        else {
            return false;
        }
    }

    //Interface impl.
    public boolean removeObserver(ClientHandlerObserver o) {

        return this.observers.remove(o);

    }


    public final void notifyDisconnection() {

        for (ClientHandlerObserver o : this.observers) {

            o.onDisconnect(this);

        }

    }

    /**
     * Getters and setters
     */
    public RMIClientInterface getClientRef() {
        return clientRef;
    }
}
