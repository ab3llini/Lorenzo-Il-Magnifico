package Server.Model.Card.Developement;
/*
 * Created by alberto on 09/05/17.
 */

import Server.Model.Effect.ImmediateEffect;
import Server.Model.Effect.PermanentEffect;

public class TerritoryDvptCard extends DvptCard {
    public TerritoryDvptCard(Integer id, String name, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {
        super(id, DvptCardType.Territory, name, period, immediateEffect, permanentEffect);
    }
}
