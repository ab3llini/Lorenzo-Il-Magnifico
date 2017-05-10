package Server.Model.Card.Ban;

import Server.Model.Card.Developement.DvptCardType;

/**
 * Created by Federico on 10/05/2017.
 */
public class DiceMalus {
    private ActionType target;
    private DvptCardType type;
    private boolean RoundDiceMalus;
    private Integer malus;

    public DiceMalus (ActionType target, DvptCardType type, boolean RoundDiceMalus, Integer malus ){
        this.target = target;
        this.type = type;
        this.RoundDiceMalus = RoundDiceMalus;
        this.malus = malus;
    }
}
