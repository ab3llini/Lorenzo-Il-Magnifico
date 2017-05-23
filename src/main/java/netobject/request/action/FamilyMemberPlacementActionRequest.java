package netobject.request.action;

import server.model.board.FamilyMember;

/**
 * Created by Federico on 22/05/2017.
 * Edited by ab3llini on 23/05/2017
 */

public class FamilyMemberPlacementActionRequest extends ActionRequest {

    private final BoardSectorType actionTarget; //sector of the board which is target of the action

    private final Integer placementIndex; //slot index of the board sector

    private final FamilyMember familyMember;

    private final int additionalServants;

    private final CostOptionType costOptionType;

    public FamilyMemberPlacementActionRequest(BoardSectorType actionTarget, Integer placementIndex, FamilyMember familyMember, int additionalServants, CostOptionType costOptionType) {

        super(ActionType.FamilyMemberPlacement);

        this.actionTarget = actionTarget;
        this.placementIndex = placementIndex;
        this.familyMember = familyMember;
        this.additionalServants = additionalServants;
        this.costOptionType = costOptionType;

    }

    public BoardSectorType getActionTarget() {
            return actionTarget;
    }

    public Integer getPlacementIndex() {
            return placementIndex;
    }

    public FamilyMember getFamilyMember() {
            return familyMember;
    }

    public CostOptionType getCostOptionType() {
            return costOptionType;
    }

    public int getAdditionalServants() {
            return additionalServants;
    }
}
