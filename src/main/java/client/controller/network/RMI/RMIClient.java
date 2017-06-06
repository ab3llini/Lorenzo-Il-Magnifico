package client.controller.network.RMI;

import client.controller.network.Client;
import exception.ConnectionFailedException;
import exception.NotRegisteredException;
import exception.authentication.AuthenticationException;
import exception.authentication.NotConnectedException;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.request.auth.LoginRequest;
import server.controller.network.RMI.RMIServerInterface;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

public class RMIClient extends Client implements RMIClientInterface {

    //The server stub
    private RMIServerInterface serverRef;

    //The local registry
    private Registry registry;

    //The entry name for the server interface in the remote registry
    private String remoteName;

    //The remote host
    private String host;

    //The remote port
    private int port;

    //Session token
    private String token;

    public RMIClient(String host, int port, String remoteName) {

        this.host = host;
        this.port = port;
        this.remoteName = remoteName;

    }

    public RMIServerInterface getServerRef() {
        return serverRef;
    }

    public boolean heartbeat() throws RemoteException {
        return true;
    }

    public void terminate() throws RemoteException {
        System.exit(0);

    }

    public void onLobbyNotification(LobbyNotification not) throws RemoteException {
        this.notifyLobbyNotificationReceived(not);
    }

    public void onModelUpdate(Match model) {
        this.notifyModelUpdate(model);
    }

    public void onTurnEnabled(Player player, String message) {
        this.notifyTurnEnabled(player, message);
    }

    public void onTurnDisabled(Player player, String message) {
        this.notifyTurnDisabled(player , message);
    }

    public void onActionTimeoutExpired(Player player, String message) {
        this.notifyActionTimeoutExpired(player, message);
    }

    public void onActionRefused(String message) {
        this.notifyActionRefused(message);
    }

    public void onImmediateActionAvailable(ImmediateActionType actionType, Player player, String message) throws RemoteException {
        this.notifyImmediateActionAvailable(actionType, player, message);

    }

    public void onActionPerformed(Player player, Action action, String message) throws RemoteException {
        this.notifyActionPerformed(player, action, message);

    }

    public void onLeaderCardDraftRequest(Deck<LeaderCard> cards, String message) throws RemoteException {
        this.notifyLeaderCardDraftRequest(cards, message);
    }

    public void onBonusTileDraftRequest(ArrayList<BonusTile> tiles, String message) throws RemoteException {
        this.notifyBonusTileDraftRequest(tiles, message);
    }

    public boolean connect() {
        try {

            //Locate the rmi registry
            this.registry = LocateRegistry.getRegistry(host, port);

            //Get the stub
            this.serverRef = (RMIServerInterface) registry.lookup(remoteName);

            //Get the IP to use when exporting the client interface
            String hookableIP = this.serverRef.getBoundableIP();

            //Tell the rmi registry to bound on the provided IP
            System.setProperty("java.rmi.server.hostname", hookableIP);

            //Export the object
            UnicastRemoteObject.exportObject(this, 0);

            Logger.log(Level.FINEST, "RMIClient::connect", "Exported on " + hookableIP);

            //Connect
            this.token = this.serverRef.connect(this);

            this.connected = true;

            Logger.log(Level.FINE, "RMIClient::connect", "Connected with token " + token);

            return true;

        } catch (AccessException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        } catch (ConnectionFailedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Logger.log(Level.FINE, "RMIClient::connect", "Unable to connect.");

        return false;
    }

    public void login(LoginRequest authentication) {

        try {

            if (this.serverRef.login(this.token, authentication)) {

                this.authenticated = true;

                this.username = authentication.getUsername();

                this.notifyLoginSucceeded();

            }

        } catch (RemoteException e) {

            Logger.log(Level.SEVERE, "RMIClient::login", "Remote exception", e);

        } catch (AuthenticationException e) {

            this.notifyLoginFailed(e.getMessage());

        } catch (NotConnectedException e) {

            Logger.log(Level.SEVERE, "RMIClient::login", "The client is disconnected", e);


        }

    }

    public void performAction(Action action) {
        try {
            this.serverRef.performAction(this.token, action);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

}
