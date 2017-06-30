package netobject.action.standard;

import client.view.cli.cmd.CliPrintable;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public enum StandardActionType implements CliPrintable {

    FamilyMemberPlacement("Place a family member"),
    ShowPersonalBoard("Show personal board"),
    LeaderCardActivation("Play or activate a leader card"),
    DiscardLeaderCard("Discard a leader card"),
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
