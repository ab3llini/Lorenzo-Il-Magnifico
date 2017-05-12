package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

import java.util.ArrayList;

public class VentureDvptCard extends DvptCard {

    private final ArrayList<Cost> cost;

    public VentureDvptCard(Integer id, String name,Integer period, ArrayList<Cost> cost,  ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.Venture, name, period, immediateEffect, permanentEffect);

        this.cost = cost;

    }
}

