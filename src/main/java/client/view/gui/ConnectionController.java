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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.Level;
import logger.Logger;
import netobject.request.auth.LoginRequest;
import netobject.request.auth.RegisterRequest;
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
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField registerUsernameTextField;

    @FXML
    private TextField resgisterPassTextField;

    @FXML
    private TextField resgisterPassConfTextField;

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

            if (this.client != null && this.client.hasConnected()) {

                this.client.login(new LoginRequest(this.usernameTextField.getText(), this.passwordTextField.getText()));

            }
            else {

                this.attemptConnection(clientSelection).login(new LoginRequest(this.usernameTextField.getText(), this.passwordTextField.getText()));

            }

        } catch (Exception e) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Exception raised", "Connection failed", e.getMessage());

        }


    }

    @FXML
    void registerAction(ActionEvent event) {

        ClientType clientSelection = (ClientType) socketRadioSelection.getToggleGroup().getSelectedToggle().getUserData();


        if (!this.registerUsernameTextField.getText().equals("")) {

            if (this.resgisterPassTextField.getText().equals(this.resgisterPassConfTextField.getText())) {

                try {

                    if (this.client != null && this.client.hasConnected()) {

                        this.client.registration(new RegisterRequest(this.registerUsernameTextField.getText(), this.resgisterPassTextField.getText()));

                    }
                    else {

                        this.attemptConnection(clientSelection).registration(new RegisterRequest(this.registerUsernameTextField.getText(), this.resgisterPassTextField.getText()));

                    }


                } catch (Exception e) {

                    this.showAsynchAlert(Alert.AlertType.ERROR, "Exception raised", "Connection failed", e.getMessage());

                }

            }
            else {

                this.showAsynchAlert(Alert.AlertType.ERROR, "Registration error", "Password missmatch", "The password must be confirmed");

            }

        }
        else {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Registration error", "Wrong username", "Pick a valid username");


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
        this.showAsynchAlert(Alert.AlertType.WARNING, "Disconnection", "You were disconnected from the server", "");

    }

    @Override
    public void onLoginFailed(Client client, String reason) {

        this.showAsynchAlert(Alert.AlertType.ERROR, "Authentication", "Login failed", reason);

    }
    @Override
    public void onLoginSuccess(Client client) {

        Platform.runLater(() -> ((LobbyController)this.navigateTo(View.Lobby)).setClient(client));

    }

    @Override
    public void onRegistrationSuccess(Client client) {

        Platform.runLater(() -> ((LobbyController)this.navigateTo(View.Lobby)).setClient(client));


    }

    @Override
    public void onRegistrationFailed(Client client, String reason) {

        this.showAsynchAlert(Alert.AlertType.ERROR, "Authentication", "Registration failed", reason);


    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);

        stage.setOnCloseRequest((WindowEvent e) -> {

            //Terminate the process upon closure
            System.exit(0);

        });

    }

}


