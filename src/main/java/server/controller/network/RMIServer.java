package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;

import exception.NotRegisteredException;
import exception.UsernameAlreadyInUseException;

import netobject.Action;
import netobject.Message;
import netobject.MessageType;

import logger.*;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;


/**
 * The RMI Server.
 * Extends UnicastRemoteObject in order to be bound with the RMI registry
 * Implements RMIServerInterface to implement remote methods
 */
public class RMIServer extends AbstractServer implements RMIServerInterface {

    //The RMI client handlers
    private transient final ArrayList<RMIClientHandler> clientHandlers;

    //The RMI registry
    private transient Registry registry;

    //The port of the RMI registry
    private transient final int port;

    //The name to bind the remote object with
    private transient final String bindName;

    /**
     * Constructor.
     * Takes care of registering the remote object into the registry.
     * Assigns the game engine to a local holder for further requests.
     * @param bindName The name to bind the remote object with.
     * @throws RemoteException UnicastRemoteObject throws this exception.
     */
    public RMIServer(int port,  String bindName) {

        super();

        //Initialize handlers
        this.clientHandlers = new ArrayList<RMIClientHandler>();

        //Init port
        this.port = port;

        //Init bind name
        this.bindName = bindName;

    }

    public void start() {

        //Block start method if no one registered as listener.
        //There would be a null pointer exception when trying to raise an event
        if (this.listener == null) {

            Logger.log(logger.Level.SEVERE, "Server (RMI)", "The server can't run without a listener");

            return;

        }

        //Try to fetch the registry.
        try {

            this.registry = LocateRegistry.createRegistry(this.port);

            Logger.log(logger.Level.FINE, "Server (RMI)", "Registry created");

        }
        catch (RemoteException e) {

            Logger.log(logger.Level.SEVERE, "Server (RMI)", "Unable to create RMI registry", e);

        }
        finally {

            //Notify the listener of an abnormal fault
            this.listener.onServerFault(this);

        }

        //Try to bind the skeleton in the registry.
        try {

            this.registry.bind(bindName, this);
            Logger.log(logger.Level.FINE, "Server (RMI)", "Bounded remote object as " + bindName);

        }
        catch (AlreadyBoundException e) {

            Logger.log(logger.Level.INFO, "Server (RMI)", "The registry was already bound to object", e);

            try {

                this.registry.rebind(bindName, this);

            } catch (RemoteException f) {

                Logger.log(logger.Level.SEVERE, "Server (RMI)", "Unable to rebound the remote object to the registry", f);
            }

        }
        catch (AccessException e) {

            Logger.log(logger.Level.SEVERE, "Server (RMI)", "Access exception while binding", e);

        }

        catch (RemoteException e) {

            Logger.log(logger.Level.SEVERE, "Server (RMI)", "Unable to bind the remote object to the registry", e);

        }

        //Export the remote object
        try {

            UnicastRemoteObject.exportObject(this, 0);

        } catch (RemoteException e) {

            Logger.log(logger.Level.SEVERE, "Server (RMI)", "Unable to export the remote object", e);

            //Notify the listener of an abnormal fault
            this.listener.onServerFault(this);
        }

        //If we reach this point, the server is up and running on port 1099 (default)
        Logger.log(logger.Level.INFO, "Server (RMI)", "Up and running on port " + this.port);

    }

    public boolean register(RMIClientInterface clientRef, Message m) throws RemoteException, UsernameAlreadyInUseException {

        if (m.type != MessageType.Registration) {

            return false;

        }

        Logger.log(logger.Level.FINE, "Server (RMI)", "New registration request from " + clientRef);

        if (!this.existsClientWithUsername(m.value)) {

            //Create a new RMI client handler
            RMIClientHandler rch = new RMIClientHandler(clientRef, m.value);

            //Register the RMI server as observer
            rch.addEventListener(this);

            //Add the client handler to the list
            this.clientHandlers.add(rch);

            Logger.log(logger.Level.INFO, "Server (RMI)", "New client added '" + m.value + "'");

            return true;

        }
        else {

            Logger.log(logger.Level.WARNING, "Server (RMI)", "Registration failed, username '"+m.value+"' already in use");

            throw new UsernameAlreadyInUseException("The username " + m.value + " is not available");

        }

    }

    public boolean performAction(RMIClientInterface clientRef, Action action) throws RemoteException, NotRegisteredException {

        //Get the handler
        RMIClientHandler handler = this.getClientHandler(clientRef);

        //If the client never authenticated throw an exception
        if (handler == null) {

            throw new NotRegisteredException("No such handler for the provided client");

        }
        else {

            //Propagate the action
            this.onAction(handler, action);

            return true;

        }

    }

    /**
     * Returns the matching client handler with the provided stub reference
     * @param clientRef The stub reference for the client
     * @return The corresponding client handler
     */
    private RMIClientHandler getClientHandler(RMIClientInterface clientRef) {

        for (RMIClientHandler handler : this.clientHandlers) {

            if (handler.getClientRef() == clientRef) {
                return handler;
            }
        }

        return null;

    }

    public void onDisconnect(AbstractClientHandler handler) {

        Logger.log(logger.Level.WARNING, "Server (RMI)", "Detected client disconnection. Removing client '"+handler+"'");

        //Remove the client handler
        this.clientHandlers.remove(handler);

    }

    public void onAction(AbstractClientHandler handler, Action action) {

        Logger.log(logger.Level.INFO, "Server (RMI)", "Client '"+handler+"' performed the action '"+action+"'");

        //Raise event
        this.listener.onAction(handler, action);

    }

    public boolean existsClientWithUsername(String username) {

        return this.listener.existsClientWithUsername(username);

    }

    public static void main(String[] args) {

        RMIServer server = new RMIServer(1099, "server");
        server.start();
    }

    public ArrayList<RMIClientHandler> getClientHandlers() {
        return clientHandlers;
    }
}
