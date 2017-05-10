package Server.Model.Card.Leader;/*
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
}
