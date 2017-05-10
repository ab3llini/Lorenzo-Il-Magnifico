package server.model.card.developement;
/*
 * Created by alberto on 09/05/17.
 */

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;

public class TerritoryDvptCard extends DvptCard {
    public TerritoryDvptCard(Integer id, String name, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {
        super(id, DvptCardType.Territory, name, period, immediateEffect, permanentEffect);
    }
}
