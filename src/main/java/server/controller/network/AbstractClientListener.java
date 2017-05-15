package server.controller.network;

import netobject.Action;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */
public interface AbstractClientListener {

    void onDisconnect(AbstractClientHandler handler);
    void onAction(AbstractClientHandler handler, Action action);

}
