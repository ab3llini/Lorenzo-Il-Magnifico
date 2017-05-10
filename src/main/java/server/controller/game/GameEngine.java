package server.controller.game;

/*
 * Created by alberto on 09/05/17.
 */

import server.controller.network.AbstractClientHandler;
import server.controller.network.ServerInterface;
import server.controller.network.RMIServer;
import server.controller.network.SocketServer;

import java.util.ArrayList;

/**
 * Rappresenta il server di gioco.
 */
public class GameEngine implements ServerInterface {

    //Il server in ascolto per nuove connessioni TCP su socket
    SocketServer socketServer;

    //Il server in ascolto per nuove connessioni RMI
    RMIServer rmiServer;

    //Le lobby esistenti
    ArrayList<Lobby> lobbies;


    public void onClientAction(AbstractClientHandler clientHandler, Action action) {

    }

    public void dispatchClientAction(Lobby lobby, AbstractClientHandler clientHandler, Action action) {

    }
}
