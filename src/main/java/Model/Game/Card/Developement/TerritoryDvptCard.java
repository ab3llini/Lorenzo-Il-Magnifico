package Model.Game.Card.Developement;
/*
 * Created by alberto on 09/05/17.
 */

import Model.Game.Effect.ImmediateEffect;
import Model.Game.Effect.PermanentEffect;

public class TerritoryDvptCard extends DvptCard {
    public TerritoryDvptCard(Integer id, String name, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {
        super(id, DvptCardType.Territory, name, period, immediateEffect, permanentEffect);
    }
}
