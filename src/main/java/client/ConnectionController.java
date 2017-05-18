package client;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

import exception.UsernameAlreadyInUseException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import netobject.RegistrationRequest;

import java.rmi.RemoteException;

public class ConnectionController implements RMIClientObserver {

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

    @FXML // fx:id="connectButton"
    private Button connectButton; // Value injected by FXMLLoader

    RMIClient rmiClient;

    boolean RMIConnectionReady = false;


    @FXML
    void connect() {

        connectButton.setText("Connecting.. See logger");

        try {

            //Initialize the RMI client
            rmiClient = new RMIClient("localhost", 1099, "server");

        }
        catch (RemoteException r) {

            connectButton.setText("Error");

        }

        //Register our self as observer
        rmiClient.addObserver(this);

        //Begin lookup
        rmiClient.start();


    }

    public void RMIConnectionReady() {

        try {

            if (rmiClient.getServerRef().performRegistrationRequest(
                    rmiClient,
                    new RegistrationRequest(usernameTextField.getText())
            ).success) {

                System.out.println("Connected..");

            }
            else {

                System.out.println("Username already taken");

            }

        }
        catch (RemoteException ex) {

            System.out.println(ex.getMessage());

        } catch (UsernameAlreadyInUseException e) {
            e.printStackTrace();
        }

    }
}
