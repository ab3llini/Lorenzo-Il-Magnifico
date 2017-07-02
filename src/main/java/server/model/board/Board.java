package server.model.board;

import logger.AnsiColors;
import server.model.card.ban.BanCard;
import server.model.card.developement.*;
import server.model.effect.EffectSurplus;
import server.model.valuable.Point;
import server.model.valuable.PointType;
import server.model.valuable.Resource;
import server.model.valuable.ResourceType;
import server.utility.BoardConfigParser;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;

import static server.model.board.Period.*;

/**
 * Created by LBARCELLA on 19/05/2017.
 */
public class Board implements Serializable {
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
    private ArrayList<Player> players;

    public Board(ArrayList<Player> players) {

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
        this.players = players;

        //create the three dices
        this.dices = new ArrayList<>();
        dices.add(new Dice(ColorType.Black));
        dices.add(new Dice(ColorType.White));
        dices.add(new Dice(ColorType.Orange));
    }

    public Player getPlayer(PlayerColor playerColor) {

        for (Player player : this.players) {

            if (player.getColor() == playerColor)
                return player;

        }

        return null;
    }

    /**
     * returns an ordered arrayList of players that has their familiar in the CouncilPalace area
     * @return
     */
    public ArrayList<Player> getCouncilPalaceOrder(){

        ArrayList<Player> playersOrder=new ArrayList<Player>();

        for (FamilyMember familyMember: this.councilPalace.places) {
            if(!playersOrder.contains(getPlayer(familyMember.getPlayerColor()))){
                playersOrder.add(getPlayer(familyMember.getPlayerColor()));
            }
        }
        return playersOrder;
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
        }


        return this.ventureTower;

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

    /**
     * this method receive a towerType color and return an arrayList of all the players that have a family member in this tower
     *
     * @param towerType
     * @return
     */
    public ArrayList<Player> getPlayersInTower(DvptCardType towerType) {

        ArrayList<Player> players = new ArrayList<Player>();

        if (towerType == DvptCardType.character) {
            players = getCharacterTowerPlayers();
        }

        if (towerType == DvptCardType.venture) {
            players = getVentureTowerPlayers();
        }

        if (towerType == DvptCardType.building) {
            players = getBuildingTowerPlayers();
        }

        if (towerType == DvptCardType.territory)
            players = getTerritoryTowerPlayers();

        return players;
    }

    public ArrayList<Player> getCharacterTowerPlayers() {

        ArrayList<Player> players = new ArrayList<Player>();

        for (TowerSlot towerSlot : this.characterTower) {

            if (towerSlot.isOccupied()) {

                players.add(getPlayer(towerSlot.getFamilyMember().getPlayerColor()));
            }
        }

        return players;
    }

    public ArrayList<Player> getVentureTowerPlayers() {

        ArrayList<Player> players = new ArrayList<Player>();

        for (TowerSlot towerSlot : this.ventureTower) {

            if (towerSlot.isOccupied()) {

                players.add(getPlayer(towerSlot.getFamilyMember().getPlayerColor()));

            }
        }
        return players;
    }

    public ArrayList<Player> getTerritoryTowerPlayers() {

        ArrayList<Player> players = new ArrayList<Player>();

        for (TowerSlot towerSlot : this.territoryTower) {

            if (towerSlot.isOccupied()) {

                players.add(getPlayer(towerSlot.getFamilyMember().getPlayerColor()));

            }
        }
        return players;
    }

    public ArrayList<Player> getBuildingTowerPlayers() {

        ArrayList<Player> players = new ArrayList<Player>();

        for (TowerSlot towerSlot : this.buildingTower) {

            if (towerSlot.isOccupied()) {

                players.add(getPlayer(towerSlot.getFamilyMember().getPlayerColor()));

            }
        }
        return players;
    }

    /**
     * clean the tower at the end of the round
     */
    public void cleanTowers() {

        for (DvptCardType type : DvptCardType.values()) {

            for (TowerSlot towerslot : getTower(type)) {

                towerslot.setFamilyMember(null);
                towerslot.setOccupied(false);

            }
        }

    }

    /**
     * This method prints the entire board in all its component in that moment
     **/

