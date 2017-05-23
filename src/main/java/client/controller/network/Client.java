package client.controller.network;

import netobject.request.auth.LoginRequest;
import netobject.NetObject;
import server.controller.network.Observable;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */

/**
 * The class describes a generic client and provides some basic functionality
 */
public abstract class Client implements Observable<ClientObserver> {

    //The list of observers
    protected ArrayList<ClientObserver> observers = new ArrayList<ClientObserver>();

    /**
     * Method that should be implemented to send an object to the server
     * @param object the object to be sent
     * @return true upon success, false otherwise.
     */
    public abstract boolean sendObject(NetObject object);

    /**
     * Method that should be implemented to connect to the remote server
     * @return true upon success, false otherwise.
     */
    public abstract boolean connect();

    /**
     * Method to attempt a login.
     * Returns void, login  success is event based
     * @param authentication
     */
    public abstract void login(LoginRequest authentication);

    /**
     * Notify that an object has been received
     * @param object
     */
    protected void notifyObjectReceived(NetObject object) {

        for (ClientObserver o : this.observers) {

            o.onObjectReceived(this,object);

        }

    }

    /**
     * Notify that the server closed the connection
     */
    protected void notifyDisconnection() {

        for (ClientObserver o : this.observers) {

            o.onDisconnection(this);

        }

    }

    public boolean addObserver(ClientObserver o) {

        return o != null && this.observers.add(o);

    }

    public boolean removeObserver(ClientObserver o) {

        return o != null && this.observers.remove(o);

    }


}
