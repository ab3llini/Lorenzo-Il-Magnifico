package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;
import server.controller.game.Action;
import server.controller.game.GameEngine;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;

/**
 * The RMI Server.
 * Extends UnicastRemoteObject in order to be binded with the RMI registry
 * Implements RMIServerInterface to implement remote methods
 */
public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {

    //The RMI client handlers
    private transient ArrayList<RMIClientHandler> clientHandlers = new ArrayList<RMIClientHandler>();

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
    RMIServer(GameEngine engine, String bindName) throws RemoteException {

        //Assign the game engine
        this.engine = engine;

        //Try to fetch the registry. Remember to turn it on throughout cli.
        try {

            this.registry = LocateRegistry.getRegistry();
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

    public boolean register(RMIClientInterface clientRef, String username) {

        System.out.println("New RMI registration request");

        if (!this.doesExistClientWithUsername(username)) {

            this.clientHandlers.add(new RMIClientHandler(clientRef, username));

            System.out.println("New RMI client added: " + username);

            return true;

        }
        else {

            try {

                clientRef.registrationFailed();

                System.out.println("Registration for new RMI client failed, username '"+username+"' is already in use");

            } catch (RemoteException e) {

                System.out.println(e.getMessage());

            }

            return false;

        }

    }

    public boolean performAction(RMIClientInterface clientRef, Action action) throws RemoteException {

        return false;

    }

    /**
     * Checks whether a client with the provided username exists or not.
     * @param username the username to check
     * @return True or False
     */
    public Boolean doesExistClientWithUsername(String username) {

        for (RMIClientHandler client : this.clientHandlers) {

            if (client.username.equals(username)) {
                return true;
            }

        }

        return false;

    }

    public static void main(String[] args) {

        try {
            RMIServer server = new RMIServer(null, "server");
        } catch (RemoteException e) {
            System.out.println("Error : " + e.getMessage());
        }

    }
}
