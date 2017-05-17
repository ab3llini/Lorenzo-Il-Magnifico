package server.model.card.leader;

import server.model.effect.OnceARoundEffect;
import server.model.effect.PermanentLeaderEffectType;

/**
 * Created by LBARCELLA on 17/05/2017.
 */
public class LeaderEffect {
    OnceARoundEffect OnceARound;
    PermanentLeaderEffectType permanentEffect;

    public LeaderEffect(OnceARoundEffect onceARound, PermanentLeaderEffectType permanentEffect){
        this.OnceARound=onceARound;
        this.permanentEffect=permanentEffect;
    }
}
