package client.controller.network.RMI;

import netobject.notification.LobbyNotification;
import server.model.Match;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

public interface RMIClientInterface extends Remote {

    /**
     * Method called by the server to check connection status
     * @return true
     * @throws RemoteException required
     */
    boolean heartbeat() throws RemoteException;

    /**
     * Method called by the server to terminate the client process
     * @throws RemoteException required
     */
    void terminate() throws RemoteException;

    void onLobbyNotification(LobbyNotification not) throws RemoteException;

    void onModelUpdate(Match model) throws RemoteException;

    void onMoveEnabled(String message) throws RemoteException;

    void onMoveDisabled(String message) throws RemoteException;

    void onMoveTimeoutExpired(String message) throws RemoteException;

    void onActionRefused(String message) throws RemoteException;


}
