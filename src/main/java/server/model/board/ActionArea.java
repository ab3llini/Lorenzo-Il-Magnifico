package server.model.board;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 */
public class ActionArea implements Serializable {
    private ActionType type;
    private SingleActionPlace mainPlace;
    private PHCompositeActionPlace secondaryPlace;


    public ActionArea (ActionType type, SingleActionPlace mainPlace, PHCompositeActionPlace secondaryPlace){
        this.type = type;
        this.mainPlace = mainPlace;
        this.secondaryPlace = secondaryPlace;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public PHCompositeActionPlace getSecondaryPlace() {
        return secondaryPlace;
    }

    public SingleActionPlace getMainPlace() {
        return mainPlace;
    }

    public void clean(){

        this.mainPlace.clean();

        this.secondaryPlace.clean();
    }

}
