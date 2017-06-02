package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;

/**
 * Created by Federico on 22/05/2017.
 */
public class RollDicesAction extends Action {

    private final StandardActionType standardActionType;

    public RollDicesAction(StandardActionType standardActionType) {
        super(ActionType.Standard);
        this.standardActionType = standardActionType;
    }
}

