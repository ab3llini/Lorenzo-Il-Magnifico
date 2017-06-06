package netobject.action.standard;

import client.view.cmd.CliPrintable;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public enum StandardActionType implements CliPrintable {

    FamilyMemberPlacement("Place a family member"),
    LeaderCardActivation("Activate a leader card"),
    ShowDvptCardDetail("Show development card details"),
    RollDice("Roll the dices"),
    TerminateRound("Terminate the round");

    private final String name;

    StandardActionType(String name) {

        this.name = name;

    }

    public String toString() {
        return name;
    }

}
