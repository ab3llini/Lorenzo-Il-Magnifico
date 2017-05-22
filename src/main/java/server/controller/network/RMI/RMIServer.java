package server.controller.network.RMI;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;
import exception.LoginFailedException;
import exception.NoSuchHanlderException;
import exception.NotRegisteredException;
import exception.UsernameAlreadyInUseException;

import netobject.Action;

import logger.*;
import netobject.LoginAuthentication;
import netobject.RegisterAuthentication;
import server.controller.network.ClientHandlerObserver;
import server.controller.network.Server;
import server.controller.network.ClientHandler;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

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
    public RMIServer(int port,  String bindName) {

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
    private RMIClientHandler getClientHandler(int connectionToken) throws NoSuchHanlderException {

        Iterator it = this.clientHandlers.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();

            RMIClientHandler handler = (RMIClientHandler)pair.getKey();

            if (connectionToken == handler.getToken()) {

                return handler;

            }

        }

        throw new NoSuchHanlderException("No handler found on RMI Server for token " + connectionToken);

    }

    /* ClientHandlerObserver implementation */
    public synchronized void onDisconnect(ClientHandler handler) {

        Logger.log(Level.FINEST, "Server (RMI)", "Client " + handler.getUsername() + " disconnected");

        //Notify the game engine
        this.notifyDisconnection(handler);

        //Remove the client handler
        this.removeClientHandler(handler);

    }

    /* RMIServerInterface implementation */
    public synchronized RMIConnectionToken connect(RMIClientInterface clientRef) throws RemoteException, ServerNotActiveException {

        int stupidToken = new Date().toString().hashCode();

        RMIConnectionToken token = new RMIConnectionToken(RemoteServer.getClientHost(), stupidToken);

        //Create the RMI handler
        RMIClientHandler handler = new RMIClientHandler(clientRef, stupidToken);

        //Register as observers
        handler.addObserver(this);

        //Add the handler
        this.addClientHandler(handler);

        Logger.log(Level.FINEST, "Server (RMI)", "New client connected, waiting for authentication..");


        return token;
    }

    public synchronized void login(int connectionToken, LoginAuthentication loginAuthentication) throws RemoteException, LoginFailedException {

        try {

            if (!this.authenticate(this.getClientHandler(connectionToken), loginAuthentication)) {

                ClientHandler h = this.getClientHandler(connectionToken);

                //Login failed, stop handler & remove object
                this.clientHandlers.get(h).interrupt();
                this.clientHandlers.remove(h);


            }

        } catch (NoSuchHanlderException e) {

            Logger.log(Level.SEVERE, "Server (RMI)", "No handler found while logging in", e);

        }

    }

    public synchronized void register(int connectionToken, RegisterAuthentication registerAuthentication) throws RemoteException, UsernameAlreadyInUseException {

        try {

            this.authenticate(this.getClientHandler(connectionToken), registerAuthentication);

        } catch (NoSuchHanlderException e) {

            Logger.log(Level.SEVERE, "Server (RMI)", "No handler found while registering", e);

        }


    }

    public synchronized boolean performAction(int connectionToken, Action action) throws RemoteException, NotRegisteredException {
        return false;
    }

    public static void main(String[] args) {

        RMIServer server = new RMIServer(1099, "server");
        server.start();
    }

}
