package server.model;

import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import server.model.board.Player;

import java.io.Serializable;
import java.util.*;

/**
 * Created by LBARCELLA on 01/07/2017.
 */
public class FinalStanding implements Serializable {

    private ArrayList<Player> standings;
    private LinkedHashMap<Player,Integer> standingsAndPoints;

    public FinalStanding(HashMap<Player,Integer> playerScores) {

        ArrayList<Integer> scores = new ArrayList<>(playerScores.values());

        standings = new ArrayList<>();
        standingsAndPoints = new LinkedHashMap<>();

        scores.sort(Comparator.reverseOrder());

        for (Integer score : scores) {

            for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
                if (entry.getValue().equals(score)) {
                    standings.add(entry.getKey());
                    standingsAndPoints.put(entry.getKey(),score);
                }

            }

        }
    }

    public ArrayList<Player> getStandings() {
        return standings;
    }

    public LinkedHashMap<Player, Integer> getStandingsAndPoints() {
        return standingsAndPoints;
    }

    public int getScore(Player player){

        return standingsAndPoints.get(player);

    }

    @Override
    public String toString() {

        String finalScore = "";

        int i=1;

        finalScore += "THE MATCH IS ENDED! \n";

        finalScore += "LET'S SEE THE FINAL STANDINGS.. \n";

        for (Player player : this.standings){

            finalScore += i + "° --> "+player.getUsername()+" "+standingsAndPoints.get(player)+"\n";

            i++;

        }

        return finalScore;

    }
}
