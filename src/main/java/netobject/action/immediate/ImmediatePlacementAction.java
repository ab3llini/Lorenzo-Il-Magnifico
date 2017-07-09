package netobject.action.immediate;


import netobject.action.*;
import server.model.card.developement.Discount;
import server.model.valuable.Resource;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public class ImmediatePlacementAction extends Action {

    private final ImmediateBoardSectorType actionTarget; //sector of the board which is target of the Action

    private Integer placementIndex; //slot index of the board sector

    private int additionalServants;

    private ArrayList<Discount> discounts;

    public ImmediatePlacementAction(ImmediateBoardSectorType actionTarget, Integer placementIndex, int additionalServants, String sender) {

        super(ActionType.Immediate, sender);

        this.actionTarget = actionTarget;
        this.placementIndex = placementIndex;
        this.additionalServants = additionalServants;
        this.discounts = new ArrayList<>();

    }

    public ImmediatePlacementAction(ImmediateBoardSectorType actionTarget, int additionalServants, String sender) {

        super(ActionType.Immediate, sender);

        this.actionTarget = actionTarget;
        this.additionalServants = additionalServants;
        this.discounts = new ArrayList<>();

    }

    public Integer getPlacementIndex() {
        return placementIndex;
    }

    public ImmediateBoardSectorType getActionTarget() {
        return actionTarget;
    }

    public int getAdditionalServants() {
        return additionalServants;
    }

    public void setAdditionalServants(int additionalServants) {
        this.additionalServants = additionalServants;
    }

    public ArrayList<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(ArrayList<Discount> discounts) {
        this.discounts = discounts;
    }
}
