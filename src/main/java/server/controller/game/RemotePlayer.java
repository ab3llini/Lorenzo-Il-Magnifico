package server.controller.game;

import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.MatchNotification;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   29/05/17.
 */


/**
 * This interface is used by the match controller to interact with the client throughout the client handler
 */
public interface RemotePlayer {

    void notify(MatchNotification notification);

    void notifyModelUpdate(Match model);

    void notifyTurnEnabled(Player player, String message);

    void notifyTurnDisabled(Player player, String message);

    void notifyActionTimeoutExpired(Player player, String message);

    void notifyActionRefused(String message);

    void notifyImmediateActionAvailable(ImmediateActionType immediateActionType, Player player, String message);

    void notifyActionPerformed(Player player, Action action, String message);

    void notifyLeaderCardDraftRequest(Deck<LeaderCard> cards, String message);

    void notifyBonusTileDraftRequest(ArrayList<BonusTile> tiles, String message);

}
