package netobject.action.immediate;


import netobject.action.*;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public class ImmediatePlacementAction extends Action {


    private final ImmediateBoardSectorType actionTarget; //sector of the board which is target of the Action

    private final Integer placementIndex; //slot index of the board sector

    private final SelectionType costOptionType;

    private int additionalServants;


    public ImmediatePlacementAction(ImmediateBoardSectorType actionTarget, Integer placementIndex, SelectionType costOptionType) {

        super(ActionType.Immediate);

        this.actionTarget = actionTarget;
        this.placementIndex = placementIndex;
        this.costOptionType = costOptionType;

    }

    public void increaseBonus(int bonus ) {
        this.additionalServants += bonus;
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

    public SelectionType getCostOptionType() {
        return costOptionType;
    }
}
