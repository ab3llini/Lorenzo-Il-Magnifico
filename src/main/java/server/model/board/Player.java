package server.model.board;

import exception.*;
import logger.AnsiColors;
import server.model.GameSingleton;
import server.model.card.ban.BanCard;
import server.model.card.developement.Cost;
import server.model.card.developement.DvptCardType;
import server.model.card.leader.LeaderCard;
import server.model.effect.PermanentLeaderEffectType;
import server.model.valuable.*;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static server.model.card.developement.DvptCardType.*;

/*
 * Created by alberto on 10/05/17.
 * All method signature has been implemented by LBARCELLA on 15/05/17
 */
public class Player implements Serializable {

    private String username;
    private PersonalBoard personalBoard;
    private HashMap<ResourceType, Integer> resources;
    private HashMap<PointType, Integer> points;
    private ArrayList<FamilyMember> familyMembers;
    private ArrayList<BanCard> banCards;
    private ArrayList<LeaderCard> leaderCards;
    private ArrayList<LeaderCard> playedLeaderCards;
    private ArrayList<LeaderCard> turnActiveLeaderCards;
    private PlayerColor color;


    //Very important attribute both for match controller & lobby, do not edit unless 100% certain of what you are doing.
    private boolean disabled = false;


    public Player(String username) {

        this.username = username;

        //initialize hashMap that contains all resources

        resources = new HashMap<ResourceType, Integer>();

        //put all kind of resource in the hashMap


        resources.put(ResourceType.Coins, 0);
        resources.put(ResourceType.Servants, 0);
        resources.put(ResourceType.Stones, 0);
        resources.put(ResourceType.Wood, 0);

        //initialize hashMap that contains all points

        points = new HashMap<PointType, Integer>();

        //put all kind of points kin the hashMap

        points.put(PointType.Military, 0);
        points.put(PointType.Victory, 0);
        points.put(PointType.Faith, 0);

        //create the arrayList with all the player's familyMembers

        familyMembers = new ArrayList<FamilyMember>();
        familyMembers.add(new FamilyMember(this.getColor(), ColorType.Black));
        familyMembers.add(new FamilyMember(this.getColor(), ColorType.Orange));
        familyMembers.add(new FamilyMember(this.getColor(), ColorType.White));
        familyMembers.add(new FamilyMember(this.getColor(), ColorType.Neutral));

        //initialize player's board
        this.personalBoard = new PersonalBoard();

        this.banCards = new ArrayList<BanCard>();

        this.leaderCards = new ArrayList<LeaderCard>();

        this.playedLeaderCards = new ArrayList<LeaderCard>();

        this.turnActiveLeaderCards = new ArrayList<LeaderCard>();

    }

    public void setFamilyMemberForce(ColorType color, int force) {

        for (FamilyMember fm : this.familyMembers) {

            if (fm.getColor() == color) {

                fm.setForce(force);

                break;

            }

        }

    }

    public void setFamilyMembersPlayerColor(){

        for (FamilyMember familyMember : this.familyMembers) {

            familyMember.setPlayerColor(this.color);

        }
    }

    public void setPersonalBoard(PersonalBoard personalBoard) {
        this.personalBoard = personalBoard;
    }

    public PersonalBoard getPersonalBoard() {
        return this.personalBoard;
    }

    public String getUsername() {
        return this.username;
    }

    public ArrayList<LeaderCard> getTurnActiveLeaderCard() {
        return turnActiveLeaderCards;
    }

