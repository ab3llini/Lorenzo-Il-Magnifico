package server.controller.network;

import client.RMIClientInterface;
import server.controller.game.Action;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */
public interface RMIServerInterface extends Remote {

    /**
     * Attempt to register a new RMI client on the server.
     * @param clientRef The stub of the client
     * @param username The username of the client
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean register(RMIClientInterface clientRef, String username) throws RemoteException;

    /**
     * Attempt to perform an action
     * @param clientRef The stub of the client
     * @param action The action to be performed
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean performAction(RMIClientInterface clientRef, Action action) throws RemoteException;

}
