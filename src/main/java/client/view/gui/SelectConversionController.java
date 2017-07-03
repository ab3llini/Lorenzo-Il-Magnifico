package client.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;

import java.util.HashMap;

/**
 * Created by Federico on 03/07/2017.
 */
public class SelectConversionController {


    private String selected;

    private HashMap<String, Integer> selectionCache;

    @FXML
    private MenuButton privilegeSelectionMenuButton;

    @FXML
    private Label selectionTextField;

    @FXML
    void selectNo(ActionEvent event) {
        this.selectionTextField.setText("No");
        this.selected = "No";

    }

    @FXML
    void selectYes(ActionEvent event) {
        this.selectionTextField.setText("Yes");
        this.selected = "No";


    }
}