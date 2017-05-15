package server.model.card.leader;/*
 * Created by alberto on 09/05/17.
 */

public class LeaderCard {
    private final Integer id;
    private final String name;
    private final Requirement requirement;
    private final LeaderEffect leaderEffect;

    public LeaderCard (Integer id, String name, Requirement requirement, LeaderEffect leaderEffect){
        this.id = id;
        this.name = name;
        this.requirement = requirement;
        this.leaderEffect = leaderEffect;
    }

    public Integer getId() {
        return id;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public LeaderEffect getLeaderEffect() {
        return leaderEffect;
    }

    public String getName() {
        return name;
    }
}
