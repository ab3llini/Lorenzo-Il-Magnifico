package client.view.gui;

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.NetUtil;
import client.controller.network.RMI.RMIClient;
import client.controller.network.Socket.SocketClient;
import client.view.cli.cmd.ClientType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.notification.MatchNotification;
import netobject.request.auth.LoginRequest;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;
import singleton.GameConfig;

import java.util.ArrayList;

public class ConnectionController extends NavigationController implements ClientObserver {

    @FXML
    private RadioButton socketRadioSelection;

    @FXML
    private ToggleGroup connectionRadio;

    @FXML
    private RadioButton rmiRadioSelection;

    @FXML
    private TextField hostAddressTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField usernameTextField1;

    @FXML
    private TextField usernameTextField11;

    @FXML
    private TextField usernameTextField12;

    @FXML
    private TextField usernameTextField13;

    @FXML
    private TextField usernameTextField131;

    private Client client;


    @FXML
    void initialize() {

        // Prepare some data
        this.socketRadioSelection.setUserData(ClientType.Socket);
        this.rmiRadioSelection.setUserData(ClientType.RMI);

        Logger.setSilenced(false);

    }

    @FXML
    void loginAction(ActionEvent event) {

        ClientType clientSelection = (ClientType) socketRadioSelection.getToggleGroup().getSelectedToggle().getUserData();

        try {

            this.attemptConnection(clientSelection).login(new LoginRequest(this.usernameTextField.getText(), this.passwordTextField.getText()));

        } catch (Exception e) {

            this.showAlert(Alert.AlertType.ERROR, "Exception raised", "Connection failed", e.getMessage());

        }


    }

    private Client attemptConnection(ClientType cType) throws Exception {

        Client client = null;
        String ipv4 = this.hostAddressTextField.getText();

        if (NetUtil.isIPv4(ipv4)) {

            switch (cType) {

                case RMI:

                    client = new RMIClient(ipv4, GameConfig.getInstance().getRmiPort(), "server");

                    break;

                case Socket:

                    client = new SocketClient(ipv4, GameConfig.getInstance().getSocketPort());

                    break;

                default:

                    Logger.log(Level.SEVERE, this.toString(), "Not a valid switch case");

            }

            client.connect();

            this.client = client;

            this.client.addObserver(this);

            return client;

        }

        throw new Exception("The string " + ipv4 + " is not a valid IPv4 address");

    }

    @Override
    public String toString() {
        return "ConnectionController";
    }

    @Override
    public void onDisconnection(Client client) {
        this.showAlert(Alert.AlertType.WARNING, "Disconnection", "You were disconnected from the server", "");

    }

    @Override
    public void onLoginFailed(Client client, String reason) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                ConnectionController.this.showAlert(Alert.AlertType.ERROR, "Authentication", "Login failed", "Username or password already in use or already logged in");

                ConnectionController.this.navigateTo(View.Gui);

            }
        });
    }

    @Override
    public void onLoginSuccess(Client client) {

        Platform.runLater(new Runnable() {
            @Override public void run() {
                ConnectionController.this.showAlert(Alert.AlertType.CONFIRMATION, "Authentication", "Login succeeded", "Welcome back " + ConnectionController.this.client.getUsername());
            }
        });


    }

    @Override
    public void onLobbyNotification(Client client, LobbyNotification not) {

    }

    @Override
    public void onNotification(Client sender, MatchNotification notification) {

    }

    @Override
    public void onModelUpdate(Client sender, Match model) {

    }

    @Override
    public void onTurnEnabled(Client sender, Player player, String message) {

    }

    @Override
    public void onImmediateActionAvailable(Client sender, ImmediateActionType actionType, Player player, String message) {

    }

    @Override
    public void onTurnDisabled(Client sender, Player player, String message) {

    }

    @Override
    public void onTimeoutExpired(Client sender, Player player, String message) {

    }

    @Override
    public void onActionRefused(Client sender,Action action, String message) {

    }

    @Override
    public void onActionPerformed(Client sender, Player player, Action action, String message) {

    }

    @Override
    public void onLeaderCardDraftRequest(Client sender, Deck<LeaderCard> cards, String message) {

    }

    @Override
    public void onBonusTileDraftRequest(Client sender, ArrayList<BonusTile> tiles, String message) {

    }
}


