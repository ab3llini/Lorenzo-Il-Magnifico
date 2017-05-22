package netobject;

import server.model.board.Board;
import server.model.board.FamilyMember;

/**
 * Created by Federico on 22/05/2017.
 */
public class FamilyPlacementAction extends NetObject {

        BoardSector actionTarget; //sector of the board which is target of the action

        Integer placementIndex; //slot index of the board sector

        FamilyMember familyMember;

        public FamilyPlacementAction(BoardSector actionTarget,Integer placementIndex, FamilyMember familyMember){
                this.actionTarget = actionTarget;
                this.placementIndex = placementIndex;
                this.familyMember = familyMember;
        }

        public BoardSector getActionTarget() {
                return actionTarget;
        }

        public Integer getPlacementIndex() {
                return placementIndex;
        }

        public FamilyMember getFamilyMember() {
                return familyMember;}
}
