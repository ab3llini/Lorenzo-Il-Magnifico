package server.model.card.leader;

import server.model.effect.OnceARoundEffect;
import server.model.effect.PermanentLeaderEffectType;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 17/05/2017.
 */
public class LeaderEffect implements Serializable {
    private OnceARoundEffect OnceARound;
    private PermanentLeaderEffectType permanentEffect;

    public LeaderEffect(OnceARoundEffect onceARound, PermanentLeaderEffectType permanentEffect){
        this.OnceARound=onceARound;
        this.permanentEffect=permanentEffect;
    }

    public OnceARoundEffect getOnceARound() {
        return OnceARound;
    }

    public PermanentLeaderEffectType getPermanentEffect() {
        return permanentEffect;
    }

    @Override
    public String toString() {

        String leaderEffect = "";

        if(OnceARound != null)
            leaderEffect += "Once a Round -> "+OnceARound.toString();
        else
            leaderEffect += permanentEffect.toString();
        
        return leaderEffect;
    }
}
