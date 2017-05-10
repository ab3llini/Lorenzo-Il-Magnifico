package Server.Model.Card.Developement;/*
 * Created by alberto on 09/05/17.
 */

import Server.Model.Effect.ImmediateEffect;
import Server.Model.Effect.PermanentEffect;

public class CharacterDvptCard extends DvptCard {

    private final Cost cost;

    public CharacterDvptCard(Integer id, String name, Cost cost, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {

        super(id, DvptCardType.Character, name, period, immediateEffect, permanentEffect);

        this.cost = cost;

    }
}

