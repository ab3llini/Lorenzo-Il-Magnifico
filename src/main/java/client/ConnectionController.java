package client;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.rmi.RemoteException;

public class ConnectionController {

    @FXML // fx:id="portTextField"
    private TextField portTextField; // Value injected by FXMLLoader

    @FXML // fx:id="hostTextField"
    private TextField hostTextField; // Value injected by FXMLLoader

    @FXML // fx:id="usernameTextField"
    private TextField usernameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="rmiRadioButton"
    private RadioButton rmiRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="connectionType"
    private ToggleGroup connectionType; // Value injected by FXMLLoader

    @FXML // fx:id="socketRadioButton"
    private RadioButton socketRadioButton; // Value injected by FXMLLoader

    @FXML
    void connect() {

        RMIClient rmiClient;

        try {

            rmiClient = new RMIClient(hostTextField.getText(), Integer.parseInt(portTextField.getText()), "server");

        }
        catch (RemoteException e) {

            e.printStackTrace();

            return;

        }



    }

}
