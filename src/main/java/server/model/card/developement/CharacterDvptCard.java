package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import netobject.action.BoardSectorType;
import netobject.action.ImmediateBoardSectorType;
import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

import java.io.Serializable;
import java.util.ArrayList;

public class CharacterDvptCard extends DvptCard implements Serializable {

    private final ArrayList<Cost> cost;

    public CharacterDvptCard(Integer id, String name, Integer period, ArrayList<Cost> cost, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.character, name, period, immediateEffect, permanentEffect);

        this.cost = cost;
    }

    public ArrayList<Cost> getCost() {
        return cost;
    }

    public BoardSectorType getBoardSector() {

        return BoardSectorType.CharacterTower;

    }

    public ImmediateBoardSectorType getImmediateBoardSector() {

        return ImmediateBoardSectorType.CharacterTower;

    }

}

