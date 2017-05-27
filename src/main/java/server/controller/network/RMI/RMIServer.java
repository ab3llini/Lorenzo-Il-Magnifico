package server.controller.network.RMI;

/*
 * Created by alberto on 10/05/17.
 */

import client.controller.network.RMI.RMIClientInterface;
import exception.*;

import exception.authentication.*;
import netobject.request.action.ActionRequest;

import logger.*;
import netobject.request.auth.LoginRequest;
import netobject.request.auth.RegisterRequest;
import server.controller.game.GameEngine;
import server.controller.network.ClientHandlerObserver;
import server.controller.network.Server;
import server.controller.network.ClientHandler;
import server.utility.Security;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;


/**
 * The RMI Server.
 * Extends UnicastRemoteObject in order to be bound with the RMI registry
 * Implements RMIServerInterface to implement remote methods
 */
public class RMIServer extends Server implements RMIServerInterface, ClientHandlerObserver {

    //The RMI registry
    private transient Registry registry;

    //The port of the RMI registry
    private transient final int port;

    //The name to bind the remote object with
    private transient final String bindName;

    /**
     * Constructor
     * @param port the port to run the server on
     * @param bindName the name to bind the interface with
     */
    public RMIServer(int port, String bindName, GameEngine gameEngine) {

        //Initialize super class
        super(gameEngine);

        //Init port
        this.port = port;

        //Init bind name
        this.bindName = bindName;

    }

    /**
     * Starts the server
     * Takes care of registering the remote object into the registry.
     * Assigns the game engine to a local holder for further requests.
     */
    public void start() {

        //Try to fetch the registry.
        try {

            this.registry = LocateRegistry.createRegistry(this.port);

            Logger.log(Level.FINEST, "Server (RMI)", "Registry created");

        }
        catch (RemoteException e) {

            Logger.log(Level.SEVERE, "Server (RMI)", "Unable to create RMI registry", e);

            this.notifyError();

        }


        //Try to bind the skeleton in the registry.
        try {

            this.registry.bind(bindName, this);
            Logger.log(Level.FINEST, "Server (RMI)", "Bounded remote object as " + bindName);

        }
        catch (AlreadyBoundException e) {

            Logger.log(Level.FINEST, "Server (RMI)", "The registry was already bound to object", e);

            try {

                this.registry.rebind(bindName, this);

            } catch (RemoteException f) {

                Logger.log(Level.SEVERE, "Server (RMI)", "Unable to rebound the remote object to the registry", f);
            }

        }
        catch (AccessException e) {

            Logger.log(Level.SEVERE, "Server (RMI)", "Access exception while binding", e);

        }

        catch (RemoteException e) {

            Logger.log(Level.SEVERE, "Server (RMI)", "Unable to bind the remote object to the registry", e);

        }

        //Export the remote object
        try {

            UnicastRemoteObject.exportObject(this, 0);

        } catch (RemoteException e) {

            Logger.log(Level.SEVERE, "Server (RMI)", "Unable to export the remote object", e);

            //Notify the listener of an abnormal fault
            this.notifyError();
        }

        //If we reach this point, the server is up and running on port 1099 (default)
        Logger.log(Level.FINE, "Server (RMI)", "Up and running on port " + this.port);

    }

    /**
     * Returns the matching client handler with the provided token
     * @param connectionToken The token to refer to the client handler
     * @return The corresponding client handler
     */
    private RMIClientHandler getClientHandler(String connectionToken) throws NotConnectedException {

        Iterator it = this.clientHandlers.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();

            RMIClientHandler handler = (RMIClientHandler)pair.getKey();

            if (connectionToken.equals(handler.getToken())) {

                return handler;

            }

        }

        throw new NotConnectedException("No user did connect with token = " + connectionToken);

    }

    /* ClientHandlerObserver implementation */
    public synchronized void onDisconnect(ClientHandler handler) {

        Logger.log(Level.FINEST, "Server (RMI)", "Client " + handler.getUsername() + " disconnected");

        //Notify the game engine
        this.notifyDisconnection(handler);

        //Remove the client handler
        this.removeClientHandler(handler);

    }

    public String getBoundableIP() throws RemoteException, ServerNotActiveException {

        return RemoteServer.getClientHost();

    }

    /* RMIServerInterface implementation */
    public synchronized String connect(RMIClientInterface clientRef) throws RemoteException, ConnectionFailedException {

        String MD5Token;

        try {

            MD5Token = Security.MD5Hash(clientRef + new Date().toString());

        } catch (NoSuchAlgorithmException e) {

            throw new ConnectionFailedException("Unable to connect.");

        }

        //Create the RMI handler
        RMIClientHandler handler = new RMIClientHandler(clientRef, MD5Token);

        //Register as observers
        handler.addObserver(this);

        //Add the handler
        this.addClientHandler(handler);

        Logger.log(Level.FINEST, "Server (RMI)", "New client connected, waiting for request..");

        return MD5Token;

    }

    public synchronized boolean login(String connectionToken, LoginRequest loginRequest) throws RemoteException, LoginFailedException, AlreadyLoggedInException, NotConnectedException {

        ClientHandler handler = null;

        try {

            handler = this.getClientHandler(connectionToken);

            return this.authenticate(handler, loginRequest);

        }
        //If we want to terminate the handler we must, before forwarding the exception to the client, catch it and do something.
        catch (LoginFailedException e) {

            Logger.log(Level.FINEST, "Server (RMI)", "Bad login for client " + loginRequest.getUsername());

            throw e;

        }

        catch (AlreadyLoggedInException e) {

            Logger.log(Level.FINEST, "Server (RMI)", "Client " + loginRequest.getUsername() + " was already logged in!");

            throw e;

        }

    }

    public synchronized void register(String connectionToken, RegisterRequest registerAuthentication) throws RemoteException, UsernameAlreadyInUseException, AlreadyLoggedInException, LoginFailedException {




    }

    public synchronized boolean performAction(String connectionToken, ActionRequest actionRequest) throws RemoteException, NotRegisteredException {
        return false;
    }

}
