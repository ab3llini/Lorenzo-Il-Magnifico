package server.controller.network.RMI;

/*
 * Created by alberto on 10/05/17.
 */

import client.controller.network.RMI.RMIClientInterface;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import server.controller.network.ClientHandler;
import server.controller.network.ClientHandlerObserver;
import server.controller.network.Observable;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RMIClientHandler extends ClientHandler implements Observable<ClientHandlerObserver> {

    /**
     * The reference to the client interface
     */
    private RMIClientInterface clientRef;

    /**
     * The connection token
     */
    private String token;

    /**
     * Heartbeat system delay constant
     */
    private static int HEARTBEAT_DELAY =  2000;

    /**
     * Heartbeat system status variable
     */
    private boolean response = false;

    //The observer list
    protected ArrayList<ClientHandlerObserver> observers = new ArrayList<ClientHandlerObserver>();

    /**
     * Constructor
     * @param clientRef The proxy reference to the client
     */

    public RMIClientHandler(RMIClientInterface clientRef, String token) {

        //Assign the reference to the RMI Client in order to make callbacks
        this.clientRef = clientRef;

        this.token = token;
    }

    /**
     * Heartbeat system to detect client disconnection
     */
    public synchronized void run() {

        while(true) {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (RMIClientHandler.this) {

                        if (!RMIClientHandler.this.response) {

                            RMIClientHandler.this.notifyDisconnection();

                        }

                        RMIClientHandler.this.notify();

                    }
                }
            }, HEARTBEAT_DELAY);

            try {

                synchronized (this) {

                    response = this.clientRef.heartbeat();

                    this.wait();

                }


            } catch (InterruptedException e) {

                Logger.log(Level.FINEST, "RMIClientHandler", "Thread terminated");

                break;

            } catch (RemoteException e) {

                //If we catch this remote exception it means that probably the client went down
                if (e instanceof ConnectException) {

                    //Went down
                    this.notifyDisconnection();

                }
                break;

            }


        }

    }

    public String getToken() {
        return token;
    }

    /* Observable<ClientHandlerObserver> implementation */
    public boolean addObserver(ClientHandlerObserver o) {

        //Check whether the observer is not null
        if (o != null) {
            this.observers.add(o);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean removeObserver(ClientHandlerObserver o) {

        return this.observers.remove(o);

    }

    public final void notifyDisconnection() {

        for (ClientHandlerObserver o : this.observers) {

            o.onDisconnect(this);

        }

    }


    public void sendLobbyNotification(LobbyNotification not) {

        try {

            this.clientRef.onLobbyNotification(not);

        } catch (RemoteException e) {

            Logger.log(Level.SEVERE, "RMIClientHandler", "Unable to send notification", e);
        }

    }

    public void notifyModelUpdate(Match model) {

        try {
            this.clientRef.onModelUpdate(model);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyTurnEnabled(Player player, String message) {

        try {
            this.clientRef.onTurnEnabled(player, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyTurnDisabled(Player player, String message) {

        try {
            this.clientRef.onTurnDisabled(player, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyActionTimeoutExpired(Player player, String message) {

        try {
            this.clientRef.onActionTimeoutExpired(player, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyActionRefused(String message) {

        try {
            this.clientRef.onActionRefused(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyImmediateActionAvailable(ImmediateActionType immediateActionType, Player player, String message) {

        try {
            this.clientRef.onImmediateActionAvailable(immediateActionType, player, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyActionPerformed(Player player, Action action, String message) {

        try {
            this.clientRef.onActionPerformed(player, action, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyLeaderCardDraftRequest(Deck<LeaderCard> cards, String message) {

        try {
            this.clientRef.onLeaderCardDraftRequest(cards, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void notifyBonusTileDraftRequest(ArrayList<BonusTile> tiles, String message) {

        try {
            this.clientRef.onBonusTileDraftRequest(tiles, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
