package client.controller.network.Socket;

import client.controller.network.Client;
import logger.Level;
import logger.Logger;
import netobject.NetObjectType;
import netobject.action.Action;
import netobject.notification.*;
import netobject.request.auth.LoginRequest;
import netobject.NetObject;
import netobject.response.Response;
import netobject.response.ResponseType;
import netobject.response.auth.LoginResponse;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

/**
 * Provides socket client functionality
 */
public class SocketClient extends Client implements Runnable {

    //The socket
    private Socket socket;

    //Host properties
    private String host;

    //The port of the host
    private int port;

    ObjectOutputStream socketOut;

    public SocketClient(String host, int port) {

        //Assign host & port
        this.host = host;
        this.port = port;

    }

    //Interface implementation
    public boolean connect() {

        //Create the tcp socket
        try {

            this.socket = new Socket(this.host, this.port);

            new Thread(this).start();

            Logger.log(Level.FINE, "SocketClient::connect", "Connected.");

            return true;

        } catch (IOException e) {

            Logger.log(Level.SEVERE, "SocketClient::connect", "Unable to connect.", e);

        }

        return false;

    }

    public void login(LoginRequest authentication) {

        this.sendObject(authentication);

    }

    public void run() {

        try {
            this.socketOut = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!this.socket.isClosed() && this.socket.isConnected()) {

            try {


                ObjectInputStream socketIn = new ObjectInputStream(this.socket.getInputStream());

                //Try to read the object
                Object obj = socketIn.readObject();

                //Notify that an object was received
                this.parse((NetObject) obj);

            }
            catch (EOFException e) {

                //Notify observers so that they can remove the handler
                this.notifyDisconnection();

                break;

            }
            catch (IOException e) {

                Logger.log(Level.WARNING, "SocketClient::run", "Broken pipe while listening", e);

                this.notifyDisconnection();

                break;

            }
            catch (ClassNotFoundException e) {

                Logger.log(Level.WARNING, "SocketClient::run", "Cast error", e);

                break;
            }

        }

        this.notifyDisconnection();

    }

    private void parse(NetObject object) {

        if (object.getType() == NetObjectType.Response) {

            Response resp = (Response)object;

            if (resp.getResponseType() == ResponseType.Login) {

                LoginResponse loginResponse = (LoginResponse)resp;

                if (loginResponse.loginHasSucceeded()) {

                    this.username = loginResponse.getUsername();

                    this.authenticated = true;

                    this.notifyLoginSucceeded();

                }

                else {

                    this.notifyLoginFailed(loginResponse.getMessage());

                }

            }

        }
        else if (object.getType() == NetObjectType.Notification) {

            Notification not = (Notification)object;

            if (not.getNotificationType() == NotificationType.Lobby) {

                this.notifyLobbyNotificationReceived((LobbyNotification)not);

            }
            else if (not.getNotificationType() == NotificationType.Match) {

                MatchNotification matchNot = (MatchNotification)not;

                if (matchNot.getMatchNotificationType() == MatchNotificationType.TurnEnabled) {

                    this.notifyTurnEnabled(matchNot.getPlayer(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.TurnDisabled) {

                    this.notifyTurnDisabled(matchNot.getPlayer(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.ImmediateAction) {

                    this.notifyImmediateActionAvailable(matchNot.getActionType(), matchNot.getPlayer(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.TimeoutExpired) {

                    this.notifyActionTimeoutExpired(matchNot.getPlayer(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.ActionRefused) {

                    this.notifyActionRefused(matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.ActionPerformed) {

                    this.notifyActionPerformed(matchNot.getPlayer(), matchNot.getAction(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.LeaderDraft) {

                    this.notifyLeaderCardDraftRequest(matchNot.getDeck(), matchNot.getMessage());

                }
                else {

                    this.notify(matchNot);

                }

            }

        }
        else if (object.getType() == NetObjectType.Model) {

            this.notifyModelUpdate((Match)object);

        }

    }

    /**
     * Method that should be implemented to send an object to the server
     * @param object the object to be sent
     * @return true upon success, false otherwise.
     */
    private boolean sendObject(NetObject object) {

        try {

            socketOut.flush();

            socketOut.writeObject(object);

            return true;

        } catch (IOException e) {

            Logger.log(Level.SEVERE, "SocketClient::SendObject", "IO Exception.");

        }

        return false;

    }


    public void performAction(Action action) {

        this.sendObject(action);

    }

}
