package server.controller.game;

import server.model.Match;

/*
 * @author  ab3llini
 * @since   29/05/17.
 */


/**
 * This interface is used by the match controller to interact with the client throughout the client handler
 */
public interface RemotePlayer {

    void notifyModelUpdate(Match model);

    void notifyMoveEnabled(String message);

    void notifyMoveDisabled(String message);

    void notifyMoveTimeoutExpired(String message);

    void notifyActionRefused(String message);

}
