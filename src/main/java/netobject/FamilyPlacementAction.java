package netobject;

import server.model.board.FamilyMember;

/**
 * Created by Federico on 22/05/2017.
 */
public class FamilyPlacementAction extends NetObject {

        private final BoardSector actionTarget; //sector of the board which is target of the action

        private final Integer placementIndex; //slot index of the board sector

        private final FamilyMember familyMember;

        private final int additionalServants;

        private final CostOption costOption;

        public FamilyPlacementAction(BoardSector actionTarget, Integer placementIndex, FamilyMember familyMember, int additionalServants, CostOption costOption) {

                this.actionTarget = actionTarget;
                this.placementIndex = placementIndex;
                this.familyMember = familyMember;
                this.additionalServants = additionalServants;
                this.costOption = costOption;

        }

        public BoardSector getActionTarget() {
                return actionTarget;
        }

        public Integer getPlacementIndex() {
                return placementIndex;
        }

        public FamilyMember getFamilyMember() {
                return familyMember;
        }

        public CostOption getCostOption() {
                return costOption;
        }

        public int getAdditionalServants() {
                return additionalServants;
        }
}
