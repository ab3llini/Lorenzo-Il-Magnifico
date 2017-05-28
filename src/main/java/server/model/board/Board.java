package server.model.board;

import exception.FamilyMemberAlreadyInUseException;
import server.model.card.ban.BanCard;
import server.model.card.developement.*;
import server.utility.BoardConfigParser;

import java.util.ArrayList;

import static server.model.board.Period.*;

/**
 * Created by LBARCELLA on 19/05/2017.
 */
public class Board {
    private ArrayList<TowerSlot> territoryTower;
    private ArrayList<TowerSlot> characterTower;
    private ArrayList<TowerSlot> buildingTower;
    private ArrayList<TowerSlot> ventureTower;
    private Cathedral cathedral;
    private CouncilPalace councilPalace;
    private ArrayList<Dice> dices;
    private ActionArea productionArea;
    private ActionArea harvestArea;
    private Market market;

    public Board() {

        //create the board configured from file
        this.territoryTower = BoardConfigParser.getTower(DvptCardType.territory);
        this.characterTower = BoardConfigParser.getTower(DvptCardType.character);
        this.buildingTower = BoardConfigParser.getTower(DvptCardType.building);
        this.ventureTower = BoardConfigParser.getTower(DvptCardType.venture);
        this.cathedral = BoardConfigParser.getCathedral();
        this.councilPalace = BoardConfigParser.getCouncilPalace();
        this.productionArea = BoardConfigParser.getProductionActionArea();
        this.harvestArea = BoardConfigParser.getHarvestActionArea();
        this.market = BoardConfigParser.getMarket();

        //create the three dices
        this.dices = new ArrayList<Dice>();
        dices.add(new Dice(ColorType.Black));
        dices.add(new Dice(ColorType.White));
        dices.add(new Dice(ColorType.Orange));
    }


    public ArrayList<TowerSlot> getTerritoryTower() {
        return territoryTower;
    }


    public ArrayList<TowerSlot> getBuildingTower() {
        return buildingTower;
    }

    public ArrayList<TowerSlot> getCharacterTower() {
        return characterTower;
    }


    public ArrayList<TowerSlot> getVentureTower() {
        return ventureTower;
    }


    public ArrayList<TowerSlot> getTower(DvptCardType towerType) {

        if (towerType == DvptCardType.building) {
            return this.buildingTower;
        }

        if (towerType == DvptCardType.character) {
            return this.characterTower;
        }

        if (towerType == DvptCardType.territory) {
            return this.territoryTower;
        } else {
            return this.ventureTower;
        }

    }

