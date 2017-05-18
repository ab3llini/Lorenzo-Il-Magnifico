package server.controller.network;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

/**
 * Abstract definition of a server, either RMI or Socket
 */
public abstract class Server implements Observable<ServerObserver> {

    //The observer list
    protected ArrayList<ServerObserver> observers = new ArrayList<ServerObserver>() ;


    //Interface impl.
    public boolean addObserver(ServerObserver o) {

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
    public boolean removeObserver(ServerObserver o) {

        return this.observers.remove(o);

    }

    /**
     * Notify error
     */
    public final void notifyError() {

        for (ServerObserver o : this.observers) {

            o.onError(this);

        }

    }

    /**
     * Notify the connection
     * @param handler The handler
     */
    public final void notifyConnection(ClientHandler handler) {

        for (ServerObserver o : this.observers) {

            o.onConnection(this, handler);

        }

    }

    /**
     * Notify the disconnection
     * @param handler The handler
     */
    public final void notifyDisconnection(ClientHandler handler) {

        for (ServerObserver o : this.observers) {

            o.onDisconnection(this, handler);

        }

    }

    /**
     * Makes the request
     * @param username The username
     * @return True/False
     */
    public final boolean checkUsernameAvailable(String username) {

        for (ServerObserver o : this.observers) {

            if (!o.onRegistrationRequest(this, username)) {

                return false;

            }

        }

        return true;

    }

    public final void register (ClientHandler handler, String username) {

        handler.setUsername(username);

    }



}
