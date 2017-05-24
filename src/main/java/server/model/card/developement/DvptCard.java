package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

import java.util.ArrayList;

public abstract class DvptCard {

    private final Integer id;
    private final DvptCardType type;
    private final String name;
    private final Integer period;
    private final ImmediateEffect immediateEffect;
    private final PermanentEffect permanentEffect;


    protected DvptCard(Integer id, DvptCardType type, String name, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.period = period;
        this.immediateEffect = immediateEffect;
        this.permanentEffect = permanentEffect;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPeriod() {
        return period;
    }

    public DvptCardType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Cost> getCost(){
        return null;
    }

    public PermanentEffect getPermanentEffect() {
        return permanentEffect;
    }

    public ImmediateEffect getImmediateEffect() {
        return immediateEffect;
    }
}

