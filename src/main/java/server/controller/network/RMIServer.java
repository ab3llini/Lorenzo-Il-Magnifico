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
import server.controller.game.GameEngine;

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
public class RMIServer extends UnicastRemoteObject implements RMIServerInterface, AbstractClientListener {

    //The RMI client handlers
    private transient ArrayList<RMIClientHandler> clientHandlers;

    //The RMI registry
    private transient Registry registry;

    //A reference to the game engine
    private transient GameEngine engine;

    /**
     * Constructor.
     * Takes care of registering the remote object into the registry.
     * Assigns the game engine to a local holder for further requests.
     * @param engine The game engine.
     * @param bindName The name to bind the remote object with.
     * @throws RemoteException UnicastRemoteObject throws this exception.
     */
    RMIServer(GameEngine engine, int port,  String bindName) throws RemoteException {

        //Assign the game engine
        this.engine = engine;

        //Initialize handlers
        this.clientHandlers = new ArrayList<RMIClientHandler>();

        //Try to fetch the registry. Remember to turn it on throughout cli.
        try {

            this.registry = LocateRegistry.createRegistry(port);
            System.out.println("Located registry..");

        }
        catch (RemoteException e) {

            System.out.println("Unable to fetch RMI registry. Did you turned it on?");
            System.out.println("The reported message was : " + e.getMessage());

        }

        //Try to bind the skeleton in the registry.
        try {

            this.registry.bind(bindName, this);
            System.out.println("Binded remote object as " + bindName + "..");

        }
        catch (AlreadyBoundException e) {

            System.out.println("The registry already has an entry for '" + e.getMessage() +"'. Replacing entry..");

            this.registry.rebind(bindName, this);

        }

        //If we reach this point, the server is up and running on port 1099 (default)
        System.out.println("RMI Server up and running on port 1099");

    }

    public boolean register(RMIClientInterface clientRef, Message m) throws RemoteException, UsernameAlreadyInUseException {

        if (m.type != MessageType.Registration) {

            return false;

        }

        System.out.println("New RMI registration request");

        if (!this.doesExistClientWithUsername(m.value)) {

            //Create a new RMI client handler
            RMIClientHandler rch = new RMIClientHandler(clientRef, m.value);

            //Register the RMI server as observer
            rch.addEventListener(this);

            //Add the client handler to the list
            this.clientHandlers.add(rch);

            System.out.println("New RMI client added: " + m.value);

            return true;

        }
        else {

            System.out.println("Registration for new RMI client failed, username '"+ m.value +"' is already in use");

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

            //Cool stuff
            System.out.println("Action performed by " + handler.username);

            return true;

        }

    }

    /**
     * Checks whether a client with the provided username exists or not.
     * @param username the username to check
     * @return True or False
     */
    private Boolean doesExistClientWithUsername(String username) {

        for (RMIClientHandler client : this.clientHandlers) {

            if (client.username.equals(username)) {
                return true;
            }

        }

        return false;

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

    }

    public void onAction(AbstractClientHandler handler, Action action) {

        //Propagate the action to the game engine.. toward the lobby
        System.out.println("The client " + handler + " performed the action " + action);

    }

    public static void main(String[] args) {

        try {
            RMIServer server = new RMIServer(null, 1099, "server");
        } catch (RemoteException e) {
            System.out.println("Error : " + e.getMessage());
        }

    }

}
