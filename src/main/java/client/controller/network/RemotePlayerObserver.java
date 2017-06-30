package client.controller.network;

import netobject.action.Action;
import netobject.action.ActionType;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.MatchNotification;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.rmi.RemoteException;
import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public interface RemotePlayerObserver extends Observer {

    void onNotification(Client sender, MatchNotification notification);

    void onModelUpdate(Client sender, Match model);

    void onTurnEnabled(Client sender, Player player, String message);

    void onImmediateActionAvailable(Client sender, ImmediateActionType actionType, Player player, String message);

    void onTurnDisabled(Client sender, Player player, String message);

    void onTimeoutExpired(Client sender, Player player, String message);

    void onActionRefused(Client sender, Action action, String message);

    void onActionPerformed(Client sender, Player player, Action action, String message);

    void onLeaderCardDraftRequest(Client sender, Deck<LeaderCard> cards, String message);

    void onBonusTileDraftRequest(Client sender, ArrayList<BonusTile> tiles, String message);

}
