package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

import java.util.ArrayList;

public class BuildingDvptCard extends DvptCard {

    private final ArrayList<Cost> cost;

    public BuildingDvptCard(Integer id, String name, ArrayList<Cost> cost, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.Venture, name, period, immediateEffect, permanentEffect);

        this.cost = cost;

}
}