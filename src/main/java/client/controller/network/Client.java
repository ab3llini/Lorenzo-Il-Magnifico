package client.controller.network;

import client.controller.LocalPlayer;
import client.view.cli.CLI;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.notification.MatchNotification;
import netobject.request.auth.LoginRequest;
import netobject.request.auth.RegisterRequest;
import server.controller.game.RemotePlayer;
import server.controller.network.Observable;
import server.model.FinalStanding;
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
public abstract class Client implements RemotePlayer, LocalPlayer {

    //The list of observers
    protected ArrayList<ClientObserver> clientObservers = new ArrayList<>();
    protected ArrayList<LobbyObserver> lobbyObservers = new ArrayList<>();
    protected ArrayList<RemotePlayerObserver> remotePlayerObservers = new ArrayList<>();


    protected String username;

    protected boolean connected = false;

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

    public abstract void registration(RegisterRequest authentication);

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

        for (ClientObserver o : this.clientObservers) {

            o.onDisconnection(this);

        }

    }

    protected void notifyLoginFailed(String reason) {

        for (ClientObserver o : this.clientObservers) {

            o.onLoginFailed(this, reason);

        }

    }

    protected void notifyLoginSucceeded() {

        for (ClientObserver o : this.clientObservers) {

            o.onLoginSuccess(this);

        }

    }

    protected void notifyRegistrationSucceeded() {

        for (ClientObserver o : this.clientObservers) {

            o.onRegistrationSuccess(this);

        }

    }

    protected void notifyRegistrationFailed(String reason) {

        for (ClientObserver o : this.clientObservers) {

            o.onRegistrationFailed(this, reason);

        }

    }


    protected void notifyLobbyNotificationReceived(LobbyNotification not) {

        for (LobbyObserver o : this.lobbyObservers) {

            o.onLobbyNotification(this, not);

        }

    }


    public boolean addClientObserver(ClientObserver o) {

        return o != null && this.clientObservers.add(o);

    }

    public boolean addLobbyObserver(LobbyObserver o) {

        return o != null && this.lobbyObservers.add(o);

    }

    public boolean addRemotePlayerObserver(RemotePlayerObserver o) {

        return o != null && this.remotePlayerObservers.add(o);

    }

    /*
     * Remote player implementation
     */

    public void notifyModelUpdate(Match model) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onModelUpdate(this, model);

        }

    }

    public void notifyTurnEnabled(Player player, String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onTurnEnabled(this, player, message);

        }

    }

    public void notifyTurnDisabled(Player player, String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onTurnDisabled(this, player, message);

        }

    }

    public void notifyActionTimeoutExpired(Player player, String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onTimeoutExpired(this, player, message);

        }

    }

    public void notifyActionRefused(Action action, String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onActionRefused(this,action, message);

        }

    }

    public void notifyImmediateActionAvailable(ImmediateActionType immediateActionType, Player player, String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onImmediateActionAvailable(this, immediateActionType, player, message);

        }

    }

    public void notifyActionPerformed(Player player, Action action, String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onActionPerformed(this, player, action, message);

        }

    }

    public void notifyLeaderCardDraftRequest(Deck<LeaderCard> deck,  String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onLeaderCardDraftRequest(this, deck, message);

        }

    }

    public void notifyBonusTileDraftRequest(ArrayList<BonusTile> tiles,  String message) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onBonusTileDraftRequest(this, tiles, message);

        }

    }

    public void notify(MatchNotification notification) {

        for (RemotePlayerObserver o : this.remotePlayerObservers) {

            o.onNotification(this, notification);

        }

    }

}
