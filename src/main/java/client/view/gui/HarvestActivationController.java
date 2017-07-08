package client.view.gui;

/**
 * Created by Federico on 01/07/2017.
 */
import client.controller.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import netobject.action.BoardSectorType;
import netobject.action.ImmediateBoardSectorType;
import netobject.action.immediate.ImmediatePlacementAction;
import netobject.action.standard.StandardActionType;
import netobject.action.standard.StandardPlacementAction;
import server.model.board.ColorType;

public class HarvestActivationController extends PlacementActionController {


    @FXML
    private TextField additionalServantsTextField;


    @FXML
    void onHarvestActivation(MouseEvent event) {

        if (Utility.isInteger(this.additionalServantsTextField.getText())) {

            this.additionalServants = Integer.parseInt(this.additionalServantsTextField.getText());
            ImmediatePlacementAction action = new ImmediatePlacementAction(ImmediateBoardSectorType.Harvest, this.additionalServants, this.client.getUsername());
            this.client.performAction(action);

            stage.close();

        }
        else {

            this.showAlert(Alert.AlertType.WARNING, "Invalid input", "Invalid input", "You must enter a number");

        }

    }

}

