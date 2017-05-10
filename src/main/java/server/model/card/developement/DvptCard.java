package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

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

}

