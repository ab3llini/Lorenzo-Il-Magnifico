package client.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netobject.action.immediate.ImmediateChoiceAction;
import server.model.board.CouncilPrivilege;

import java.util.HashMap;

/**
 * Created by Federico on 03/07/2017.
 */
public class SelectCostController extends  DialogController {


    private int selected = 0;

    @FXML
    private MenuButton privilegeSelectionMenuButton;

    @FXML
    private Label costTextField;

    @FXML
    void selectFirst(ActionEvent event) {
        this.costTextField.setText("First");
        this.selected = 0;

    }

    @FXML
    void selectSecond(ActionEvent event) {
        this.costTextField.setText("Second");
        this.selected = 1;


    }



    @FXML
    void onSelectClick(MouseEvent event) {
        if (this.selected >= 0) {

            //Useful to get the corresponding index
            this.client.performAction(new ImmediateChoiceAction(this.selected, this.client.getUsername()));

            //Close the stage
            stage.close();


        }
        else {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Forbidden", "Invalid selection", "You must select a cost!");

        }
    }

    /**
     * Very important to override this in order to detect windows closure and send a default action
     * @param stage the stage loaded
     */
    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        stage.setOnCloseRequest((WindowEvent e) -> {
            //We send the first choice always before closing the stage!
            this.client.performAction(new ImmediateChoiceAction(this.selected, this.client.getUsername()));

        });
    }

}
