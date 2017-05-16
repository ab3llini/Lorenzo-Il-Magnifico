package server.controller.network;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

/**
 * This interface acts mainly as a proxy between the game engine and the client handler event chain
 * Has been defined to provide further implementation later on
 */
public interface AbstractServerListener extends AbstractClientListener {

    void onServerFault(AbstractServer server);

}
