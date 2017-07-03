package client.controller.network.Socket;

import client.controller.network.Client;
import logger.Level;
import logger.Logger;
import netobject.NetObjectType;
import netobject.action.Action;
import netobject.notification.*;
import netobject.request.auth.LoginRequest;
import netobject.NetObject;
import netobject.request.auth.RegisterRequest;
import netobject.response.Response;
import netobject.response.ResponseType;
import netobject.response.auth.LoginResponse;
import netobject.response.auth.RegistrationResponse;
import server.model.Match;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

    private ObjectInputStream in;

    private ObjectOutputStream out;

    public SocketClient(String host, int port) {

        //Assign host & port
        this.host = host;
        this.port = port;


    }

    //Interface implementation
    public void connect() throws Exception {

        //Create the tcp socket
        try {

            this.socket = new Socket(this.host, this.port);


        } catch (IOException e) {

            Logger.log(Level.SEVERE, "SocketClient::connect", "Unable to connect.", e);

            throw e;

        }

        try {

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {

            Logger.log(Level.SEVERE, this.toString(), "Unable to get socket streams", e);

            throw e;

        }

        new Thread(this).start();

        Logger.log(Level.FINE, "SocketClient::connect", "Connected.");

    }

    public void login(LoginRequest authentication) {

        this.sendObject(authentication);

    }

    public void registration(RegisterRequest authentication){

        this.sendObject(authentication);

    }

    public void run() {

        while (!this.socket.isClosed() && this.socket.isConnected()) {

            try {

                //Try to read the object
                Object obj = in.readObject();

                //Notify that an object was received
                this.parse((NetObject) obj);

            } catch (EOFException e) {

                Logger.log(Level.WARNING, "SocketClient::run", "EOFException, server might have just closed the connection", e);

                break;

            } catch (IOException e) {

                Logger.log(Level.WARNING, "SocketClient::run", "THIS IS A BUG THAT I AM TRYING TO FIX; BE PATIENT.", e);

                break;


            } catch (ClassNotFoundException e) {

                Logger.log(Level.WARNING, "SocketClient::run", "Cast error", e);

                break;

            } finally {

                if (this.socket.isClosed() || !this.socket.isConnected()) {

                    //Notify observers so that they can remove the handler
                    this.notifyDisconnection();

                }

            }
        }
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
            else if (resp.getResponseType() == ResponseType.Registration) {

                RegistrationResponse registrationResponse = (RegistrationResponse)resp;

                if (registrationResponse.registerHasSucceeded()) {

                    this.username = registrationResponse.getUsername();

                    this.authenticated = true;

                    this.notifyRegistrationSucceeded();

                }

                else {

                    this.notifyRegistrationFailed(registrationResponse.getMessage());

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

                    this.notifyActionRefused(matchNot.getAction(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.ActionPerformed) {

                    this.notifyActionPerformed(matchNot.getPlayer(), matchNot.getAction(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.LeaderDraft) {

                    this.notifyLeaderCardDraftRequest(matchNot.getDeck(), matchNot.getMessage());

                }
                else if (matchNot.getMatchNotificationType() == MatchNotificationType.BonusTileDraft) {

                    this.notifyBonusTileDraftRequest(matchNot.getTiles(), matchNot.getMessage());

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
    private synchronized boolean sendObject(NetObject object) {

        try {

            out.writeObject(object);

            out.flush();

            out.reset();

            return true;

        } catch (IOException e) {

            Logger.log(Level.SEVERE, "SocketClient::SendObject", "IO Exception.");

        }

        return false;

    }


    public void performAction(Action action) {

        this.sendObject(action);

    }

    @Override
    public void sendNotification(Notification notification) {

        this.sendObject(notification);

    }
}
