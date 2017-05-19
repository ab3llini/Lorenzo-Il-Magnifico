package server.controller.network.RMI;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;

import exception.NotRegisteredException;
import exception.UsernameAlreadyInUseException;

import netobject.Action;

import logger.*;
import netobject.RegistrationRequest;
import netobject.RegistrationResponse;
import server.controller.network.Server;
import server.controller.network.ClientHandler;

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
 * Implements RMIServerStub to implement remote methods
 */
public class RMIServer extends Server implements RMIServerStub {

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

        //Initialize handlers
        this.clientHandlers = new ArrayList<RMIClientHandler>();

        //Init port
        this.port = port;

        //Init bind name
        this.bindName = bindName;

    }

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

    public RegistrationResponse performRegistrationRequest(RMIClientInterface clientRef, RegistrationRequest request) throws RemoteException, UsernameAlreadyInUseException {

        if (request.username == null) {

            Logger.log(Level.WARNING, "Server (RMI)", "Attempting to performRegistrationRequest without a username");

            return new RegistrationResponse(false);

        }

        Logger.log(Level.FINEST, "Server (RMI)", "New registration request with username " + request.username);

        if (!this.checkUsernameAvailable(request.username)) {

            //Create a new RMI client handler
            RMIClientHandler rch = new RMIClientHandler(clientRef, request.username);

            //Add the client handler to the list
            this.clientHandlers.add(rch);

            //Notify observers
            this.notifyConnection(rch);

            return new RegistrationResponse(true);

        }
        else {

            Logger.log(Level.WARNING, "Server (RMI)", "Registration failed, username '"+ request.username+"' already in use");

            throw new UsernameAlreadyInUseException("The username " + request.username + " is not available");

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
            Logger.log(Level.FINE, "Server (RMI)", "Action performed by " + handler.getUsername());

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

    //Interface impl.
    public void onDisconnect(ClientHandler handler) {

        Logger.log(Level.WARNING, "Server (RMI)", "Detected client disconnection. Removing client '"+handler+"'");

        //Notify the game engine
        this.notifyDisconnection(handler);

        //Remove the client handler
        this.clientHandlers.remove(handler);

    }


    public ArrayList<RMIClientHandler> getClientHandlers() {
        return clientHandlers;
    }


    public static void main(String[] args) {

        RMIServer server = new RMIServer(1099, "server");
        server.start();
    }

}
