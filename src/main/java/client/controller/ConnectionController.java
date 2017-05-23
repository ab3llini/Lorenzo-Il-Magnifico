package client.controller;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

import client.NavigationController;
import client.controller.network.RMI.RMIClient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;


public class ConnectionController extends NavigationController{

    @FXML // fx:id="portTextField"
    private TextField portTextField; // Value injected by FXMLLoader

    @FXML // fx:id="hostTextField"
    private TextField hostTextField; // Value injected by FXMLLoader

    @FXML // fx:id="usernameTextField"
    private TextField usernameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordTextField"
    private TextField passwordTextField; // Value injected by FXMLLoader

    @FXML // fx:id="rmiRadioButton"
    private RadioButton rmiRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="connectionType"
    private ToggleGroup connectionType; // Value injected by FXMLLoader

    @FXML // fx:id="socketRadioButton"
    private RadioButton socketRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="connectButton"
    private Button connectButton; // Value injected by FXMLLoader

    RMIClient rmiClient;

    boolean RMIConnectionReady = false;


    @FXML
    void connect() {




    }

}