    public void setTurnActiveLeaderCard(ArrayList<LeaderCard> turnActiveLeaderCard) {
        this.turnActiveLeaderCards = turnActiveLeaderCard;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public Integer getCoins() {
        return this.resources.get(ResourceType.Coins);
    }

    public void setCoins(Integer coins) {
        this.resources.put(ResourceType.Coins, coins);
    }

    public Integer getServants() {
        return resources.get(ResourceType.Servants);
    }

    public void setServants(Integer servants) {
        this.resources.put(ResourceType.Servants, servants);
    }

    public Integer getStones() {
        return this.resources.get(ResourceType.Stones);
    }

    public void setStones(Integer stones) {
        this.resources.put(ResourceType.Stones, stones);
    }

    public Integer getWood() {
        return this.resources.get(ResourceType.Wood);
    }

    public void setWood(Integer wood) {
        this.resources.put(ResourceType.Wood, wood);
    }

    public Integer getResource(ResourceType resourceType) {

        if (resourceType == ResourceType.Coins)

            return getCoins();

        if (resourceType == ResourceType.Wood)

            return getWood();

        if (resourceType == ResourceType.Servants)

            return getServants();

        else

            return getStones();

    }

    public ArrayList<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public ArrayList<LeaderCard> getTurnActiveLeaderCards() {
        return turnActiveLeaderCards;
    }

    public Integer getMilitaryPoints() {
        return this.points.get(PointType.Military);
    }

    public Integer getVictoryPoints() {
        return this.points.get(PointType.Victory);
    }

    public Integer getFaithPoints() {
        return this.points.get(PointType.Faith);
     }

    public Integer getPoints(PointType pointType){

        if(pointType == PointType.Military)
            return getMilitaryPoints();

        if(pointType == PointType.Faith)
            return getFaithPoints();

        return getVictoryPoints();
    }



    public ArrayList<LeaderCard> getPlayedLeaderCards() {
        return playedLeaderCards;
    }

    public boolean isPermanentLeaderActive(PermanentLeaderEffectType permanentLeader){
        for(LeaderCard leaderCard : getPlayedLeaderCards()){
            if(permanentLeader == leaderCard.getLeaderEffect().getPermanentEffect())
                return true;
        }
        return false;
    }

    public void setMilitaryPoints(Integer militaryPoints) {
        this.points.put(PointType.Military, militaryPoints);
    }

    public void setVictoryPoints(Integer victoryPoints) {
        this.points.put(PointType.Victory, victoryPoints);
    }

    public void setFaithPoints(Integer faithPoints) {
        this.points.put(PointType.Faith, faithPoints);
    }

    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public ArrayList<BanCard> getBanCards() {
        return banCards;
    }

    public boolean addBanCard(BanCard card) {
        return this.banCards.add(card);
    }

    public void addLeaderCard(LeaderCard card) {

        if (this.leaderCards != null) {

            this.leaderCards.add(card);

        }
    }

    public void setFamilyMembers(ArrayList<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void addCoins(Integer coinsPlus) {
        this.resources.put(ResourceType.Coins, getCoins() + coinsPlus);
    }

    public void addWood(Integer woodPlus) {
        this.resources.put(ResourceType.Wood, getWood() + woodPlus);
    }

    public void addStones(Integer stonesPlus) {
        this.resources.put(ResourceType.Stones, getStones() + stonesPlus);
    }

    public void addServants(Integer servantsPlus) {

        this.resources.put(ResourceType.Servants, getServants() + servantsPlus);

    }

    public void addMilitaryPoints(Integer militaryPlus) {

        this.points.put(PointType.Military, getMilitaryPoints() + militaryPlus);

    }

    public void addVictoryPoints(Integer victoryPlus) {

        this.points.put(PointType.Victory, getVictoryPoints() + victoryPlus);

    }

    public void addFaithPoints(Integer faithPlus) {
        this.points.put(PointType.Faith, getFaithPoints() + faithPlus);
    }

    /**
     * this method returns a specific family member depending on its color
     *
     * @param colorType
     * @return
     * @throws FamilyMemberAlreadyInUseException
     */
    public FamilyMember getFamilyMember(ColorType colorType) throws FamilyMemberAlreadyInUseException {

        FamilyMember memberWanted = null;

        //search through the array of family members until finds the member we want
        for (FamilyMember member : familyMembers) {

            if (member.getColor() == colorType) {

                memberWanted = member;

                break;

            }

        }

        //the member wanted can be already in use

        if (memberWanted.isBusy()) {

            throw new FamilyMemberAlreadyInUseException("this family member is already in use");

        } else {

            return memberWanted;

        }

    }


    //Get all the family members
    public FamilyMember getSpecificFamilyMemberInfo(ColorType colorType) {

        FamilyMember memberWanted = null;

        //search through the array of family members until finds the member we want
        for (FamilyMember member : familyMembers) {

            if (member.getColor() == colorType) {

                memberWanted = member;

                break;

            }

        }

        return memberWanted;

    }

    /**
     * this method returns an array of family members that has enough force(or they can have with an addition of servants) to do an Action
     *
     * @return
     */

    public ArrayList<FamilyMember> getFamilyMembersSuitable(Integer forceRequired) {

        ArrayList<FamilyMember> familyMembersSuitable = new ArrayList<FamilyMember>();

        for (FamilyMember member : familyMembers) {

            if (member.getForce() >= (forceRequired - this.resources.get(ResourceType.Servants)))

                familyMembersSuitable.add(member);

        }

        return familyMembersSuitable;

    }

    /**
     * this method subtract a coinsMalus if the player has enough money
     *
     * @param coinsMalus
     * @throws NotEnoughCoinsException
     */

    public void subtractCoins(Integer coinsMalus) throws NotEnoughCoinsException {

        if (this.resources.get(ResourceType.Coins) >= coinsMalus) {

            resources.put(ResourceType.Coins, getCoins() - coinsMalus);

        } else {

            throw new NotEnoughCoinsException("Not enough money to do this");

        }

    }

    /**
     * this method check if the player has enough coins to do something
     *
     * @param coinsMalus
     * @throws NotEnoughCoinsException
     */

    public boolean hasEnoughCoins(Integer coinsMalus) {
        return (this.resources.get(ResourceType.Coins) >= coinsMalus);
    }

    /**
     * this method subtract a woodMalus if the player has enough wood
     *
     * @param woodMalus
     * @throws NotEnoughWoodException
     */

    public void subtractWood(Integer woodMalus) throws NotEnoughWoodException {

        if (this.resources.get(ResourceType.Wood) >= woodMalus) {

            this.resources.put(ResourceType.Wood, getWood() - woodMalus);

        } else {

            throw new NotEnoughWoodException("Not enough wood to do this");
        }
    }

    /**
     * this method check if the player has enough wood to do something
     *
     * @param woodMalus
     */

    public boolean hasEnoughWood(Integer woodMalus) {
        return (this.resources.get(ResourceType.Wood) >= woodMalus);
    }

    /**
     * this method subtract a stonesMalus if the player has enough stones
     *
     * @param stonesMalus
     * @throws NotEnoughStonesException
     */

    public void subtractStones(Integer stonesMalus) throws NotEnoughStonesException {

        if (this.resources.get(ResourceType.Stones) >= stonesMalus) {

            this.resources.put(ResourceType.Stones, getStones() - stonesMalus);

        } else {

            throw new NotEnoughStonesException("Not enough stones to do this");

        }

    }

    /**
     * this method check if the player has enough stones to do something
     *
     * @param stonesMalus
     */

    public boolean hasEnoughStones(Integer stonesMalus) {

        return (this.resources.get(ResourceType.Stones) >= stonesMalus);

    }

    /**
     * this method subtract a servantsMalus if the player has enough servants
     *
     * @param servantsMalus
     * @throws NotEnoughServantsException
     */

    public void subtractServants(Integer servantsMalus) throws NotEnoughServantsException {

        if (this.resources.get(ResourceType.Servants) >= servantsMalus) {

            this.resources.put(ResourceType.Servants, getServants() - servantsMalus);

        } else {

            throw new NotEnoughServantsException("Not enough servants to do this");

        }

    }

    /**
     * this method check if the player has enough servants to do something
     *
     * @param servantsMalus
     */
    public boolean hasEnoughServants(Integer servantsMalus) {

        return (this.resources.get(ResourceType.Servants) >= servantsMalus);

    }

    /**
     * this method subtract a militaryMalus if the player has enough military points
     *
     * @param militaryMalus
     */
    public void subtractMilitaryPoints(Integer militaryMalus) throws NotEnoughMilitaryPointsException {

        if (this.points.get(PointType.Military) >= militaryMalus) {

            this.points.put(PointType.Military, getMilitaryPoints() - militaryMalus);

        } else {

            throw new NotEnoughMilitaryPointsException("Not enough military points to do this");

        }

    }

    /**
     * this method subtract a victoryMalus if the player has enough victory points
     *
     * @param victoryMalus
     * @throws NotEnoughVictoryPointsException
     */
    public void subtractVictoryPoints(Integer victoryMalus) throws NotEnoughVictoryPointsException {

        if (this.points.get(PointType.Victory) >= victoryMalus) {

            this.points.put(PointType.Victory, getVictoryPoints() - victoryMalus);

        } else {

            throw new NotEnoughVictoryPointsException("Not enough military points to do this");

        }

    }

    /**
     * this method subtract a faithMalus if the player has enough faith points
     *
     * @param faithMalus
     * @throws NotEnoughFaithPointsException
     */

    public void subtractFaithPoints(Integer faithMalus) throws NotEnoughFaithPointsException {

        if (this.points.get(PointType.Faith) >= faithMalus) {

            this.points.put(PointType.Faith, getFaithPoints() - faithMalus);

        } else {

            throw new NotEnoughFaithPointsException("Not enough faith points to do this");

        }

    }

    /**
     * this method check if the player has enough resources to do something
     *
     * @param resourceType
     * @param amount
     * @throws NotEnoughResourcesException
     */

    public boolean hasEnoughResources(ResourceType resourceType, Integer amount) {

        if (resourceType == ResourceType.Coins)

            return hasEnoughCoins(amount);

        if (resourceType == ResourceType.Stones)

            return hasEnoughStones(amount);

        if (resourceType == ResourceType.Servants)

            return hasEnoughServants(amount);

        else

            return hasEnoughWood(amount);
    }

    public boolean hasEnoughCostResources(Cost cost) {

        for (int j = 0; j < cost.getResources().size(); j++) {

            if (!this.hasEnoughResources(cost.getResources().get(j).getType(), cost.getResources().get(j).getAmount()))

                return false;

        }

        return true;

    }

    public boolean hasEnoughRequiredResources(ArrayList<Resource> resources) {

        for (int j = 0; j < resources.size(); j++) {

            if (!this.hasEnoughResources(resources.get(j).getType(), resources.get(j).getAmount()))

                return false;

        }

        return true;

    }

    public void subtractResources(Cost cost) throws NotEnoughResourcesException {

        for (int j = 0; j < cost.getResources().size(); j++) {

            this.subtractGenericResource(cost.getResources().get(j).getType(), cost.getResources().get(j).getAmount());

        }

    }

    public void addResources(ArrayList<Resource> resources)  {

        for (int j = 0; j < resources.size(); j++) {

            this.addGenericResource(resources.get(j).getType(), resources.get(j).getAmount());

        }

    }

    public void addPoints(ArrayList<Point> points){

        for (int j = 0; j < points.size(); j++) {

            this.addGenericPoint(points.get(j).getType(), points.get(j).getAmount());

        }

    }


    /**
     * this method check if the player has enough points to do something
     *
     */

    public boolean hasEnoughPoints(PointType pointType, Integer amount) {

        if (pointType == PointType.Military) {

            return hasEnoughMilitaryPoints(amount);

        }

        if (pointType == PointType.Victory) {

            return hasEnoughVictoryPoints(amount);

        }

        else {

            return hasEnoughFaithPoints(amount);

        }
    }


    /**
     * this method check if the player has enough points to activate a leader card
     */

    public boolean hasEnoughRequiredPoints(ArrayList<Point> points) {

        for (int j = 0; j < points.size(); j++) {

            if (!this.hasEnoughPoints(points.get(j).getType(), points.get(j).getAmount()))

                return false;
        }

        return true;
    }


    /**
     * this method verifies if there are enough points of a specific type (faith, victory, military) to activate a leader card
     */

    public boolean hasEnoughFaithPoints(Integer faithMalus) {

        return (this.points.get(PointType.Faith) >= faithMalus);
    }

    public boolean hasEnoughVictoryPoints(Integer victoryMalus) {

        return (this.points.get(PointType.Victory) >= victoryMalus);

    }


    public boolean hasEnoughMilitaryPoints(Integer militaryMalus) {

        return (this.points.get(PointType.Military) >= militaryMalus);

    }

    /**
     * this method subtract a specific amount of a resource from player resources
     *
     * @param resourceType
     * @param amount
     * @throws NotEnoughResourcesException
     */

    public void subtractGenericResource(ResourceType resourceType, Integer amount) throws NotEnoughResourcesException {

        if (resourceType == ResourceType.Coins)

            subtractCoins(amount);

        if (resourceType == ResourceType.Stones)

            subtractStones(amount);

        if (resourceType == ResourceType.Servants)

            subtractServants(amount);

        if (resourceType == ResourceType.Wood)

            subtractWood(amount);
    }

    /**
     * this method subtracts player's points by a specific amount of a points
     * @param pointType that specify which point type has to be increased
     * @param amount
     */

    public void subtractGenericPoint(PointType pointType, Integer amount) throws NotEnoughPointsException{

        if (pointType == PointType.Faith)

            subtractFaithPoints(amount);

        if (pointType == PointType.Military)

            subtractMilitaryPoints(amount);

        if (pointType == PointType.Victory)

            subtractVictoryPoints(amount);

    }


    /**
     * this method increase player resorces by a specific amount of a resources
     * @param resourceType that specify which resource type has to be increased
     * @param amount
     */
    public void addGenericResource(ResourceType resourceType, Integer amount) {

        if (resourceType == ResourceType.Coins)

            addCoins(amount);

        if (resourceType == ResourceType.Stones)

            addStones(amount);

        if (resourceType == ResourceType.Servants)

            addServants(amount);

        if (resourceType == ResourceType.Wood)

            addWood(amount);

    }


    /**
     * this method increase player's points by a specific amount of a points
     * @param pointType that specify which point type has to be increased
     * @param amount
     */

    public void addGenericPoint(PointType pointType, Integer amount) {

        if (pointType == PointType.Faith)

            addFaithPoints(amount);

        if (pointType == PointType.Military)

            addMilitaryPoints(amount);

        if (pointType == PointType.Victory)

            addVictoryPoints(amount);

    }



    public void setDisabled(boolean disabled) {

        this.disabled = disabled;

    }

    public boolean isDisabled() {

        return this.disabled;

    }

    public boolean hasEnoughTerritoryCard (Integer territoryMalus) {

        return this.personalBoard.getTerritoryCards().size() >= territoryMalus;

    }

    public boolean hasEnoughBuildingCard (Integer buildingMalus) {

        return this.personalBoard.getBuildingCards().size() >= buildingMalus;

    }

    public boolean hasEnoughCharacterCard (Integer characterMalus) {

        return this.personalBoard.getCharacterCards().size() >= characterMalus;

    }

    public boolean hasEnoughVentureCard (Integer ventureMalus) {

        return this.personalBoard.getVentureCards().size() >= ventureMalus;

    }


    /**
     * this method verify if player's cards of a specific
     * @param cardType are enough to do something
     */

    public boolean hasEnoughCards(DvptCardType cardType, Integer amount) {

        if (cardType == territory)

            return hasEnoughTerritoryCard(amount);

        if (cardType == building)

            return hasEnoughBuildingCard(amount);

        if (cardType == character)

            return hasEnoughCharacterCard(amount);

        else

            return hasEnoughVentureCard(amount);

    }

    /**
     * this method check if player's cards of a specific type are enough to do activate a leader card
     */

    public boolean hasEnoughRequiredCards(HashMap<DvptCardType,Integer> cards) {

        if (cards.containsKey(territory)){

            if (!this.hasEnoughCards(territory, cards.get(territory)))

                return false;

        }

        if (cards.containsKey(building)){

            if (!this.hasEnoughCards(building, cards.get(building)))

                return false;

        }

        if (cards.containsKey(character)){

            if (!this.hasEnoughCards(character, cards.get(character)))

                return false;
        }

        if (cards.containsKey(venture)) {

            if (!this.hasEnoughCards(venture, cards.get(venture)))

                return false;
        }

        return true;

    }

    public boolean hasSixIdentical (boolean SixIdentical){

        if (SixIdentical){

            if(this.hasEnoughCards(territory, 6))

                return true;

            if(this.hasEnoughCards(building, 6))

                return true;

            if(this.hasEnoughCards(character, 6))

                return true;

            if(this.hasEnoughCards(venture, 6))

                return true;

            return false;
        }

        return true;

    }

    /** this method gets the size of cards of a specific type on the personal board that must be multiplied **/

    public Integer getSizeMultipliedType (MultipliedType type){

        if(type == MultipliedType.territory)
            return this.personalBoard.getTerritoryCards().size();

        if(type == MultipliedType.building)
            return this.personalBoard.getBuildingCards().size();

        if(type == MultipliedType.character)
            return this.personalBoard.getCharacterCards().size();

        else
            return this.personalBoard.getVentureCards().size();
    }

    /**
     * this method checks if all requirement of a LeaderCard with a specific
     * @param leaderIndex are respected to activate it
     */

    public boolean hasEnoughLeaderRequirements(Integer leaderIndex) {

        boolean hasEnoughResources; //verifies if the player has enough resources to activate the Leader Card

        boolean hasEnoughPoints; //verifies if the player has enough points to activate the Leader Card

        boolean hasEnoughCards; //verifies if the player has enough cards of a specific type to activate the Leader Card

        boolean hasSixIdentical; //verifies if the player has six development card of the same type

        hasEnoughCards = hasEnoughRequiredCards(GameSingleton.getInstance().getSpecificLeaderCard(leaderIndex).getRequirement().getCardsRequired());

        hasEnoughResources = hasEnoughRequiredResources(GameSingleton.getInstance().getSpecificLeaderCard(leaderIndex).getRequirement().getResourceRequired());

        hasEnoughPoints = hasEnoughRequiredPoints(GameSingleton.getInstance().getSpecificLeaderCard(leaderIndex).getRequirement().getPointsRequired());

        hasSixIdentical = hasSixIdentical(GameSingleton.getInstance().getSpecificLeaderCard(leaderIndex).getRequirement().getSixIdentical());

        return hasEnoughCards && hasEnoughResources && hasEnoughPoints && hasSixIdentical;

    }

    /**
     * this method free family members at the end of the round
     */
    public void freeFamilyMembers () {

        for (FamilyMember familyMember: familyMembers) {

            familyMember.setBusy(false);

        }
    }

    /**
     * this method is useful to reset player resources and points when something goes wrong
     * @return
     */
    public void rollback(RollbackClass rollbackThings){

        this.setVictoryPoints(rollbackThings.getVictoryPoints());
        this.setMilitaryPoints(rollbackThings.getMilitaryPoints());
        this.setFaithPoints(rollbackThings.getFaithPoints());
        this.setStones(rollbackThings.getStones());
        this.setServants(rollbackThings.getServants());
        this.setCoins(rollbackThings.getCoins());
        this.setWood(rollbackThings.getWood());

    }

    public String toString2() {

        String playerString = "";

        playerString += UnicodeChars.Man+" "+this.username+"\n";

        playerString += "- coins "+UnicodeChars.Coins+": "+this.getCoins()+"\n";

        playerString += "- servants "+UnicodeChars.Servants+": "+this.getServants()+"\n";

        playerString += "- wood "+UnicodeChars.Wood+": "+this.getWood()+"\n";

        playerString += "- stones "+UnicodeChars.Stones+": "+this.getStones()+"\n";

        playerString += "- victory points "+UnicodeChars.VictoryPoints+": "+this.getVictoryPoints()+"\n";

        playerString += "- military points "+UnicodeChars.MilitaryPoints+": "+this.getMilitaryPoints()+"\n";

        playerString += "- faith points "+UnicodeChars.FaithPoints+": "+this.getFaithPoints()+"\n";

        return playerString;
    }

    @Override
    public String toString() {

        String playerString = "\n";

        if(color == PlayerColor.Red)
            playerString += (AnsiColors.ANSI_RED );

        if(color == PlayerColor.Blue)
            playerString += (AnsiColors.ANSI_BLUE );


        if(color == PlayerColor.Purple)
            playerString += (AnsiColors.ANSI_PURPLE );

        if(color == PlayerColor.Green)
            playerString += (AnsiColors.ANSI_GREEN);

        if(color == PlayerColor.Yellow)
            playerString += (AnsiColors.ANSI_YELLOW );

        playerString += (AnsiColors.ANSI_RESET );

        playerString += UnicodeChars.Man+" "+this.username.toUpperCase()+"\n";

        playerString += UnicodeChars.Coins+"\t"+UnicodeChars.Wood+"\t"+UnicodeChars.Stones+"\t"+UnicodeChars.Servants+"\t";

        playerString += UnicodeChars.VictoryPoints+"\t"+UnicodeChars.MilitaryPoints+"\t"+UnicodeChars.FaithPoints+"\n";

        playerString += this.getCoins()+"\t"+this.getWood()+"\t"+this.getStones()+"\t"+this.getServants()+"\t";

        playerString += this.getVictoryPoints()+"\t"+this.getMilitaryPoints()+"\t"+this.getFaithPoints();

        return playerString;
    }

}