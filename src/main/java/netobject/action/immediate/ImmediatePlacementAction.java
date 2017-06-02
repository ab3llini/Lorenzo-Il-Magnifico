package netobject.action.immediate;


import netobject.action.BoardSectorType;
import netobject.action.SelectionType;
import netobject.action.Action;
import netobject.action.ActionType;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public class ImmediatePlacementAction extends Action {

    private final ImmediateActionType immediateActionType;

    private final BoardSectorType actionTarget; //sector of the board which is target of the Action

    private final Integer placementIndex; //slot index of the board sector

    private final SelectionType costOptionType;

    private int additionalServants;


    public ImmediatePlacementAction(ImmediateActionType immediateActionType, BoardSectorType actionTarget, Integer placementIndex, SelectionType costOptionType) {

        super(ActionType.Immediate);

        this.immediateActionType = immediateActionType;
        this.actionTarget = actionTarget;
        this.placementIndex = placementIndex;
        this.costOptionType = costOptionType;

    }

    public ImmediateActionType getImmediateActionType() {
        return immediateActionType;
    }

    public void increaseBonus(int bonus ) {
        this.additionalServants += bonus;
    }


}
