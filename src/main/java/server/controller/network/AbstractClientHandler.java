package server.controller.network;


import netobject.Action;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */
public abstract class AbstractClientHandler {

    //A representative username
    protected String username;

    //The event listeners
    protected AbstractClientListener listener;

    /**
     * The method is fired when a player makes a move
     * This method is going to and must be called just by the MatchController to inform other clients
     * @param action The action performed
     * @param sender The player who made the action
     */
    abstract public void notifyPlayerForAction(Action action, AbstractClientHandler sender);

    /**
     * Adds an event listener
     * @param listener The listener
     * @return true upon success
     */
    public final boolean addEventListener(AbstractClientListener listener) {


        //Check whether the listener is not null
        if (listener != null) {
            this.listener = listener;
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
    public final void removeEventListener() {

        //Check whether the listener is not null
        this.listener = null;
    }

    public AbstractClientListener getListener() {
        return listener;
    }

    public String getUsername() {
        return username;
    }
}
