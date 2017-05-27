package client.controller.network;

import netobject.notification.Notification;
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

    protected String username;

    protected boolean connected;

    protected boolean authenticated;

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


    public String getUsername() {
        return username;
    }

    public boolean hasConnected() {
        return this.connected;
    }

    public boolean hasAuthenticated() {
        return this.authenticated;
    }

    /**
     * Notify that the server closed the connection
     */
    protected void notifyDisconnection() {

        for (ClientObserver o : this.observers) {

            o.onDisconnection(this);

        }

    }

    protected void notifyLoginFailed(String reason) {

        for (ClientObserver o : this.observers) {

            o.onLoginFailed(this, reason);

        }

    }

    protected void notifyLoginSucceeded() {

        for (ClientObserver o : this.observers) {

            o.onLoginSuccess(this);

        }

    }

    protected void notifyNotificationReceived(Notification not) {

        for (ClientObserver o : this.observers) {

            o.onNotification(this, not);

        }

    }

    public boolean addObserver(ClientObserver o) {

        return o != null && this.observers.add(o);

    }

    public boolean removeObserver(ClientObserver o) {

        return o != null && this.observers.remove(o);

    }


}
