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
import server.model.board.ColorType;
import server.model.board.ColoredColorType;
import server.model.board.CouncilPrivilege;

import java.util.HashMap;

public class SelectFamilyMemberController extends DialogController {

    private ColoredColorType selected;

    private HashMap<ColoredColorType, Integer> selectionCache;


    @FXML
    private Label selectionTextField;

    public SelectFamilyMemberController() {

        this.selectionCache = new HashMap<>();
        for(ColoredColorType color: ColoredColorType.values()) {
            this.selectionCache.put(color, color.getValue());
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
    void selectWhite (ActionEvent event) {
        this.selected = ColoredColorType.White;
        this.selectionTextField.setText(this.selected.toString());
    }

    @FXML
    void selectBlack (ActionEvent event) {
        this.selected = ColoredColorType.Black;
        this.selectionTextField.setText(this.selected.toString());


    }

    @FXML
    void selectOrange(ActionEvent event) {
        this.selected = ColoredColorType.Orange;
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