    @Override
    public String toString() {

        String board = "";
        board += "__________________________________________________________________________________________\n";
        board += ("|                                                                                        |\n");
        board += ("| ");
        board += (AnsiColors.ANSI_GREEN + "  TERRITORY            " + AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_BLUE + "CHARACTER             " + AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_YELLOW + "BUILDING              " + AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_PURPLE + " VENTURE            |\n" + AnsiColors.ANSI_RESET);

        board += ("| ");
        board += (AnsiColors.ANSI_GREEN + "  TOWER               " + AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_BLUE + "  TOWER              " + AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_YELLOW + "   TOWER                " + AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_PURPLE + " TOWER              |\n" + AnsiColors.ANSI_RESET);

        board += ("|   ______                ______                ______                ______             |\n");


        board += ("|   |    |___             |    |___             |    |___             |    |___          |\n");

        board += ("|   |    |   |            |    |   |            |    |   |            |    |   |         |");

        if (getTerritoryTower().get(3).getDvptCard() != null) {

            board += "   " + getTerritoryTower().get(3).getDvptCard().getId() + ". " + getTerritoryTower().get(3).getDvptCard().getName();
            board += " Imm: " + printSurplus(getTerritoryTower().get(3).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";
        board += printTowerLine(3);

        if (getTerritoryTower().get(2).getDvptCard() != null) {

            board += "   " + getTerritoryTower().get(2).getDvptCard().getId() + ". " + getTerritoryTower().get(2).getDvptCard().getName();
            board += " Imm: " + printSurplus(getTerritoryTower().get(2).getDvptCard().getImmediateEffect().getSurplus());

        }

        board += "\n";

        board += ("|   |    |___|            |    |___|            |    |___|            |    |___|         |");

        if (getTerritoryTower().get(1).getDvptCard() != null) {

            board += "   " + getTerritoryTower().get(1).getDvptCard().getId() + ". " + getTerritoryTower().get(1).getDvptCard().getName();
            board += " Imm: " + printSurplus(getTerritoryTower().get(1).getDvptCard().getImmediateEffect().getSurplus());

        }

        board += "\n";

        board += ("|   |____|                |____|                |____|                |____|             |");

        if (getTerritoryTower().get(0).getDvptCard() != null) {

            board += "   " + getTerritoryTower().get(0).getDvptCard().getId() + ". " + getTerritoryTower().get(0).getDvptCard().getName();
            board += " Imm: " + printSurplus(getTerritoryTower().get(0).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |    |___             |    |___             |    |___             |    |___          |");

        if (getCharacterTower().get(3).getDvptCard() != null) {

            board += "   " + getCharacterTower().get(3).getDvptCard().getId() + ". " + getCharacterTower().get(3).getDvptCard().getName() + "   Cost:  " + printDvptCost(getCharacterTower().get(3).getDvptCard());
            board += " Imm: " + printSurplus(getCharacterTower().get(3).getDvptCard().getImmediateEffect().getSurplus());

        }

        board += "\n";


        board += ("|   |    |   |            |    |   |            |    |   |            |    |   |         |");

        if (getCharacterTower().get(2).getDvptCard() != null) {

            board += "   " + getCharacterTower().get(2).getDvptCard().getId() + ". " + getCharacterTower().get(2).getDvptCard().getName() + "   Cost:  " + printDvptCost(getCharacterTower().get(2).getDvptCard());
            board += " Imm: " + printSurplus(getCharacterTower().get(2).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";
        board += printTowerLine(2);

        if (getCharacterTower().get(1).getDvptCard() != null) {

            board += "   " + getCharacterTower().get(1).getDvptCard().getId() + ". " + getCharacterTower().get(1).getDvptCard().getName() + "   Cost:  " + printDvptCost(getCharacterTower().get(1).getDvptCard());
            board += " Imm: " + printSurplus(getCharacterTower().get(1).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |    |___|            |    |___|            |    |___|            |    |___|         |");

        if (getCharacterTower().get(0).getDvptCard() != null) {

            board += "   " + getCharacterTower().get(0).getDvptCard().getId() + ". " + getCharacterTower().get(0).getDvptCard().getName() + "   Cost:  " + printDvptCost(getCharacterTower().get(0).getDvptCard());
            board += " Imm: " + printSurplus(getCharacterTower().get(0).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |____|                |____|                |____|                |____|             |");

        if (getBuildingTower().get(3).getDvptCard() != null) {

            board += "   " + getBuildingTower().get(3).getDvptCard().getId() + ". " + getBuildingTower().get(3).getDvptCard().getName() + "   Cost:  " + printDvptCost(getBuildingTower().get(3).getDvptCard());
            board += " Imm: " + printSurplus(getBuildingTower().get(3).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |    |___             |    |___             |    |___             |    |___          |");

        if (getBuildingTower().get(2).getDvptCard() != null) {

            board += "   " + getBuildingTower().get(2).getDvptCard().getId() + ". " + getBuildingTower().get(2).getDvptCard().getName() + "   Cost:  " + printDvptCost(getBuildingTower().get(2).getDvptCard());
            board += " Imm: " + printSurplus(getBuildingTower().get(2).getDvptCard().getImmediateEffect().getSurplus());

        }

        board += "\n";

        board += ("|   |    |   |            |    |   |            |    |   |            |    |   |         |");

        if (getBuildingTower().get(1).getDvptCard() != null)

        {

            board += "   " + getBuildingTower().get(1).getDvptCard().getId() + ". " + getBuildingTower().get(1).getDvptCard().getName() + "   Cost:  " + printDvptCost(getBuildingTower().get(1).getDvptCard());
            board += " Imm: " + printSurplus(getBuildingTower().get(1).getDvptCard().getImmediateEffect().getSurplus());

        }

        board += "\n";


        board += printTowerLine(1);


        if (getBuildingTower().get(0).getDvptCard() != null) {

            board += "   " + getBuildingTower().get(0).getDvptCard().getId() + ". " + getBuildingTower().get(0).getDvptCard().getName() + "   Cost:  " + printDvptCost(getBuildingTower().get(0).getDvptCard());
            board += " Imm: " + printSurplus(getBuildingTower().get(0).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |    |___|            |    |___|            |    |___|            |    |___|         |");

        if (getVentureTower().get(3).getDvptCard() != null)

        {
            board += "   " + getVentureTower().get(3).getDvptCard().getId() + ". " + getVentureTower().get(3).getDvptCard().getName() + "   Cost:  " + printDvptCost(getVentureTower().get(3).getDvptCard());
            board += " Imm: " + printSurplus(getVentureTower().get(3).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |____|                |____|                |____|                |____|             |");


        if (getVentureTower().get(2).getDvptCard() != null) {

            board += "   " + getVentureTower().get(2).getDvptCard().getId() + ". " + getVentureTower().get(2).getDvptCard().getName() + "   Cost:  " + printDvptCost(getVentureTower().get(2).getDvptCard());
            board += " Imm: " + printSurplus(getVentureTower().get(2).getDvptCard().getImmediateEffect().getSurplus());

        }

        board += "\n";


        board += ("|   |    |___             |    |___             |    |___             |    |___          |");
        ;

        if (getVentureTower().get(1).getDvptCard() != null) {

            board += "   " + getVentureTower().get(1).getDvptCard().getId() + ". " + getVentureTower().get(1).getDvptCard().getName() + "   Cost:  " + printDvptCost(getVentureTower().get(1).getDvptCard());
            board += " Imm: " + printSurplus(getVentureTower().get(1).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += ("|   |    |   |            |    |   |            |    |   |            |    |   |         |");

        if (getVentureTower().get(0).getDvptCard() != null) {

            board += "   " + getVentureTower().get(0).getDvptCard().getId() + ". " + getVentureTower().get(0).getDvptCard().getName() + "   Cost:  " + printDvptCost(getVentureTower().get(0).getDvptCard());
            board += " Imm: " + printSurplus(getVentureTower().get(0).getDvptCard().getImmediateEffect().getSurplus());

        }
        board += "\n";

        board += printTowerLine(0) + "\n";

        board += ("|   |    |___|            |    |___|            |    |___|            |    |___|         |\n");

        board += ("|   |____|                |____|                |____|                |____|             |\n");

        board += ("|                                                                                        |\n");

        board += ("|                                                                                        |\n");

        board += ("|        CATHEDRAL                                            COUNCIL PALACE             |\n");

        board += ("|     ________________                                       ____________________________|\n");

        board += printCouncilPalace();

        board += printCathedralLine();
        board += "|     |" + printBannedPlayers(first) + "|" + printBannedPlayers(second) + "|" + printBannedPlayers(third) + "|" + "                                                                   |\n";

        board += ("|     |____|____|____|                                                                   |\n");

        board += ("|                                                                                        |\n");

        board += ("|                                                                                        |\n");

        board += ("|                                                      MARKET                            |\n");

        board += printMarketBonusLine();

        board += ("|                                    _______      _______      _______      _______      |\n");

        board += printMarketLine();

        board += ("|                                    |_____|      |_____|      |_____|      |_____|      |\n");

        board += ("|      SINGLE  PLACES                                                                    |\n");

        board += ("|    PRODUCTION HARVEST                                                                  |\n");

        board += ("|    _______     _______                                             DICES               |\n");

        board += printMainPHPlacesLine();

        board += ("|    |_____|     |_____|                                                                 |\n");

        board += ("|                                                  ");
        board += (AnsiColors.ANSI_BLACK);
        board += ("     BLACK  ");
        board += (AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_WHITE);
        board += ("    WHITE  ");
        board += (AnsiColors.ANSI_RESET);
        board += (AnsiColors.ANSI_RED);
        board += ("    ORANGE   ");
        board += (AnsiColors.ANSI_RESET);
        board += ("  |\n");

        board += ("|                                                       _______    _______    _______    |\n");

        board += printDicesLine();

        board += ("|                                                       |_____|    |_____|    |_____|    |\n");

        board += ("|     MULTIPLE  PLACES                                                                   |\n");

        board += ("|    PRODUCTION HARVEST                                                                  |\n");

        board += ("|    _______     _______                                                                 |\n");

        board += printMultiplePHPlacesLine();

        board += ("|    |_____|     |_____|                                                                 |\n");

        board += ("|________________________________________________________________________________________|\n");

        return board;
    }


    public String printDvptCost(DvptCard card) {

        String board = new String();
        for (Cost cost : card.getCost()) {
            if (cost.getResources() != null) {
                for (Resource resource : cost.getResources())
                    board += printSingleResource(resource);
            }
            if (cost.getMilitary() != null) {
                board += printMilitaryCost(cost.getMilitary());
            }
            board += " ";
        }
        return board;
    }

    /**
     * This method prints a line on the towers according to the different floor of them
     **/

    public String printTowerLine(Integer line) {

        String board = new String();

        board += "|   | " + printCardId(this.getTerritoryTower().get(line).getDvptCard()) + " |" + printFamilyMember(this.getTerritoryTower().get(line).getFamilyMember());

        board += " |            | " + printCardId(this.getCharacterTower().get(line).getDvptCard()) + " |" + printFamilyMember(this.getCharacterTower().get(line).getFamilyMember());

        board += " |            | " + printCardId(this.getBuildingTower().get(line).getDvptCard()) + " |" + printFamilyMember(this.getBuildingTower().get(line).getFamilyMember());

        board += " |            | " + printCardId(this.getVentureTower().get(line).getDvptCard()) + " |" + printFamilyMember(this.getVentureTower().get(line).getFamilyMember()) + " |         |";

        return board;

    }

    public String printBannedPlayers(Period period) {
        String board = new String();
        for (Player player : players) {
            Boolean banned = false;
            for (BanCard banCard : player.getBanCards()) {
                if (banCard.getId() == cathedral.getBanCard(period).getId()) {
                    banned = true;
                }
            }
            if (banned == true) {
                if (player.getColor() == PlayerColor.Red)
                    board += (AnsiColors.ANSI_RED);

                if (player.getColor() == PlayerColor.Blue)
                    board += (AnsiColors.ANSI_BLUE);

                if (player.getColor() == PlayerColor.Black)
                    board += (AnsiColors.ANSI_BLACK);

                if (player.getColor() == PlayerColor.Purple)
                    board += (AnsiColors.ANSI_PURPLE);

                if (player.getColor() == PlayerColor.Cyan)
                    board += (AnsiColors.ANSI_CYAN);

                if (player.getColor() == PlayerColor.Green)
                    board += (AnsiColors.ANSI_GREEN);

                if (player.getColor() == PlayerColor.Yellow)
                    board += (AnsiColors.ANSI_YELLOW);

                if (player.getColor() == PlayerColor.White)
                    board += (AnsiColors.ANSI_WHITE);
                board += "*";
                board += (AnsiColors.ANSI_RESET);
            } else
                board += " ";
        }
        if(players.size() == 3)
            board += " ";
        if(players.size() == 2)
            board += "  ";
        return board;
    }

    /** This method prints a particular family member**/


    public static String printCardId(DvptCard card) {
        String id = new String();
        if (card == null)
            id += "  ";
        else if (card.getId() < 10)
            id += (" " + card.getId());
        else
            id += (card.getId());
        return id;
    }

    public String printBanId(BanCard card) {
        String id = new String();
        if (card == null)
            id += "  ";
        else if (card.getId() < 10)
            id += (" " + card.getId());
        else
            id += (card.getId());
        return id;}

    public String printFamilyMember(FamilyMember familyMember) {
        String board = new String();

        if(familyMember == null)
            board += ("  ");

        else {

            if(familyMember.getPlayerColor() == PlayerColor.Red)
                board += (AnsiColors.ANSI_RED );

            if(familyMember.getPlayerColor() == PlayerColor.Blue)
                board += (AnsiColors.ANSI_BLUE);

            if(familyMember.getPlayerColor() == PlayerColor.Black)
                board += (AnsiColors.ANSI_BLACK );

            if(familyMember.getPlayerColor() == PlayerColor.Purple)
                board += (AnsiColors.ANSI_PURPLE );

            if(familyMember.getPlayerColor() == PlayerColor.Cyan)
                board += (AnsiColors.ANSI_CYAN );

            if(familyMember.getPlayerColor() == PlayerColor.Green)
                board += (AnsiColors.ANSI_GREEN );

            if(familyMember.getPlayerColor() == PlayerColor.Yellow)
                board += (AnsiColors.ANSI_YELLOW );

            if(familyMember.getPlayerColor() == PlayerColor.White)
                board += (AnsiColors.ANSI_WHITE);

            if (familyMember.getColor() == ColorType.Orange) {
                board += ("OF");
            }

            if (familyMember.getColor() == ColorType.Black) {
                board += ("BF");
            }

            if (familyMember.getColor() == ColorType.White) {
                board += ("WF");
            }

            if (familyMember.getColor() == ColorType.Neutral) {
                board += ("NF");
            }
            board += (AnsiColors.ANSI_RESET);

        }
        return board;
    }

    /** This method prints a line which contains the BanCards occupying the CouncilPalace **/

    public String printCathedralLine() {
        String board = new String();

        board += "|     | " + printBanId(getCathedral().getBanCard(first)) +" | " + printBanId(getCathedral().getBanCard(second)) + " | " + printBanId(getCathedral().getBanCard(third)) + " |                                      |____________________________\n";

        return board;
    }

    /** This method prints a line which contains the Family Members, ordered, occupying the Council Palace  **/

    public String printCouncilPalace() {
        String board = new String();
        board += ("|     |    |    |    |                                      |");
        for(FamilyMember member : getCouncilPalace().getPlaces()) {
            board += (" ");
            board += printFamilyMember(member);
            board += (" ");
        }

        board += ("\n");

        return board;

    }

    /** This method prints a line which contains the bonus of the market places **/

    public String printMarketBonusLine() {

        String board = new String();

        board += ("|                                     ");

        for(int i=0; i<2; i++){
            board += printSurplus(getMarket().getMarketPlaces().get(i).getEffectSurplus());
            board += "         ";
        }

        for(int i=2; i<4; i++){
            board += printSurplus(getMarket().getMarketPlaces().get(i).getEffectSurplus());
            board += "       ";
        }

        board += "|\n";

        return board;
    }

    public String printSingleResource (Resource resource){

        String board = new String();

        if (resource.getType() == ResourceType.Coins)
            board += resource.getAmount() + "" + UnicodeChars.Coins + " ";

        if (resource.getType() == ResourceType.Servants)
            board += resource.getAmount() + "" + UnicodeChars.Servants + " ";

        if (resource.getType() == ResourceType.Wood)
            board += resource.getAmount() + "" + UnicodeChars.Wood + " ";

        if (resource.getType() == ResourceType.Stones)
            board += resource.getAmount() + "" + UnicodeChars.Stones + " ";

        return board;

    }

    public String printMilitaryCost (MilitaryCost militaryCost) {
        String board = new String();

        if (militaryCost != null && militaryCost.getRequired() != 0)
            board += UnicodeChars.MilitaryPoints + " " + militaryCost.getRequired() + "/" + militaryCost.getMalus() + " ";

        else
            board += "";

        return board;
    }

    public static String printSurplus(EffectSurplus effectSurplus) {
        String bonus = new String();

        if (effectSurplus.getResources() != null) {

            for (Resource resource : effectSurplus.getResources()) {

                if (resource.getType() == ResourceType.Coins)
                    bonus += resource.getAmount() + "" + UnicodeChars.Coins + " ";

                if (resource.getType() == ResourceType.Servants)
                    bonus += resource.getAmount() + "" + UnicodeChars.Servants+ " ";

                if (resource.getType() == ResourceType.Wood)
                    bonus += resource.getAmount() + "" + UnicodeChars.Wood + " ";

                if (resource.getType() == ResourceType.Stones)
                    bonus += resource.getAmount() + "" + UnicodeChars.Stones + " ";

            }

        }

        if (effectSurplus.getPoints() != null) {

            for (Point point : effectSurplus.getPoints()) {

                if (point.getType() == PointType.Victory)
                    bonus += point.getAmount() + " " + UnicodeChars.VictoryPoints + " ";

                if (point.getType() == PointType.Military)
                    bonus += point.getAmount() + " " + UnicodeChars.MilitaryPoints + " ";

                if (point.getType() == PointType.Faith)
                    bonus += point.getAmount() + " " + UnicodeChars.FaithPoints + " ";

            }

        }

        if (effectSurplus.getCouncil() != 0) {
            bonus += effectSurplus.getCouncil() + " " + UnicodeChars.Council;
        }

        return bonus;

    }

    public String printMarketLine() {
        String board = new String();

        board += ("|                                    | ");

        for (int i=0; i<4; i++) {
            board += printFamilyMember(market.getMarketPlaces().get(i).getFamilyMember());
            board += ("  |      | ");
        }

        board += ("\n");

        return board;
    }

    /** This method prints a line which contains the Family Members occupying the main place of Production and Harvest Area**/


    public String printMainPHPlacesLine() {
        String board = new String();
        board += ("|    | ");

        board += printFamilyMember(productionArea.getMainPlace().getFamilyMember());

        board += ("  |     | ");

        board += printFamilyMember(harvestArea.getMainPlace().getFamilyMember());

        board += ("  |                                                                 |");
        board += ("\n");
        return board;
    }


    /** This method prints some lines that contain the Family Members occupying the multiple places of Production and Harvest Area **/

    public String printMultiplePHPlacesLine() {
        String board = new String();
        Integer max;

        if(productionArea.getSecondaryPlace().getPlaces().size() > harvestArea.getSecondaryPlace().getPlaces().size())
            max = productionArea.getSecondaryPlace().getPlaces().size();

        else
            max = harvestArea.getSecondaryPlace().getPlaces().size();

        for(int i=0; i<max; i++){
            board += ("|    | ");

            if(productionArea.getSecondaryPlace().getPlaces().size() > i)
                board += printFamilyMember(productionArea.getSecondaryPlace().getPlaces().get(i));
            else
                board += ("  ");
            board += ("  |     | ");

            if(harvestArea.getSecondaryPlace().getPlaces().size() > i)
                board += printFamilyMember(harvestArea.getSecondaryPlace().getPlaces().get(i));
            else
                board += ("  ");

            board += ("  |                                                                 |");
            board += ("\n");
        }

        return board;
    }

    /** This method prints a line with the value of dices on board **/

    public String printDicesLine(){
        String board = new String();
        board += ("|                                                       | ");
        board += (AnsiColors.ANSI_BLACK);
        if(dices == null)
            board += " ";
        else
            board += (dices.get(0).getValue());
        board += (AnsiColors.ANSI_RESET);
        board += ("   |    | ");
        board += (AnsiColors.ANSI_WHITE);
        if(dices == null)
            board += " ";
        else
            board += (dices.get(1).getValue());
        board += (AnsiColors.ANSI_RESET);
        board += ("   |    | ");
        board += (AnsiColors.ANSI_RED);
        if(dices == null)
            board += " ";
        else
            board += (dices.get(2).getValue());
        board += (AnsiColors.ANSI_RESET);
        board += ("   |    | ");
        board += ("\n");
        return board;
    }
}