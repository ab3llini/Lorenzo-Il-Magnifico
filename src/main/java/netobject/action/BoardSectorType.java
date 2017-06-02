package netobject.action;

import client.view.cmd.CliPrintable;

/**
 * Created by Federico on 22/05/2017.
 */

public enum BoardSectorType implements CliPrintable {

    TerritoryTower("Territory tower"),
    BuildingTower("Building tower"),
    CharacterTower("Character tower"),
    VentureTower("Venture tower"),
    Market("Market"),
    CouncilPalace("Council palace"),
    SingleProductionPlace("Single production place"),
    CompositeProductionPlace("Composite production place"),
    SingleHarvestPlace("Single harvest place"),
    CompositeHarvestPlace("Composite harvest place");

    private final String name;

    BoardSectorType(String name) {

        this.name = name;

    }

    @Override
    public String toString() {
        return name;
    }
}
