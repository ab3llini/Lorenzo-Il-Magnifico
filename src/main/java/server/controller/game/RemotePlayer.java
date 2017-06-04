package server.controller.game;

import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import server.model.Match;
import server.model.board.Player;

/*
 * @author  ab3llini
 * @since   29/05/17.
 */


/**
 * This interface is used by the match controller to interact with the client throughout the client handler
 */
public interface RemotePlayer {

    void notifyModelUpdate(Match model);

    void notifyTurnEnabled(Player player, String message);

    void notifyTurnDisabled(Player player, String message);

    void notifyActionTimeoutExpired(Player player, String message);

    void notifyActionRefused(String message);

    void notifyImmediateActionAvailable(ImmediateActionType immediateActionType, Player player, String message);

    void notifyActionPerformed(Player player, Action action, String message);

}
