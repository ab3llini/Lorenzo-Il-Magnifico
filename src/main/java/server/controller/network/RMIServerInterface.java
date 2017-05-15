package server.controller.network;

import client.RMIClientInterface;
import exception.NotRegisteredException;
import exception.UsernameAlreadyInUseException;
import netobject.Action;
import netobject.Message;

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
     * @param m The username of the client
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean register(RMIClientInterface clientRef, Message m) throws RemoteException, UsernameAlreadyInUseException;

    /**
     * Attempt to perform an action
     * @param clientRef The stub of the client
     * @param action The action to be performed
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean performAction(RMIClientInterface clientRef, Action action) throws RemoteException, NotRegisteredException;

}
