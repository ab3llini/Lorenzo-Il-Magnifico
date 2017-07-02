package client.view.gui;

import exception.gui.GuiException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import server.model.Match;
import server.model.card.developement.DvptCard;
import server.model.card.leader.LeaderCard;

import java.util.HashMap;

/**
 * Created by Federico on 02/07/2017.
 */
public class LeaderCardDraftController extends NavigationController{

    HashMap<ImageView, LeaderCard> imageViewLeaderCardCache = new HashMap<>();

    private synchronized void updatedLeaderCardGrid(Match model){

    }

    @FXML
        void onLeaderCardClick(MouseEvent event) {
            ImageView clicked = (ImageView) event.getSource();

            try {

                this.showAsynchAlert(Alert.AlertType.CONFIRMATION, "Click", "Click on card", "You clicked on the card with id " + this.getLeaderCardFromImageView(clicked));

            } catch (GuiException e) {

                this.showAsynchAlert(Alert.AlertType.ERROR, "GuiException", "Card not found", e.getMessage());


            }
        }

        private LeaderCard getLeaderCardFromImageView (ImageView view) throws  GuiException{

            LeaderCard requested = this.imageViewLeaderCardCache.get(view);

            if (requested == null) {

                throw new GuiException("No card set for the provided image view");

            }
            else {

                return requested;

            }
        }

}

