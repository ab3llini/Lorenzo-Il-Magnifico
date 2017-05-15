package server.controller.game;

/*
 * Created by alberto on 09/05/17.
 */

import netobject.Action;
import server.controller.network.AbstractClientHandler;
import server.controller.network.RMIServer;
import server.controller.network.SocketServer;

import java.util.ArrayList;

/**
 * Rappresenta il server di gioco.
 */
public class GameEngine {

    //Il server in ascolto per nuove connessioni TCP su socket
    SocketServer socketServer;

    //Il server in ascolto per nuove connessioni RMI
    RMIServer rmiServer;

    //Le lobby esistenti
    ArrayList<Lobby> lobbies;


    public void onClientAction(Action action, AbstractClientHandler clientHandler) {

    }

    public void dispatchClientAction(Lobby lobby, AbstractClientHandler clientHandler, Action action) {

    }

    public RMIServer getRmiServer() {
        return rmiServer;
    }

    public SocketServer getSocketServer() {
        return socketServer;
    }
}
