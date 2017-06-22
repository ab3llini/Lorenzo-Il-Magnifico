package client.view;

import client.view.cli.cmd.ClientType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class ConnectionController extends NavigationController {

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

    @FXML
    void initialize() {

        // Prepare some data
        this.socketRadioSelection.setUserData(ClientType.Socket);
        this.rmiRadioSelection.setUserData(ClientType.RMI);

    }

    @FXML
    void loginAction(ActionEvent event) {

        System.out.println((ClientType)socketRadioSelection.getToggleGroup().getSelectedToggle().getUserData());


        try {

            FXMLLoader l = new FXMLLoader(getClass().getResource("/fxml/gui.fxml"));


            Parent root = l.load();



        this.stage.setTitle("GUI");

        this.stage.setScene(new Scene(root, 1000, 800));


        } catch (IOException e) {

            e.printStackTrace();

        }

    }


}


