package Server.Model.Card.Developement;/*
 * Created by alberto on 09/05/17.
 */

import Server.Model.Effect.ImmediateEffect;
import Server.Model.Effect.PermanentEffect;

import java.util.ArrayList;

public class VentureDvptCard extends DvptCard {

    private final ArrayList<Cost> cost;

    public VentureDvptCard(Integer id, String name, ArrayList<Cost> cost, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.Venture, name, period, immediateEffect, permanentEffect);

        this.cost = cost;

    }
}

