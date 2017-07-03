package server.controller.network;

import client.controller.network.Observer;
import client.controller.network.ObserverType;
import exception.authentication.AlreadyLoggedInException;
import exception.authentication.AuthenticationException;
import exception.authentication.LoginFailedException;
import exception.authentication.RegistrationFailedException;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.request.Request;
import netobject.request.RequestType;
import netobject.request.auth.LoginRequest;
import netobject.NetObject;
import netobject.request.auth.RegisterRequest;
import server.controller.game.GameEngine;
import singleton.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    protected HashMap<ClientHandler, Thread> clientHandlers = new HashMap<ClientHandler, Thread>();

    //The game engine reference
    protected GameEngine gameEngine;

    /**
     * Every server must call super to initialize the game engine reference
     * @param gameEngine the game engine reference
     */
    protected Server(GameEngine gameEngine) {

        this.gameEngine = gameEngine;

    }


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
     * Notify the request of a client
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

        if (handler == null || this.clientHandlers.get(handler) == null) {

            Logger.log(Level.SEVERE, "Server", "Trying to remove a client handler that is either null or already removed");

            return;

        }

        //Terminate the thread
        this.clientHandlers.get(handler).interrupt();

        //Remove the reference
        this.clientHandlers.remove(handler);

        handler.setAuthenticated(false);


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

    protected final void notifyAction(ClientHandler handler, Action action) {

        if (!handler.isAuthenticated()) {

            return;

        }

        for (ServerObserver o : this.observers) {

            o.onAction(this, handler, action);

        }

    }

    protected final void notifyObserverReady(ClientHandler handler, ObserverType observerType) {

        if (!handler.isAuthenticated()) {

            return;

        }

        for (ServerObserver o : this.observers) {

            o.onObserverReady(this, handler, observerType);

        }

    }


    protected final synchronized boolean authenticate(ClientHandler handler, Request genericRequest) throws AuthenticationException {

        //Login or register
        if (genericRequest.getRequestType() == RequestType.Login) {

            LoginRequest request = (LoginRequest)genericRequest;

            //Check if there is a client already authenticated with the same username
            if (this.gameEngine.hasAlreadyAuthenticated(request.getUsername())) {

                throw new AlreadyLoggedInException("A client with username " + request.getUsername() + " has already logged in");

            }


            //Attempt to login
            if (Database.getInstance().login(request.getUsername(), request.getPassword())) {

                //Assign username & session to handler
                handler.setUsername(request.getUsername());
                handler.setAuthenticated(true);

                //Notify the request
                this.notifyAuthentication(handler);

                return true;

            } else {

                throw new LoginFailedException("Wrong username or password");

            }

        }

        if (genericRequest.getRequestType() == RequestType.Registration) {

            RegisterRequest request = (RegisterRequest) genericRequest;

            //Attempt to registration
            if (Database.getInstance().registration(request.getUsername(), request.getPassword())) {

                handler.setUsername(request.getUsername());
                handler.setAuthenticated(true);

                //Notify the request
                this.notifyAuthentication(handler);

                return true;

            } else {

                throw new RegistrationFailedException("A player with the name '"+ request.getUsername() +"' already exists!");

            }

        }
        else  {

            throw new AuthenticationException("Unknown authentication request");

        }



    }

    public boolean hasHandlerWithUsername(String username) {

        Iterator it = this.clientHandlers.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();

            if (((ClientHandler)pair.getKey()).getUsername().equals(username)) {

                return true;

            }
        }

        return false;

    }




}
