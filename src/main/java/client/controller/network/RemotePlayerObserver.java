package client.controller.network;

import server.model.Match;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public interface RemotePlayerObserver {

    void onModelUpdate(Client sender, Match model);

    void onMoveEnabled(Client sender, String message);

    void onMoveDisabled(Client sender, String message);

    void onTimeoutExpired(Client sender, String message);

    void onActionRefused(Client sender, String message);

}
