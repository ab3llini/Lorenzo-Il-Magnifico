package server.model.board;

import server.model.card.developement.DvptCard;
import server.model.effect.EffectSurplus;
import server.model.effect.ImmediateEffect;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 * Methods implemented by LBARCELLA on 18/05/2017
 */
public class TowerSlot extends SingleActionPlace  implements Serializable {
    private DvptCard dvptCard;


    public TowerSlot(EffectSurplus effectSurplus, Integer entryForce, Integer minPlayers) {
        super(effectSurplus, entryForce, minPlayers);
    }


    public DvptCard getDvptCard() {
        return dvptCard;
    }

    public void setDvptCard(DvptCard dvptCard) {
        this.dvptCard = dvptCard;
    }

}
