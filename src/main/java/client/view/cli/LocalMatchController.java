package client.view.cli;

/*
 * @author  ab3llini
 * @since   03/06/17.
 */

import exception.NoSuchPlayerException;
import netobject.action.immediate.ImmediateActionType;
import netobject.action.standard.StandardActionType;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Dice;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles the player logic.
 */
public class LocalMatchController {

    //The model local copy
    private Match match;

    private String playerUsername;

    private Deck<LeaderCard> draftableLeaderCards;

    private ArrayList<BonusTile> draftableBonusTiles;

    private boolean matchEnded = false;

    private StandardActionType lastPendingStandardAction;

    private ImmediateActionType lastPendingImmediateAction;
    

    //actions performed by a player on this round
    private HashMap<StandardActionType, Boolean> actionsPerformedOnThisRound;

    public LocalMatchController() {

        this.actionsPerformedOnThisRound = new HashMap<StandardActionType, Boolean>();

        for (StandardActionType action : StandardActionType.values()) {

            this.actionsPerformedOnThisRound.put(action, false);

        }

    }


    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    /**
     * Clears the actions performed by a player on this round
     */
    public void flushActionsPerformed() {

        this.actionsPerformedOnThisRound.clear();

        this.lastPendingStandardAction = null;

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

    /**
     * You can roll the dices just if you are the first of that turn or if you are the first non disabled player
     * @return
     */
    public boolean canRollDices() {

        if  (this.match.getCurrentRound() == 1) {

            Player firstNotDisabled = null;

            for (Player p : this.match.getRoundOrder()) {

                if (!p.isDisabled()) {

                    firstNotDisabled = p;

                    break ;

                }
            }

            if (firstNotDisabled.getUsername().equals(this.playerUsername)) {

                return true;

            }

        }

        return false;

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

    public synchronized boolean canPerformAction(StandardActionType action) {

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

    public synchronized void setLastPendingStandardAction(StandardActionType action) {

        this.lastPendingStandardAction = action;

    }

    public synchronized void confirmLastPendingAction() {

        this.setActionPerformed(this.lastPendingStandardAction, true);

    }


    public synchronized void  setDraftableLeaderCards(Deck<LeaderCard> draftableLeaderCards) {
        this.draftableLeaderCards = draftableLeaderCards;
    }

    public boolean canSelectBanOption() {

        if  (this.match.getCurrentTurn() == 2 && this.match.getCurrentRound() == 4) {

            try {

                if (this.match.getPlayerFromUsername(this.playerUsername).getFaithPoints() >= this.match.getBoard().getCathedral().getMinFaith(this.match.getCurrentPeriod())){

                    return true;

                }
            } catch (NoSuchPlayerException e) {

                e.printStackTrace();

            }

        }

        return false;

    }

    public synchronized Player getLocalPlayer() {

        try {
            return this.match.getPlayerFromUsername(this.playerUsername);
        } catch (NoSuchPlayerException e) {
            e.printStackTrace();
        }

        return null;

    }

    public synchronized Deck<LeaderCard> getDraftableLeaderCards() {
        return draftableLeaderCards;
    }

    public Match getMatch() {
        return match;
    }

    public void confirmLastPendingImmediateAction() {
        this.lastPendingImmediateAction = null;
    }

    public void setLastPendingImmediateAction(ImmediateActionType lastPendingImmediateAction) {
        this.lastPendingImmediateAction = lastPendingImmediateAction;
    }

    public StandardActionType getLastPendingStandardAction() {
        return lastPendingStandardAction;
    }

    public ImmediateActionType getLastPendingImmediateAction() {
        return lastPendingImmediateAction;
    }

    public void setDraftableBonusTiles(ArrayList<BonusTile> draftableBonusTiles) {
        this.draftableBonusTiles = draftableBonusTiles;
    }

    public ArrayList<BonusTile> getDraftableBonusTiles() {
        return draftableBonusTiles;
    }
}
