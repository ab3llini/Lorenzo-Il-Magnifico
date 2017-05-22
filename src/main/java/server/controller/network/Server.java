package server.controller.network;

import netobject.LoginAuthentication;
import netobject.NetObject;
import netobject.RegisterAuthentication;
import server.model.board.Period;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

/**
 * Abstract definition of a server, either RMI or Socket
 */
public abstract class Server implements Observable<ServerObserver> {

    //The observer list
    protected ArrayList<ServerObserver> observers = new ArrayList<ServerObserver>();

    //The client handlers
    protected HashMap<ClientHandler, Thread> clientHandlers = new HashMap<ClientHandler, Thread>();;


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
    protected final void notifyError() {

        for (ServerObserver o : this.observers) {

            o.onError(this);

        }

    }

    /**
     * Notify the authentication of a client
     * @param handler The handler
     */
    private final synchronized void notifyAuthentication(ClientHandler handler) {

        for (ServerObserver o : this.observers) {

            o.onAuthentication(this, handler);

        }

    }

    /**
     * Notify the disconnection of an authenticated client only
     * @param handler The handler
     */
    protected final synchronized void notifyDisconnection(ClientHandler handler) {

        if (!handler.isAuthenticated()) {

            return;

        }

        for (ServerObserver o : this.observers) {

            o.onDisconnection(this, handler);

        }

    }

    /**
     * Removes a client and terminates his thread
     * @param handler the handler to be removed
     */
    protected synchronized void removeClientHandler(ClientHandler handler) {

        //Terminate the thread
        this.clientHandlers.get(handler).interrupt();

        //Remove the reference
        this.clientHandlers.remove(handler);

    }

    /**
     * Adds a client handler to the map.
     * Takes care of giving a thread and running it
     * @param handler the handler to add
     */
    protected synchronized void addClientHandler(ClientHandler handler) {

        //Prepare a dedicated thread for the client.
        Thread thread = new Thread(handler);

        //Map the client
        clientHandlers.put(handler, thread);

        //Start the handler
        thread.start();

    }


    protected final synchronized boolean authenticate(ClientHandler handler, NetObject authentication) {

        //Login or register
        if (authentication instanceof LoginAuthentication) {

            LoginAuthentication login = (LoginAuthentication)authentication;

            //Perform login with postgre sql server
            //Check if username & password are correct

            //Assign username & session to handler
            handler.setUsername(login.getUsername());
            handler.setAuthenticated(true);

            //Notify the authentication
            this.notifyAuthentication(handler);

            return true;


        }
        else if (authentication instanceof RegisterAuthentication) {

            //Perform registration

        }

        return false;

    }


}
