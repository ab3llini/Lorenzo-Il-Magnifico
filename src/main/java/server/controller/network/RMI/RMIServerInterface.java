package server.controller.network.RMI;

import client.controller.network.RMI.RMIClientInterface;
import exception.*;
import exception.authentication.AlreadyLoggedInException;
import exception.authentication.LoginFailedException;
import exception.authentication.NotConnectedException;
import exception.authentication.UsernameAlreadyInUseException;
import netobject.request.action.ActionRequest;
import netobject.request.auth.LoginRequest;
import netobject.request.auth.RegisterRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */
public interface RMIServerInterface extends Remote {

    /**
     * Returns the external IP of the client so that he can export himself properly
     * @return the client external ip
     */
    String getBoundableIP() throws RemoteException, ServerNotActiveException;

    /**
     * Method used to get  server.
     * It will return to the client a session token that will be later used to make any call
     * @param clientRef the connecting client
     * @return the connection token as an MD5 Hexadecimal string.
     * @throws RemoteException Needed for RMI Pattern
     */
    String connect(RMIClientInterface clientRef) throws RemoteException, ConnectionFailedException;

    /**
     * Attempts to login a new RMI client on the server.
     * @param connectionToken The stub of the client
     * @param loginAuthentication The auth request
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean login(String connectionToken, LoginRequest loginAuthentication) throws RemoteException, LoginFailedException, AlreadyLoggedInException, NotConnectedException;

    /**
     * Attempts to login a new RMI client on the server.
     * @param connectionToken The stub of the client
     * @param authentication The registration request
     * @throws RemoteException Needed for RMI Pattern
     */
    void register(String connectionToken, RegisterRequest authentication) throws RemoteException, UsernameAlreadyInUseException, AlreadyLoggedInException, LoginFailedException;

    /**
     * Attempts to perform an actionRequest
     * @param connectionToken The stub of the client
     * @param actionRequest The actionRequest to be performed
     * @return Upon success returns true, otherwise false.
     * @throws RemoteException Needed for RMI Pattern
     */
    boolean performAction(String connectionToken, ActionRequest actionRequest) throws RemoteException, NotRegisteredException;

}
