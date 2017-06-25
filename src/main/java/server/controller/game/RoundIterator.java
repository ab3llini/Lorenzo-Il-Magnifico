package server.controller.game;

import server.model.Match;
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

    private static final int MINIMUM_PLAYERS = 2;

    private static final int FIRST_ROUND = 1;
    private static final int LAST_ROUND = 4;

    private Match match;

    public RoundIterator(Match match) {

        this.match = match;

    }

    public boolean hasNext() {

        boolean a = this.match.getCurrentPeriod().toInt() == PERIODS && this.match.getCurrentTurn() == TURNS && this.match.getCurrentRound() == ROUNDS;

        boolean b = this.match.getPlayers().size() > this.match.getDisabledPlayers().size();

        return (!a && b);

    }

    public Queue<Player> next() {

        //For the moment the order of each round is given just by the council palace order
        Queue<Player> roundOrder = new LinkedList<Player>();

        //A queue of banned players that need to be put at the end of the round queue
        Queue<Player> banned = new LinkedList<Player>();


        //Update period, turn &
        if (this.match.getCurrentRound() == 0 && this.match.getCurrentTurn() == 0 && this.match.getCurrentPeriod().toInt() == 0) {

            this.match.setCurrentRound(1);

            this.match.setCurrentTurn(1);

            this.match.setCurrentPeriod(1);

        }
        else if (this.match.getCurrentRound() < ROUNDS) {

            this.match.setCurrentRound(this.match.getCurrentRound() + 1);

        }
        else if (this.match.getCurrentTurn() < TURNS) {

            this.match.setCurrentRound(1);

            this.match.setCurrentTurn(this.match.getCurrentTurn() + 1);

        }
        else if (this.match.getCurrentPeriod().toInt() < PERIODS) {

            this.match.setCurrentRound(1);

            this.match.setCurrentTurn(1);

            this.match.setCurrentPeriod(this.match.getCurrentPeriod().toInt() + 1);

        }

        //Check if the player that is being added to the queue has been banned
        for (Player p : this.match.getRoundOrder()) {

            boolean isBanned = false;

            for (BanCard c : p.getBanCards()) {

                if (c.getType() == BanType.special && ((SpecialBanCard)c).getSpecialEffect() == SpecialEffectType.noFirstAction) {

                    isBanned = true;

                    //He is banned, need to put it in the banned queue only if it is the last round of the turn
                    if (this.match.getCurrentRound() == LAST_ROUND) {

                        banned.add(p);

                    }

                }

            }

            if (!isBanned) {

                roundOrder.add(p);

            }
            else {

                if (this.match.getCurrentRound() != FIRST_ROUND) {

                    roundOrder.add(p);

                }

            }

        }

        //Add the banned ones in the last round
        if (this.match.getCurrentRound() == LAST_ROUND) {

            roundOrder.addAll(banned);

        }

        return roundOrder;

    }

    public void remove() {

    }
}
