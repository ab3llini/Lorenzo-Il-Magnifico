package server.controller.network;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */
public abstract class AbstractServer implements AbstractClientListener {

    //The event listeners
    protected AbstractServerListener listener;

    /**
     * Adds an event listener
     * @param listener The listener
     * @return true upon success
     */
    public final boolean addEventListener(AbstractServerListener listener) {


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
     * @return true upon success.
     */
    public final void removeEventListener() {

        this.listener = null;

    }

}
