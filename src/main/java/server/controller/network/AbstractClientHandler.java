package server.controller.network;


import netobject.Action;
import server.model.Player;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */
public abstract class AbstractClientHandler {

    //A representative username
    protected String username;

    //The event listeners
    protected ArrayList<AbstractClientListener> listeners = new ArrayList<AbstractClientListener>();

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

    /**
     * Adds an event listener
     * @param listener The listener
     * @return true upon success
     */
    public final boolean addEventListener(AbstractClientListener listener) {

        //Check whether the listener is not null
        if (listener != null) {
            this.listeners.add(listener);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Removes the event listener
     * @param listener The listener to nbe removed
     * @return true upon success.
     */
    public final boolean removeEventListener(AbstractClientListener listener) {

        //Check whether the listener is not null
        if (listener != null) {
            this.listeners.remove(listener);
            return true;
        }
        else {
            return false;
        }
    }

}
