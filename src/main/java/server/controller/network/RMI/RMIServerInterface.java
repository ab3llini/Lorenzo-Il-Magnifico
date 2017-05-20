package server.controller.network.RMI;

import client.RMIClientInterface;
import exception.LoginFailedException;
import exception.NotRegisteredException;
import exception.UsernameAlreadyInUseException;
import netobject.Action;
import netobject.LoginAuthentication;
import netobject.RegisterAuthentication;
import server.controller.network.RMIConnectionToken;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */
public interface RMIServerInterface extends Remote {

    /**
     * Method used to get  server.
     * It will return to the client his external IP address so that he can export himself properly
     * @param clientRef the connecting client
     * @return the connection token
     * @throws RemoteException Needed for RMI Pattern
     */
    RMIConnectionToken connect(RMIClientInterface clientRef) throws RemoteException, ServerNotActiveException;

    /**
     * Attempts to login a new RMI client on the server.
     * @param connectionToken The stub of the client
     * @param loginAuthentication The auth request
     * @throws RemoteException Needed for RMI Pattern
     */
    void login(int connectionToken, LoginAuthentication loginAuthentication) throws RemoteException, LoginFailedException;

    /**
     * Attempts to login a new RMI client on the server.
     * @param connectionToken The stub of the client
     * @param authentication The registration request
     * @throws RemoteException Needed for RMI Pattern
     */
    void register(int connectionToken, RegisterAuthentication authentication) throws RemoteException, UsernameAlreadyInUseException;

    /**
     * Attempts to perform an action
     * @param connectionToken The stub of the client
     * @param action The action to be performed
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean performAction(int connectionToken, Action action) throws RemoteException, NotRegisteredException;

}
