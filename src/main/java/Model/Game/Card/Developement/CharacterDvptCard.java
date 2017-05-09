package Model.Game.Card.Developement;/*
 * Created by alberto on 09/05/17.
 */

import Model.Game.Effect.ImmediateEffect;
import Model.Game.Effect.PermanentEffect;

public class CharacterDvptCard extends DvptCard {

    private final Cost cost;

    public CharacterDvptCard(Integer id, String name, Cost cost, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.Character, name, period, immediateEffect, permanentEffect);

        this.cost = cost;

    }
}

