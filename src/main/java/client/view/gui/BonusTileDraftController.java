package client.view.gui;

/**
 * Created by Alberto on 06/07/2017.
 */

import client.view.LocalMatchController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import netobject.action.standard.ShuffleBonusTileStandardAction;
import server.model.board.BonusTile;

import java.util.ArrayList;
import java.util.HashMap;


public class BonusTileDraftController extends DialogController {

    @FXML
    private ImageView tile0;

    @FXML
    private ImageView tile1;

    @FXML
    private ImageView tile2;

    @FXML
    private ImageView tile3;

    @FXML
    private ImageView tile4;

    private ArrayList<BonusTile> tiles;

    private HashMap<Integer, ImageView> tilesCache;

    private Integer selection = 0;

    @FXML
    void onTileSelection(MouseEvent event) {

        Integer clicked = GridPane.getColumnIndex((Node) event.getSource());

        if (clicked + 1 > tiles.size()) return;

        selection = clicked;

        //Send the action
        this.client.performAction(new ShuffleBonusTileStandardAction(selection, this.tiles, this.client.getUsername()));

        //Close the stage
        stage.close();

    }

    @FXML
    void initialize() {

        //Build a cache for further usage
        this.tilesCache = new HashMap<>();
        this.tilesCache.put(0, tile0);
        this.tilesCache.put(1, tile1);
        this.tilesCache.put(2, tile2);
        this.tilesCache.put(3, tile3);
        this.tilesCache.put(4, tile4);

    }

    @Override
    public void setLocalMatchController(LocalMatchController localMatchController) {
        super.setLocalMatchController(localMatchController);

        tiles = this.localMatchController.getDraftableBonusTiles();

        //Display the cards
        for (int index = 0; index < tiles.size(); index++) {

            Integer id = tiles.get(index).getId();
            String url = "assets/tiles/personalbonustile_"+id+".png";
            this.tilesCache.get(index).setImage(new Image(url));

        }

        stage.setOnCloseRequest((WindowEvent e) -> {
            //We send the first choice always before closing the stage!
            this.client.performAction(new ShuffleBonusTileStandardAction(selection, this.tiles, this.client.getUsername()));

        });

    }

}
