package client.controller.network;

import netobject.action.ActionType;
import server.model.Match;
import server.model.board.Player;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public interface RemotePlayerObserver {

    void onModelUpdate(Client sender, Match model);

    void onTurnEnabled(Client sender, Player player, String message);

    void onImmediateActionAvailable(Client sender, ActionType actionType, Player player, String message);

    void onTurnDisabled(Client sender, Player player, String message);

    void onTimeoutExpired(Client sender, Player player, String message);

    void onActionRefused(Client sender, String message);

}
