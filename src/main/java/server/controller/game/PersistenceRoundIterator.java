package server.controller.game;

import server.model.Match;
import server.model.board.Player;

import java.util.Queue;

/*
 * @author  ab3llini
 * @since   07/07/17.
 */
public class PersistenceRoundIterator extends RoundIterator {

    private Player previousCurrentPlayer;

    public PersistenceRoundIterator(Match match, Player previousCurrentPlayer) {
        super(match);

        this.previousCurrentPlayer = previousCurrentPlayer;

    }

    @Override
    public Queue<Player> next() {

        Queue<Player> queue;

        //If we are restarting a persistence match, resolve just the previous order
        //After that just call super.next()
        if (previousCurrentPlayer == null) {

            queue = super.next();

        }
        else {

            queue = this.resolveRoundOrder();


            //Filter out players that have already played in this round until we find the previous player
            //This is a routine that we need to perform just once
            while (!queue.peek().getUsername().equals(this.previousCurrentPlayer.getUsername())) {

                //Remove the player, he has already played..
                queue.remove();

            }

            //We can now return the correct queue.
            //Remember to null the player
            this.previousCurrentPlayer = null;

        }


        return queue;

    }
}
