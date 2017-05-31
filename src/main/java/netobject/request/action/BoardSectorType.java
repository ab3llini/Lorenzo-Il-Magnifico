package netobject.request.action;

import client.view.cmd.CliPrintable;
import client.view.cmd.Command;

/**
 * Created by Federico on 22/05/2017.
 */

public enum BoardSectorType implements CliPrintable {

    TerritoryTower("Territory tower", "1"),
    BuildingTower("Building tower", "2"),
    CharacterTower("Character tower", "3"),
    VentureTower("Venture tower", "4"),
    Market("Market", "5"),
    CouncilPalace("Council palace", "6"),
    SingleProductionPlace("Single production place", "7"),
    CompositeProductionPlace("Composite production place", "8"),
    SingleHarvestPlace("Single harvest place", "9"),
    CompositeHarvestPlace("Composite harvest place", "10");

    private final String name;
    private final String value;

    BoardSectorType(String name, String value) {

        this.name = name;
        this.value = value;

    }

    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }


}
