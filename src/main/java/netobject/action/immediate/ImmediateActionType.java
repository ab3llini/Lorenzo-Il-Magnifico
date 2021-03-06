package netobject.action.immediate;

import client.view.cli.cmd.CliPrintable;

/*
 * @author  ab3llini
 * @since   31/05/17.
 */
public enum ImmediateActionType implements CliPrintable {

    TakeTerritoryCard("Take a territory card", ImmediateActionTypeImpl.Placement),
    TakeBuildingCard("Take a building card", ImmediateActionTypeImpl.Placement),
    TakeCharacterCard("Take a character card", ImmediateActionTypeImpl.Placement),
    TakeVentureCard("Take a venture card", ImmediateActionTypeImpl.Placement),
    TakeAnyCard("Take any card", ImmediateActionTypeImpl.Placement),
    ActivateHarvest("Activate the harvest area", ImmediateActionTypeImpl.Placement),
    ActivateProduction("Activate the production area", ImmediateActionTypeImpl.Placement),
    SelectCost("Select a cost", ImmediateActionTypeImpl.Choice),
    SelectCouncilPrivilege("Select a council privilege", ImmediateActionTypeImpl.Choice),
    SelectConversion("Select a conversion", ImmediateActionTypeImpl.Choice),
    DecideBanOption("Select an option", ImmediateActionTypeImpl.Choice),
    DecideDiscountOption("Select an option", ImmediateActionTypeImpl.Choice),
    SelectFamilyMember("Select a family member",ImmediateActionTypeImpl.Choice),
    SelectActiveLeaderCard("Which leader do you want to copy?", ImmediateActionTypeImpl.Choice);

    private final String name;

    private final ImmediateActionTypeImpl impl;

    private ImmediateActionType(String name, ImmediateActionTypeImpl impl) {

        this.name = name;
        this.impl = impl;
    }

    public ImmediateActionTypeImpl getImpl() {
        return impl;
    }

    @Override
    public String toString() {
        return name;
    }
}
