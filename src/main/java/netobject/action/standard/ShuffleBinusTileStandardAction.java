package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;
import server.model.board.BonusTile;

import java.util.ArrayList;

public class ShuffleBinusTileStandardAction extends Action {

    private final int selection;

    private final ArrayList<BonusTile> tiles;

    public ShuffleBinusTileStandardAction(int selection, ArrayList<BonusTile> tiles, String sender) {

        super(ActionType.Standard, sender);

        this.selection = selection;

        this.tiles = tiles;
    }

    public ArrayList<BonusTile> getTiles() {
        return tiles;
    }

    public int getSelection() {
        return selection;
    }
}
