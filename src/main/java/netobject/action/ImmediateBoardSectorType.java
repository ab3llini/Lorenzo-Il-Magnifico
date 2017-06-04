package netobject.action;

import client.view.cmd.CliPrintable;

/**
 * Created by LBARCELLA on 04/06/2017.
 */
public enum ImmediateBoardSectorType implements CliPrintable {

    TerritoryTower("Territory tower"),
    BuildingTower("Building tower"),
    CharacterTower("Character tower"),
    VentureTower("Venture tower"),
    Harvest("Harvest"),
    Production("Production");

    private final String name;

    ImmediateBoardSectorType(String name) {

        this.name = name;

    }

    @Override
    public String toString() {
        return name;
    }

}
