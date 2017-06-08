package netobject.action.standard;

import netobject.action.*;
import server.model.board.ColorType;
import server.model.valuable.Resource;

import java.util.ArrayList;

/**
 * Created by Federico on 22/05/2017.
 * Edited by ab3llini on 23/05/2017
 */

public class StandardPlacementAction extends Action {

    private final BoardSectorType actionTarget; //sector of the board which is target of the Action

    private final Integer placementIndex; //slot index of the board sector

    private final ColorType colorType;

    private Integer additionalServants;

    private final SelectionType costOptionType;

    private ArrayList<Resource> discount;

    public StandardPlacementAction(BoardSectorType actionTarget, Integer placementIndex, ColorType colorType, int additionalServants, SelectionType costOptionType, String sender) {

        super(ActionType.Standard, sender);
        this.actionTarget = actionTarget;
        this.placementIndex = placementIndex;
        this.colorType = colorType;
        this.additionalServants = additionalServants;
        this.costOptionType = costOptionType;
        this.discount = new ArrayList<>();
    }

    public BoardSectorType getActionTarget() {
            return actionTarget;
    }

    public Integer getPlacementIndex() {
            return placementIndex;
    }

    public ColorType  getColorType() {
            return colorType;
    }

    public SelectionType getCostOptionType() {
            return costOptionType;
    }

    public int getAdditionalServants() {
            return additionalServants;
    }

    public void setAdditionalServants(Integer additionalServants) {
        this.additionalServants = additionalServants;
    }

    public void increaseBonus(int bonus) {

        this.additionalServants += bonus;

    }

    public void setDiscount(ArrayList<Resource> discount) {
        this.discount = discount;}

    public ArrayList<Resource> getDiscount() {
        return discount;
    }
}
