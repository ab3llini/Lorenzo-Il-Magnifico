package server.model.card.developement;
/*
 * Created by alberto on 09/05/17.
 */

import netobject.action.BoardSectorType;
import netobject.action.ImmediateBoardSectorType;
import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

import java.io.Serializable;

public class TerritoryDvptCard extends DvptCard implements Serializable {
    public TerritoryDvptCard(Integer id, String name, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {
        super(id, DvptCardType.territory, name, period, immediateEffect, permanentEffect);
    }

    public BoardSectorType getBoardSector() {

        return BoardSectorType.TerritoryTower;

    }

    public ImmediateBoardSectorType getImmediateBoardSector() {

        return ImmediateBoardSectorType.TerritoryTower;

    }

}
