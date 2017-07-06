package server.model.valuable;

import server.model.board.Player;

/**
 * Created by LBARCELLA on 06/07/2017.
 */
public class RollbackClass {

    private final int coins;
    private final int wood;
    private final int stones;
    private final int servants;
    private final int militaryPoints;
    private final int faithPoints;
    private final int victoryPoints;

    public RollbackClass(Player player){

        this.coins = player.getCoins();
        this.faithPoints = player.getFaithPoints();
        this.wood = player.getWood();
        this.servants = player.getServants();
        this.stones = player.getStones();
        this.militaryPoints = player.getMilitaryPoints();
        this.victoryPoints = player.getVictoryPoints();

    }

    public int getCoins() {
        return coins;
    }

    public int getWood() {
        return wood;
    }

    public int getServants() {
        return servants;
    }

    public int getStones() {
        return stones;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public int getFaithPoints() {
        return faithPoints;
    }

    public int getMilitaryPoints() {
        return militaryPoints;
    }
}
