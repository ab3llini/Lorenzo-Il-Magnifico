package client.view.gui;

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.NetUtil;
import client.controller.network.RMI.RMIClient;
import client.controller.network.Socket.SocketClient;
import client.view.cli.cmd.ClientType;
import client.view.gui.lobby.LobbyController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logger.Level;
import logger.Logger;
import netobject.request.auth.LoginRequest;
import singleton.GameConfig;

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

            this.client.addClientObserver(this);

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
            }
        });
    }

    @Override
    public void onLoginSuccess(Client client) {

        Platform.runLater(new Runnable() {
            @Override public void run() {
                ((LobbyController)ConnectionController.this.navigateTo(View.Lobby)).setClient(client);

            }
        });


    }

    @Override
    public void onRegistrationSuccess(Client client) {

    }

    @Override
    public void onRegistrationFailed(Client client, String reason) {

    }

}


