package client.controller.network;

import client.controller.LocalPlayer;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.notification.MatchNotification;
import netobject.request.auth.LoginRequest;
import server.controller.game.RemotePlayer;
import server.controller.network.Observable;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */

/**
 * The class describes a generic client and provides some basic functionality
 */
public abstract class Client implements Observable<ClientObserver>, RemotePlayer, LocalPlayer {

    //The list of observers
    protected ArrayList<ClientObserver> observers = new ArrayList<ClientObserver>();

    protected String username;

    protected boolean connected;

    protected boolean authenticated;

    /**
     * Method that should be implemented to connect to the remote server
     * @return true upon success, false otherwise.
     */
    public abstract void connect() throws Exception;

    /**
     * Method to attempt a login.
     * Returns void, login  success is event based
     * @param authentication
     */
    public abstract void login(LoginRequest authentication);


    public String getUsername() {
        return username;
    }

    public boolean hasConnected() {
        return this.connected;
    }

    public boolean hasAuthenticated() {
        return this.authenticated;
    }

    /**
     * Notify that the server closed the connection
     */
    protected void notifyDisconnection() {

        for (ClientObserver o : this.observers) {

            o.onDisconnection(this);

        }

    }

    protected void notifyLoginFailed(String reason) {

        for (ClientObserver o : this.observers) {

            o.onLoginFailed(this, reason);

        }

    }

    protected void notifyLoginSucceeded() {

        for (ClientObserver o : this.observers) {

            o.onLoginSuccess(this);

        }

    }

    protected void notifyLobbyNotificationReceived(LobbyNotification not) {

        for (ClientObserver o : this.observers) {

            o.onLobbyNotification(this, not);

        }

    }

    public boolean addObserver(ClientObserver o) {

        return o != null && this.observers.add(o);

    }

    public boolean removeObserver(ClientObserver o) {

        return o != null && this.observers.remove(o);

    }

    /*
     * Remote player implementation
     */

    public void notifyModelUpdate(Match model) {

        for (ClientObserver o : this.observers) {

            o.onModelUpdate(this, model);

        }

    }

    public void notifyTurnEnabled(Player player, String message) {

        for (ClientObserver o : this.observers) {

            o.onTurnEnabled(this, player, message);

        }

    }

    public void notifyTurnDisabled(Player player, String message) {

        for (ClientObserver o : this.observers) {

            o.onTurnDisabled(this, player, message);

        }

    }

    public void notifyActionTimeoutExpired(Player player, String message) {

        for (ClientObserver o : this.observers) {

            o.onTimeoutExpired(this, player, message);

        }

    }

    public void notifyActionRefused(String message) {

        for (ClientObserver o : this.observers) {

            o.onActionRefused(this, message);

        }

    }

    public void notifyImmediateActionAvailable(ImmediateActionType immediateActionType, Player player, String message) {

        for (ClientObserver o : this.observers) {

            o.onImmediateActionAvailable(this, immediateActionType, player, message);

        }

    }

    public void notifyActionPerformed(Player player, Action action, String message) {

        for (ClientObserver o : this.observers) {

            o.onActionPerformed(this, player, action, message);

        }

    }

    public void notifyLeaderCardDraftRequest(Deck<LeaderCard> deck,  String message) {

        for (ClientObserver o : this.observers) {

            o.onLeaderCardDraftRequest(this, deck, message);

        }

    }

    public void notifyBonusTileDraftRequest(ArrayList<BonusTile> tiles,  String message) {

        for (ClientObserver o : this.observers) {

            o.onBonusTileDraftRequest(this, tiles, message);

        }

    }

    public void notify(MatchNotification notification) {

        for (ClientObserver o : this.observers) {

            o.onNotification(this, notification);

        }

    }

}
