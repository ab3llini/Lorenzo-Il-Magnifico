package server.model.board;

/**
 * Created by Federico on 11/05/2017.
 */
public class ActionArea {
    private ActionType type;
    private SingleActionPlace mainPlace;
    private PHCompositeActionPlace secondaryPlace;


    public ActionArea (ActionType type, SingleActionPlace mainPlace, PHCompositeActionPlace secondaryPlace){
        this.type = type;
        this.mainPlace = mainPlace;
        this.secondaryPlace = secondaryPlace;
    }

}
