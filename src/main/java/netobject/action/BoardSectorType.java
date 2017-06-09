package netobject.action;

import client.view.cmd.CliPrintable;

/**
 * Created by Federico on 22/05/2017.
 */

public enum BoardSectorType implements CliPrintable {

    TerritoryTower("Territory tower", true),
    BuildingTower("Building tower", true),
    CharacterTower("Character tower", true),
    VentureTower("Venture tower", true),
    Market("Market", true),
    CouncilPalace("Council palace", false),
    SingleProductionPlace("Single production place", false),
    CompositeProductionPlace("Composite production place", false),
    SingleHarvestPlace("Single harvest place", false),
    CompositeHarvestPlace("Composite harvest place", false);

    private final String name;
    private final boolean canChoseIndex;

    BoardSectorType(String name, boolean canChoseIndex) {

        this.name = name;
        this.canChoseIndex = canChoseIndex;

    }

    @Override
    public String toString() {
        return name;
    }

    public boolean canChoseIndex() {

        return canChoseIndex;

    }
}
