package client.controller.network.RMI;

import client.controller.network.Client;
import netobject.action.Action;
import netobject.action.ActionType;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.notification.MatchNotification;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

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

    void onNotification(MatchNotification notification) throws RemoteException;

    void onModelUpdate(Match model) throws RemoteException;

    void onTurnEnabled(Player player, String message) throws RemoteException;

    void onTurnDisabled(Player player, String message) throws RemoteException;

    void onActionTimeoutExpired(Player player, String message) throws RemoteException;

    void onActionRefused(Action action, String message) throws RemoteException;

    void onImmediateActionAvailable(ImmediateActionType actionType, Player player, String message) throws RemoteException;

    void onActionPerformed(Player player, Action action, String message) throws RemoteException;

    void onLeaderCardDraftRequest(Deck<LeaderCard> cards, String message) throws RemoteException;

    void onBonusTileDraftRequest(ArrayList<BonusTile> tiles, String message) throws RemoteException;


}
