package netobject.request.action;

import netobject.request.Request;
import netobject.request.RequestType;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public class ImmediateActionRequest extends Request {

    private final ImmediateActionType immediateActionType;

    private final BoardSectorType actionTarget; //sector of the board which is target of the action

    private final Integer placementIndex; //slot index of the board sector

    private final CostOptionType costOptionType;


    public ImmediateActionRequest(ImmediateActionType immediateActionType, BoardSectorType actionTarget, Integer placementIndex, CostOptionType costOptionType) {

        super(RequestType.ImmediateAction);

        this.immediateActionType = immediateActionType;
        this.actionTarget = actionTarget;
        this.placementIndex = placementIndex;
        this.costOptionType = costOptionType;
    }

    public ImmediateActionType getImmediateActionType() {
        return immediateActionType;
    }
}
