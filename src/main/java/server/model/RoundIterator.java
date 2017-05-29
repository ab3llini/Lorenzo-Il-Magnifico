package server.model;

import server.model.board.Player;
import server.model.card.ban.BanCard;
import server.model.card.ban.BanType;
import server.model.card.ban.SpecialBanCard;
import server.model.card.ban.SpecialEffectType;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/*
 * @author  ab3llini
 * @since   29/05/17.
 */

/**
 * Custom iterator that loops through the rounds
 */
public class RoundIterator implements Iterator<Queue<Player>> {


    private static final int PERIODS = 3;
    private static final int TURNS = 2;
    private static final int ROUNDS = 4;

    private static final int FIRST_ROUND = 0;

    private Match match;

    public RoundIterator(Match match) {

        this.match = match;

    }


    public boolean hasNext() {

        return (this.match.getCurrentPeriod() <= PERIODS && this.match.getCurrentTurn() <= TURNS && this.match.getCurrentRound() <= ROUNDS);

    }

    public Queue<Player> next() {

        //For the moment the order of each round is given just by the council palace order
        Queue<Player> roundOrder = new LinkedList<Player>();

        //A queue of banned players that need to be put at the end of the round queue
        Queue<Player> banned = new LinkedList<Player>();

        //For every round, setup the players in the queue
        for (int roundNr = 0; roundNr < ROUNDS; roundNr++) {

            if (roundNr == FIRST_ROUND) {

                //Check if one of the players got the ban card that prevents him from making the move
                for (Player player : this.match.getRoundOrder()) {

                    //Lookup the player cards, does it have the bad one ?
                    for (BanCard card : player.getBanCards()) {

                        if (card.getType() == BanType.special && ((SpecialBanCard)card).getSpecialEffect() == SpecialEffectType.noFirstAction) {

                            //If yes, we add it to the banned queue
                            banned.add(player);

                            //Continue looping!
                            continue;

                        }

                    }

                    //Otherwise we add it to the next round order queue as they are
                    roundOrder.add(player);

                }

            }
            else {

                //Otherwise add all the players in the current order since nothing may change this
                roundOrder.addAll(this.match.getRoundOrder());

            }

        }

        //After the roundOrder has been defined, lets add the banned players at the end of the queue
        roundOrder.addAll(banned);

        //Update period, turn & round
        if (this.match.getCurrentRound() < ROUNDS) {

            this.match.setCurrentRound(this.match.getCurrentRound() + 1);

        }
        else if (this.match.getCurrentTurn() < TURNS) {

            this.match.setCurrentTurn(this.match.getCurrentTurn() + 1);

        }
        else if (this.match.getCurrentPeriod() < PERIODS) {

            this.match.setCurrentPeriod(this.match.getCurrentPeriod() + 1);

        }

        return roundOrder;

    }

    public void remove() {

    }
}
