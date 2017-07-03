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
import netobject.action.standard.StandardActionType;
import netobject.action.standard.StandardPlacementAction;
import server.model.board.ColorType;

public class StandardPlacementActionController extends PlacementActionController {

    private BoardSectorType boardSector;
    private ColorType colorType = ColorType.Neutral;

    @FXML
    private TextField additionalServantsTextField;

    @FXML
    private RadioButton orangeColorTypeRadioButton;

    @FXML
    private ToggleGroup colorTypeRadioGroup;

    @FXML
    private RadioButton blackColorTypeRadioButton;

    @FXML
    private RadioButton whiteColorTypeRadioButton;

    @FXML
    private RadioButton neutralColorTypeRadioButton;


    @FXML
    void onPlaceClick(MouseEvent event) {

        if (Utility.isInteger(this.additionalServantsTextField.getText())) {

            this.additionalServants = Integer.parseInt(this.additionalServantsTextField.getText());
            this.colorType = (ColorType)this.colorTypeRadioGroup.getSelectedToggle().getUserData();
            StandardPlacementAction action = new StandardPlacementAction(boardSector, index, colorType, additionalServants, this.client.getUsername());

            this.client.performAction(action);

            this.localMatchController.setLastPendingStandardAction(StandardActionType.FamilyMemberPlacement);

            stage.close();

        }
        else {

            this.showAlert(Alert.AlertType.WARNING, "Invalid input", "Invalid input", "You must enter a number");

        }

    }

    @FXML
    void initialize (){

        this.blackColorTypeRadioButton.setUserData(ColorType.Black);
        this.orangeColorTypeRadioButton.setUserData(ColorType.Orange);
        this.whiteColorTypeRadioButton.setUserData(ColorType.White);
        this.neutralColorTypeRadioButton.setUserData(ColorType.Neutral);

    }

    public void setBoardSector(BoardSectorType boardSector) {
        this.boardSector = boardSector;
    }

}

