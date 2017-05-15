package server.model.card.ban;

/**
 * Created by Federico on 10/05/2017.
 */
public class BanEffect {
    private DiceMalus diceMalus;
    private NoPointsMalus noPointsMalus;
    private VictoryMalus victoryMalus;
    private PointMalus pointMalus;
    private boolean servantsPowerMalus;
    private boolean noFirstActionMalus;
    private boolean noMarketMalus;

    public BanEffect (DiceMalus diceMalus, NoPointsMalus noPointsMalus, VictoryMalus victoryMalus, PointMalus pointmalus, boolean servantsPowerMalus, boolean noFirstActionMalus){
        this.diceMalus = diceMalus;
        this.noPointsMalus = noPointsMalus;
        this.victoryMalus = victoryMalus;
        this.pointMalus = pointMalus;
        this.servantsPowerMalus = servantsPowerMalus;
        this.noFirstActionMalus = noFirstActionMalus;
        this.noMarketMalus = noMarketMalus;
    }

    public DiceMalus getDiceMalus() {
        return diceMalus;
    }

    public NoPointsMalus getNoPointsMalus() {
        return noPointsMalus;
    }

    public boolean isNoFirstActionMalus() {
        return noFirstActionMalus;
    }

    public PointMalus getPointMalus() {
        return pointMalus;
    }

    public boolean isServantsPowerMalus() {
        return servantsPowerMalus;
    }

    public boolean isNoMarketMalus() {
        return noMarketMalus;
    }

    public VictoryMalus getVictoryMalus() {
        return victoryMalus;
    }
    
}