package server.controller.network;

import server.controller.game.Action;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */

public interface RemoteClientHandlerInterface extends Remote {

    void performAction(Action action) throws RemoteException;

}
