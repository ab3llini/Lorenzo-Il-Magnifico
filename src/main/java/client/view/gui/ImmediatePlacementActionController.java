package client.view.gui;

import client.controller.Utility;
import client.view.gui.lobby.PlacementActionController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import netobject.action.ImmediateBoardSectorType;
import netobject.action.immediate.ImmediatePlacementAction;
import netobject.action.standard.StandardActionType;

/*
 * @author  ab3llini
 * @since   02/07/17.
 */
public class ImmediatePlacementActionController extends PlacementActionController {

    private ImmediateBoardSectorType immediateBoardSectorType;

    @FXML
    private TextField additionalServantsTextField;

    @FXML
    void onTakeCardClick(MouseEvent event) {


        if (Utility.isInteger(this.additionalServantsTextField.getText())) {

            this.additionalServants = Integer.parseInt(this.additionalServantsTextField.getText());
            ImmediatePlacementAction action = new ImmediatePlacementAction(immediateBoardSectorType, index, additionalServants, this.client.getUsername());

            this.client.performAction(action);
            this.localMatchController.setLastPendingStandardAction(StandardActionType.FamilyMemberPlacement);

            stage.close();

        }
        else {

            this.showAlert(Alert.AlertType.WARNING, "Invalid input", "Invalid input", "You must enter a number");

        }

    }

    public void setImmediateBoardSectorType(ImmediateBoardSectorType immediateBoardSectorType) {
        this.immediateBoardSectorType = immediateBoardSectorType;
    }
}