    /**
     * this method insert territory dvptCard in the territory tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     *
     * @param cards
     */
    public void setDvptCardOnTerritoryTower(ArrayList<DvptCard> cards) {
        Integer i = 0;
        for (TowerSlot towerSlot : this.territoryTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert building dvptCard in the building tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     *
     * @param cards
     */

    public void setDvptCardOnBuildingTower(ArrayList<DvptCard> cards) {
        Integer i = 0;
        for (TowerSlot towerSlot : this.buildingTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert character dvptCard in the character tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     *
     * @param cards
     */
    public void setDvptCardOnCharacterTower(ArrayList<DvptCard> cards) {
        Integer i = 0;
        for (TowerSlot towerSlot : this.characterTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert venture dvptCard in the venture tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     *
     * @param cards
     */
    public void setDvptCardOnVentureTower(ArrayList<DvptCard> cards) {
        Integer i = 0;
        for (TowerSlot towerSlot : this.ventureTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert ban card in the cathedral.
     * it enters an array of 3 ban cards and put them in order from first period to third
     *
     * @param banCards
     */
    public void setBanCardOnCathedral(ArrayList<BanCard> banCards) {
        Period period = Period.first;
        for (BanCard banCard : banCards) {
            this.cathedral.setBanCard(period, banCard);
            period = next(period);
        }
    }


    public Cathedral getCathedral() {
        return this.cathedral;
    }

    public ActionArea getHarvestArea() {
        return this.harvestArea;
    }

    public ActionArea getProductionArea() {
        return this.productionArea;
    }

    public ArrayList<Dice> getDices() {
        return this.dices;
    }

    public void setDices(ArrayList<Dice> dices) {
        this.dices = dices;
    }

    public CouncilPalace getCouncilPalace() {
        return this.councilPalace;
    }

    public Market getMarket() {
        return this.market;
    }

    /**
     * return dice force from its color
     *
     * @param colorType
     * @return
     */
    public Integer getDiceForce(ColorType colorType) {

        for (Dice dice : this.dices) {
            if (dice.getColor() == colorType)
                return dice.getValue();
        }
        return 0;
    }


    /** This method prints the entire board in all its component in that moment**/

    public void printBoard() {
        System.out.println("__________________________________________________________________________________________");
        System.out.println("|                                                                                        |");
        System.out.println("| TERRITORY              BUILDING            CHARACTER               VENTURE             |");
        System.out.println("|   TOWER                  TOWER               TOWER                  TOWER              |");
        System.out.println("|   ______                ______                ______                ______             |");
        System.out.println("|   |    |___             |    |___             |    |___             |    |___          |");
        System.out.println("|   |    |   |            |    |   |            |    |   |            |    |   |         |");
        printTowerLine(0);
        System.out.println("|   |    |___|            |    |___|            |    |___|            |    |___|         |");
        System.out.println("|   |____|                |____|                |____|                |____|             |");
        System.out.println("|   |    |___             |    |___             |    |___             |    |___          |");
        System.out.println("|   |    |   |            |    |   |            |    |   |            |    |   |         |");
        printTowerLine(1);
        System.out.println("|   |    |___|            |    |___|            |    |___|            |    |___|         |");
        System.out.println("|   |____|                |____|                |____|                |____|             |");
        System.out.println("|   |    |___             |    |___             |    |___             |    |___          |");
        System.out.println("|   |    |   |            |    |   |            |    |   |            |    |   |         |");
        printTowerLine(2);
        System.out.println("|   |    |___|            |    |___|            |    |___|            |    |___|         |");
        System.out.println("|   |____|                |____|                |____|                |____|             |");
        System.out.println("|   |    |___             |    |___             |    |___             |    |___          |");
        System.out.println("|   |    |   |            |    |   |            |    |   |            |    |   |         |");
        printTowerLine(3);
        System.out.println("|   |    |___|            |    |___|            |    |___|            |    |___|         |");
        System.out.println("|   |____|                |____|                |____|                |____|             |");
        System.out.println("|                                                                                        |");
        System.out.println("|                                                                                        |");
        System.out.println("|        CATHEDRAL                                            COUNCIL PALACE             |");
        System.out.println("|     ________________                                       _______________________     |");
        printCouncilPalace();
        printCathedralLine();
        System.out.println("|     |____|____|____|                                                                   |");
        System.out.println("|                                                                                        |");
        System.out.println("|                                                                                        |");
        System.out.println("|                                                                 MARKET                 |");
        System.out.println("|                                   5 COINS       5 SERVANTS   3 MILITARY   2 DIFF       |");
        System.out.println("|                                                             + 2 COINS      COUNCILS    |");
        System.out.println("|                                    _______      _______      _______      _______      |");
        printMarketLine();
        System.out.println("|                                    |_____|      |_____|      |_____|      |_____|      |");
        System.out.println("|      SINGLE  PLACES                                                                    |");
        System.out.println("|    PRODUCTION HARVEST                                                                  |");
        System.out.println("|    _______     _______                                             DICES               |");
        printMainPHPlacesLine();
        System.out.println("|    |_____|     |_____|                                 BLACK       WHITE       ORANGE  |");
        System.out.println("|                                                       _______    _______    _______    |");
        printDicesLine();
        System.out.println("|                                                       |_____|    |_____|    |_____|    |");
        System.out.println("|     MULTIPLE  PLACES                                                                   |");
        System.out.println("|    PRODUCTION HARVEST                                                                  |");
        System.out.println("|    _______     _______                                                                 |");
        printMultiplePHPlacesLine();
        System.out.println("|    |_____|     |_____|                                                                 |");
        System.out.println("|________________________________________________________________________________________|");
    }

    /** This method prints a line on the towers according to the different floor of them **/

    public void printTowerLine(Integer line) {

        if (this.getTerritoryTower().get(line).getDvptCard() == null && this.getTerritoryTower().get(line).getFamilyMember() == null)
            System.out.print("|   |    |   |            ");

        else {

            if (this.getTerritoryTower().get(line).getFamilyMember() == null) {
                if (this.getTerritoryTower().get(0).getDvptCard().getId() < 10)
                    System.out.print("|   |  " + this.getTerritoryTower().get(line).getDvptCard().getId() + " |   |            ");
                else
                    System.out.print("|   | " + this.getTerritoryTower().get(line).getDvptCard().getId() + " |   |            ");
            }

            else {

                if (this.getTerritoryTower().get(0).getDvptCard().getId() < 10) {
                    System.out.print("|   |    | ");
                    printFamilyMember(this.getTerritoryTower().get(line).getFamilyMember());
                    System.out.print("|            ");

                }

                else {
                    System.out.print("|   |     | ");
                    printFamilyMember(this.getTerritoryTower().get(line).getFamilyMember());
                    System.out.print("|            ");
                }

            }

        }

        if (this.getBuildingTower().get(line).getDvptCard() == null && this.getBuildingTower().get(line).getFamilyMember() == null)

            System.out.print("|    |   |            ");

        else {
            if (this.getBuildingTower().get(line).getFamilyMember() == null) {
                System.out.print("| " + this.getBuildingTower().get(line).getDvptCard().getId() + " |   |            ");
            } else {
                System.out.print("|    | ");
                printFamilyMember(this.getBuildingTower().get(line).getFamilyMember());
                System.out.print("|            ");
            }
        }

        if (this.getCharacterTower().get(line).getDvptCard() == null && this.getCharacterTower().get(line).getFamilyMember() == null)
            System.out.print("|    |   |            ");

        else {
            if (this.getCharacterTower().get(line).getFamilyMember() == null) {
                System.out.print("| " + this.getCharacterTower().get(line).getDvptCard().getId() + " |   |            ");
            } else {
                System.out.print("|    | ");
                printFamilyMember(this.getCharacterTower().get(line).getFamilyMember());
                System.out.print("|            ");
            }

        }

        if (this.getVentureTower().get(line).getDvptCard() == null && this.getVentureTower().get(line).getFamilyMember() == null)
            System.out.print("|   |     |   |         |");
        else {
            if (this.getVentureTower().get(line).getFamilyMember() == null) {
                System.out.print("| " + this.getVentureTower().get(line).getDvptCard().getId() + " |   |         |" + "\n");
            } else {
                System.out.print("|    | ");
                printFamilyMember(this.getVentureTower().get(line).getFamilyMember());
                System.out.print("|         |" + "\n");
            }

        }

    }

    /** This method prints a particular family member**/

    public void printFamilyMember(FamilyMember familyMember) {

        if(familyMember == null)
            System.out.print("  ");

        else {

            if (familyMember.getColor() == ColorType.Orange) {
                System.out.print("OF");
            }

            if (familyMember.getColor() == ColorType.Black) {
                System.out.print("BF");
            }

            if (familyMember.getColor() == ColorType.White) {
                System.out.print("WF");
            }

            if (familyMember.getColor() == ColorType.Uncoloured) {
                System.out.print("UF");
            }

        }

    }

    /** This method prints a line which contains the BanCards occupying the CouncilPalace **/

    public void printCathedralLine() {

        System.out.print("|     | ");

        if (getCathedral().getBanCard(first).getId() < 10) {
            System.out.print(" ");
        }

        System.out.print(getCathedral().getBanCard(first).getId());
        System.out.print(" | ");

        if (getCathedral().getBanCard(second).getId() < 10)
            System.out.print(" ");

        System.out.print(getCathedral().getBanCard(second).getId());
        System.out.print(" | ");
        System.out.print(getCathedral().getBanCard(third).getId());
        System.out.print(" |                                      |_______________________|    |\n");

    }

    /** This method prints a line which contains the Family Members, ordered, occupying the Council Palace  **/

    public void printCouncilPalace() {

        System.out.print("|     |    |    |    |                                      |");

        for(FamilyMember member : getCouncilPalace().getPlaces()) {
            System.out.print(" ");
            printFamilyMember(member);
            System.out.print(" ");
        }

        System.out.print("\n");

    }

    /** This method prints a line which contains the Family Members occupying the Market **/

    public void printMarketLine() {

        System.out.print("|                                    | ");

        for (int i=0; i<4; i++) {
            printFamilyMember(market.getMarketPlaces().get(i).getFamilyMember());
            System.out.print("  |      | ");
        }

        System.out.print("\n");

    }

    /** This method prints a line which contains the Family Members occupying the main place of Production and Harvest Area**/


    public void printMainPHPlacesLine() {

        System.out.print("|    | ");

        if(productionArea.getMainPlace().getFamilyMember() != null)
            printFamilyMember(productionArea.getMainPlace().getFamilyMember());

        System.out.print("  |     | ");

        printFamilyMember(harvestArea.getMainPlace().getFamilyMember());

        System.out.print("  |                                                                |");
        System.out.print("\n");

    }


    /** This method prints some lines that contain the Family Members occupying the multiple places of Production and Harvest Area **/

    public void printMultiplePHPlacesLine() {
        Integer max;

        if(productionArea.getSecondaryPlace().getPlaces().size() > harvestArea.getSecondaryPlace().getPlaces().size())
            max = productionArea.getSecondaryPlace().getPlaces().size();

        else
            max = harvestArea.getSecondaryPlace().getPlaces().size();

        for(int i=0; i<max; i++){
            System.out.print("|    | ");

            if(productionArea.getSecondaryPlace().getPlaces().size() > i)
                printFamilyMember(productionArea.getSecondaryPlace().getPlaces().get(i));
            else
                System.out.print("  ");
            System.out.print("  |     | ");

            if(harvestArea.getSecondaryPlace().getPlaces().size() > i)
                printFamilyMember(harvestArea.getSecondaryPlace().getPlaces().get(i));
            else
                System.out.print("  ");

        System.out.print("  |                                                                 |");
            System.out.print("\n");
        }
    }

    /** This method prints a line with the value of dices on board **/

    public void printDicesLine(){
        System.out.print("|                                                       | ");

        for(Dice dice : dices) {
            System.out.print(dice.getValue());
            System.out.print("   |    | ");
        }

        System.out.print("\n");

    }
}