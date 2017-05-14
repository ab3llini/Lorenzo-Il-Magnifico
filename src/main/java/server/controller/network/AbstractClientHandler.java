package server.controller.network;

import server.controller.game.Action;
import server.model.Player;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */
public abstract class AbstractClientHandler {

    //A representative username
    protected String username;

    public AbstractClientHandler(String username) {

       this.username = username;

    }

    /**
     * Triggered method on client interaction
     * This method is going to be called both by RMI and Socket
     * @param action The action performed
     */
    abstract public void onClientAction(Action action);

    /**
     * The method is fired when a player makes a move
     * This method is going to and must be called just by the MatchController to inform other clients
     * @param action The action performed
     * @param sender The player who made the action
     */
    abstract public void notifyPlayerForAction(Action action, Player sender);

}
