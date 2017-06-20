package server.model.board;

import client.view.cli.cmd.CliPrintable;
import server.model.effect.EffectSurplus;
import server.utility.BoardConfigParser;

/*
 * @author  ab3llini
 * @since   08/06/17.
 */
public enum CouncilPrivilege implements CliPrintable {

    WoodsAndStones(BoardConfigParser.getCouncilPrivilegeOptions().get(0).toString(), BoardConfigParser.getCouncilPrivilegeOptions().get(0)),
    Stones(BoardConfigParser.getCouncilPrivilegeOptions().get(1).toString(), BoardConfigParser.getCouncilPrivilegeOptions().get(1)),
    Servants(BoardConfigParser.getCouncilPrivilegeOptions().get(2).toString(), BoardConfigParser.getCouncilPrivilegeOptions().get(2)),
    Coins(BoardConfigParser.getCouncilPrivilegeOptions().get(3).toString(), BoardConfigParser.getCouncilPrivilegeOptions().get(3)),
    MilitaryPoints(BoardConfigParser.getCouncilPrivilegeOptions().get(4).toString(), BoardConfigParser.getCouncilPrivilegeOptions().get(4)),
    FaithPoints(BoardConfigParser.getCouncilPrivilegeOptions().get(5).toString(), BoardConfigParser.getCouncilPrivilegeOptions().get(5));


    private final String name;
    private final EffectSurplus surplus;

    CouncilPrivilege(String name, EffectSurplus surplus) {

        this.name = name;
        this.surplus = surplus;

    }

}
