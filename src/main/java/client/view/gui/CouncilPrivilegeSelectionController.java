package client.view.gui;/*
 * Created by albob on 03/07/2017.
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class CouncilPrivilegeSelectionController extends DialogController {

    private CouncilPrivilege selected;

    private HashMap<CouncilPrivilege, Integer> selectionCache;

    @FXML
    private MenuButton privilegeSelectionMenuButton;

    @FXML
    private Label selectionTextField;

    public CouncilPrivilegeSelectionController() {

        this.selectionCache = new HashMap<>();

        //Build the cache
        int value = 0;
        for (CouncilPrivilege privilege : CouncilPrivilege.values()) {

            this.selectionCache.put(privilege, value);
            value++;

        }


    }


    @FXML
    void onSelectClick(MouseEvent event) {
        if (this.selected != null) {

            //Useful to get the corresponding index
            this.client.performAction(new ImmediateChoiceAction(this.selectionCache.get(this.selected), this.client.getUsername()));

            //Close the stage
            stage.close();


        }
        else {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Forbidden", "Invalid selection", "You must select a privilege!");

        }
    }

    @FXML
    void selectCoins(ActionEvent event) {
        this.selected = CouncilPrivilege.Coins;
        this.selectionTextField.setText(this.selected.toString());
    }

    @FXML
    void selectFaith(ActionEvent event) {
        this.selected = CouncilPrivilege.FaithPoints;
        this.selectionTextField.setText(this.selected.toString());


    }

    @FXML
    void selectMilitary(ActionEvent event) {
        this.selected = CouncilPrivilege.MilitaryPoints;
        this.selectionTextField.setText(this.selected.toString());


    }

    @FXML
    void selectServants(ActionEvent event) {
        this.selected = CouncilPrivilege.Servants;
        this.selectionTextField.setText(this.selected.toString());


    }

    @FXML
    void selectWoodAndStones(ActionEvent event) {
        this.selected = CouncilPrivilege.WoodsAndStones;
        this.selectionTextField.setText(this.selected.toString());


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
            this.client.performAction(new ImmediateChoiceAction(0, this.client.getUsername()));

        });
    }
}
