package server.model.card.leader;

import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentLeaderEffectType;

/**
 * Created by Federico on 10/05/2017.
 */
public class LeaderEffect {
    ImmediateEffect OnceATurn;
    PermanentLeaderEffectType permanentEffect;

    public LeaderEffect(ImmediateEffect onceATurn, PermanentLeaderEffectType permanentEffect){
        this.OnceATurn=onceATurn;
        this.permanentEffect=permanentEffect;
    }
}
