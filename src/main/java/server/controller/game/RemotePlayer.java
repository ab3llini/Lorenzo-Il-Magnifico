package server.controller.game;

import exception.ActionException;
import server.model.Match;

/*
 * @author  ab3llini
 * @since   29/05/17.
 */


/**
 * This interface is used by the match controller to interact with the client throughout the client handler
 */
public interface RemotePlayer {

    void updateModel(Match model);

    void notifyMoveEnabled();

    void notifyMoveDisabled();

    void notifyMoveTimeoutExpired();

    void notifyActionRefused(ActionException exception);

}
