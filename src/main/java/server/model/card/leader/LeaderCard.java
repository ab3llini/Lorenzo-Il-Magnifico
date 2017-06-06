package server.model.card.leader;/*
 * Created by alberto on 09/05/17.
 */

import java.io.Serializable;

public class LeaderCard implements Serializable {
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

    public String getName() {
        return name;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public LeaderEffect getLeaderEffect() {
        return leaderEffect;
    }

    @Override
    public String toString() {

        String leader = "";

        leader += "Card ID: "+this.id+"\n";

        leader += "Name : "+this.name+"\n";

        leader += "Requirements: "+this.requirement.toString()+"\n";

        leader += "Effect: "+this.leaderEffect.toString()+"\n";

        return leader;
    }
}
