package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

import java.util.ArrayList;

public class BuildingDvptCard extends DvptCard {

    private final ArrayList<Cost> cost;

    public BuildingDvptCard(Integer id, String name, Integer period, ArrayList<Cost> cost, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.venture, name, period, immediateEffect, permanentEffect);

        this.cost = cost;

}
}