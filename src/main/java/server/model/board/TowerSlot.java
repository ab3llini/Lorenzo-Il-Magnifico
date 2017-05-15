package server.model.board;

import server.model.card.developement.DvptCard;
import server.model.effect.ImmediateEffect;

/**
 * Created by Federico on 11/05/2017.
 */
public class TowerSlot extends SingleActionPlace {
    private DvptCard dvptCard;


    public TowerSlot(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers, DvptCard dvptCard) {
        super(immediateEffect, entryForce, minPlayers);
        this.dvptCard = dvptCard;
    }

    public DvptCard getDvptCard() {
        return dvptCard;
    }

}
