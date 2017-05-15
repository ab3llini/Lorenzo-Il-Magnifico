package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

public abstract class DvptCard {

    protected final Integer id;
    protected final DvptCardType type;
    protected final String name;
    protected final Integer period;
    protected final ImmediateEffect immediateEffect;
    protected final PermanentEffect permanentEffect;


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

    public DvptCardType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Integer getPeriod() {
        return period;
    }

    public ImmediateEffect getImmediateEffect() {
        return immediateEffect;
    }

    public PermanentEffect getPermanentEffect() {
        return permanentEffect;
    }
    
}

