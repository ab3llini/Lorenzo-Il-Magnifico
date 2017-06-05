package client.view;

/*
 * @author  ab3llini
 * @since   03/06/17.
 */

import exception.NoSuchPlayerException;
import netobject.action.standard.StandardActionType;
import server.model.Match;

import java.util.HashMap;

/**
 * Handles the player logic.
 */
public class LocalMatchController {

    //The model local copy
    private Match match;

    private String playerUsername;

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

    /**
     * Updates the model
     */
    public void setMatch(Match match) {

        this.match = match;


    }

    public boolean canPerformAction(StandardActionType action) {

        return this.actionsPerformedOnThisRound.get(action);

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

    public Match getMatch() {
        return match;
    }
}
