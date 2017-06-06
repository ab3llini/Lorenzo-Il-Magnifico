package client.view;

/*
 * @author  ab3llini
 * @since   03/06/17.
 */

import exception.NoSuchPlayerException;
import netobject.action.standard.StandardActionType;
import server.model.Match;
import server.model.board.Dice;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.HashMap;

/**
 * Handles the player logic.
 */
public class LocalMatchController {

    //The model local copy
    private Match match;

    private String playerUsername;

    private Deck<LeaderCard> draftable;

    private boolean matchEnded = false;

    private StandardActionType lastPendingAction;

    //actions performed by a player on this round
    HashMap<StandardActionType, Boolean> actionsPerformedOnThisRound;

    LocalMatchController(String playerUsername) {

        this.playerUsername = playerUsername;

        this.actionsPerformedOnThisRound = new HashMap<StandardActionType, Boolean>();

        for (StandardActionType action : StandardActionType.values()) {

            this.actionsPerformedOnThisRound.put(action, false);

        }

    }

    /**
     * Clears the actions performed by a player on this round
     */
    public void flushActionsPerformed() {

        this.actionsPerformedOnThisRound.clear();

        this.lastPendingAction = null;

        for (StandardActionType action : StandardActionType.values()) {

            this.actionsPerformedOnThisRound.put(action, false);

        }

    }

    public boolean diceAreRolled() {

        for (Dice d : this.match.getBoard().getDices()) {

            if (d.getValue() == 0)

                return false;

        }

        return true;

    }

    public boolean canRollDices() {

        return  (this.match.getCurrentTurn() == 1 && this.match.getRoundOrder().get(0).getUsername().equals(this.playerUsername));

    }

    /**
     * Updates the model
     */
    public void setMatch(Match match) {

        this.match = match;

        //Check if the user can roll the dices
        if (!this.canRollDices()) {

            this.setActionPerformed(StandardActionType.RollDice, true);

        }


    }

    public boolean canPerformAction(StandardActionType action) {

        return !this.actionsPerformedOnThisRound.get(action);

    }

    public void setMatchEnded(boolean matchEnded) {
        this.matchEnded = matchEnded;
    }

    public boolean matchHasEnded() {
        return this.matchEnded;
    }

    private void setActionPerformed(StandardActionType action, Boolean value) {

        this.actionsPerformedOnThisRound.put(action, value);

        return;

    }

    public void setLastPendingAction(StandardActionType action) {

        this.lastPendingAction = action;

    }

    public void confirmLastPendingAction() {

        this.setActionPerformed(this.lastPendingAction, true);

    }

    public void printLocalPlayer() {

        try {

            System.out.println(this.match.getPlayerFromUsername(this.playerUsername));

        } catch (NoSuchPlayerException e) {

            e.printStackTrace();

        }

    }

    public void setDraftable(Deck<LeaderCard> draftable) {
        this.draftable = draftable;
    }

    public Deck<LeaderCard> getDraftable() {
        return draftable;
    }

    public Match getMatch() {
        return match;
    }
}
