package server.controller.network.RMI;

import client.RMIClientInterface;
import exception.NotRegisteredException;
import exception.UsernameAlreadyInUseException;
import netobject.Action;
import netobject.RegistrationRequest;
import netobject.RegistrationResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */
public interface RMIServerStub extends Remote {

    /**
     * Attempts to register a new RMI client on the server.
     * @param clientRef The stub of the client
     * @param request The request
     * @return Upon success returns RegistrationResponse with success = true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    RegistrationResponse performRegistrationRequest(RMIClientInterface clientRef, RegistrationRequest request) throws RemoteException, UsernameAlreadyInUseException;

    /**
     * Attempts to perform an action
     * @param clientRef The stub of the client
     * @param action The action to be performed
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean performAction(RMIClientInterface clientRef, Action action) throws RemoteException, NotRegisteredException;

}
