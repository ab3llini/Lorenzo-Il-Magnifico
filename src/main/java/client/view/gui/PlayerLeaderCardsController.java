package client.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import netobject.action.standard.DiscardLeaderCardAction;
import netobject.action.standard.LeaderCardActivationAction;
import server.model.card.leader.LeaderCard;

/**
 * Created by Federico on 03/07/2017.
 */


public class PlayerLeaderCardsController extends  DialogController {


    private int selection = -1;

    private LeaderCard clicked = null;


    @FXML
    private Label leaderTextField;

    @FXML
    void selectDiscard(ActionEvent event) {
        this.leaderTextField.setText("Discard");
        this.selection = 1;

    }

    @FXML
    void selectActivate(ActionEvent event) {
        this.leaderTextField.setText("Activate / Play");
        this.selection = 0;


    }



    @FXML
    void onSelectClick(MouseEvent event) {

        if (this.selection == 1) {

            this.client.performAction(new DiscardLeaderCardAction(this.clicked.getId(), this.client.getUsername()));

            this.cleanUp();


        }
        else if (this.selection == 0) {

            this.client.performAction(new LeaderCardActivationAction(this.clicked.getId(), this.client.getUsername()));

            this.cleanUp();

        }
        else {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Forbidden", "Invalid selection", "You must select a ban option!");

        }

    }

    private void cleanUp() {

        //Close the stage
        stage.close();

    }


    public void setClicked(LeaderCard clicked) {
        this.clicked = clicked;
    }

}
