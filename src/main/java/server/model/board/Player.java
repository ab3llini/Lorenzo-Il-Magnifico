package server.model.board;

import exception.*;
import server.model.board.ColorType;
import server.model.board.FamilyMember;
import server.model.board.PersonalBoard;
import server.model.valuable.PointType;
import server.model.valuable.ResourceType;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Created by alberto on 10/05/17.
 */
public class Player {

    private String username;
    private PersonalBoard personalBoard;
    private HashMap<ResourceType,Integer> resources;
    private HashMap<PointType,Integer> points;
    private ArrayList<FamilyMember> familyMembers;

    public Player(String username){

        this.username=username;

        //initialize hashMap that contains all resources
        resources=new HashMap<ResourceType, Integer>();

        //put all kind of resource in the hashMap
        resources.put(ResourceType.Coins,0);
        resources.put(ResourceType.Servants,0);
        resources.put(ResourceType.Stones,0);
        resources.put(ResourceType.Servants,0);

        //initialize hashMap that contains all points
        points=new HashMap<PointType, Integer>();

        //put all kind of points in the hashMap
        points.put(PointType.Military,0);
        points.put(PointType.Victory,0);
        points.put(PointType.Faith,0);
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

    public Integer getCoins() {return this.resources.get(ResourceType.Coins);
    }

    public void setCoins(Integer coins){this.resources.put(ResourceType.Coins,coins);
    }

    public Integer getServants() {
        return resources.get(ResourceType.Servants);
    }

    public void setServants(Integer servants) {
        this.resources.put(ResourceType.Servants,servants);
    }

    public Integer getStones() {
        return this.resources.get(ResourceType.Stones);
    }

    public void setStones(Integer stones) {
        this.resources.put(ResourceType.Stones,stones);
    }

    public Integer getWood() {
        return this.resources.get(ResourceType.Wood);
    }

    public void setWood(Integer wood) {
        this.resources.put(ResourceType.Wood,wood);
    }

    public Integer getMilitaryPoints() {
        return this.points.get(PointType.Military);
    }

    public void setMilitaryPoints(Integer militaryPoints){
        this.points.put(PointType.Military,militaryPoints);
    }

    public void setVictoryPoints(Integer victoryPoints){
        this.points.put(PointType.Victory,victoryPoints);
    }

    public void setFaithPoints(Integer faithPoints){
        this.points.put(PointType.Faith,faithPoints);
    }

    public Integer getVictoryPoints() {
        return this.points.get(PointType.Victory);
    }

    public Integer getFaithPoints() {
        return this.points.get(PointType.Faith);
    }

    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(ArrayList<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void addCoins(Integer coinsPlus){
        this.resources.put(ResourceType.Coins,getCoins()+coinsPlus);
    }

    public void addWood(Integer woodPlus){
        this.resources.put(ResourceType.Wood,getWood()+woodPlus);
    }

    public void addStones(Integer stonesPlus){
        this.resources.put(ResourceType.Stones,getStones()+stonesPlus);
    }

    public void addServants(Integer servantsPlus){
        this.resources.put(ResourceType.Servants,getServants()+servantsPlus);
    }

    public void addMilitaryPoints(Integer militaryPlus){
        this.points.put(PointType.Military,getMilitaryPoints()+militaryPlus);
    }

    public void addVictoryPoints(Integer victoryPlus){
        this.points.put(PointType.Victory,getVictoryPoints()+victoryPlus);
    }

    public void addFaithPoints(Integer faithPlus){
        this.points.put(PointType.Faith,getFaithPoints()+faithPlus);
    }

    /**
     * this method returns a specific family member depending on its color
     * @param colorType
     * @return
     * @throws FamilyMemberAlreadyInUseException
     */
    public FamilyMember getFamilymember(ColorType colorType) throws FamilyMemberAlreadyInUseException {

        FamilyMember memberWanted=null;

        //search through the array of family members until finds the member we want
        for (FamilyMember member: familyMembers) {
            if(member.getColor()==colorType){
                memberWanted=member;
                break;}
        }

        //the member wanted can be already in use
        if(memberWanted.isBusy()){
            throw new FamilyMemberAlreadyInUseException("this family member is already in use");
        }
        else {
            return memberWanted;
        }
    }


    /**
     * this method returns an array of family members that has enough force(or they can have with an addition of servants) to do an action
     * @return
     */
    public ArrayList<FamilyMember> getFamilyMembersSuitable(Integer forceRequired){
        ArrayList<FamilyMember> familyMembersSuitable=new ArrayList<FamilyMember>();

        for (FamilyMember member:familyMembers) {
            if(member.getForce()>=(forceRequired-this.resources.get(ResourceType.Servants)))
                familyMembersSuitable.add(member);
        }
        return familyMembersSuitable;
    }

    /**
     * this method subtract a coinsMalus if the player has enough money
     * @param coinsMalus
     * @throws NotEnoughCoinsException
     */
    public void subtractCoins(Integer coinsMalus) throws NotEnoughCoinsException{

        if(this.resources.get(ResourceType.Coins)>=coinsMalus){
            resources.put(ResourceType.Coins,getCoins()-coinsMalus);
        }
        else{
            throw new NotEnoughCoinsException("Not enough money to do this");
        }
    }

    /**
     * this method subtract a woodMalus if the player has enough wood
     * @param woodMalus
     * @throws NotEnoughWoodException
     */
    public void subtractWood(Integer woodMalus) throws NotEnoughWoodException {

        if(this.resources.get(ResourceType.Wood)>=woodMalus){
            this.resources.put(ResourceType.Wood,getWood()-woodMalus);
        }
        else{
            throw new NotEnoughWoodException("Not enough wood to do this");
        }
    }

    /**
     * this method subtract a stonesMalus if the player has enough stones
     * @param stonesMalus
     * @throws NotEnoughStonesException
     */
    public void subtractStones(Integer stonesMalus) throws NotEnoughStonesException {

        if(this.resources.get(ResourceType.Stones)>=stonesMalus){
            this.resources.put(ResourceType.Stones,getStones()-stonesMalus);
        }
        else {
            throw new NotEnoughStonesException("Not enough stones to do this");
        }
    }

    /**
     * this method subtract a servantsMalus if the player has enough servants
     * @param servantsMalus
     * @throws NotEnoughServantsException
     */
    public void subtractServants(Integer servantsMalus) throws NotEnoughServantsException {

        if(this.resources.get(ResourceType.Servants)>=servantsMalus){
            this.resources.put(ResourceType.Servants,getServants()-servantsMalus);
        }
        else {
            throw new NotEnoughServantsException("Not enough servants to do this");
        }
    }

    /**
     * this method subtract a militaryMalus if the player has enough military points
     * @param militaryMalus
     */
    public void subtractMilitaryPoints(Integer militaryMalus) throws NotEnoughMilitaryPointsException{

        if(this.points.get(PointType.Military)>=militaryMalus){
            this.points.put(PointType.Military,getMilitaryPoints()-militaryMalus);
        }
        else{
            throw  new NotEnoughMilitaryPointsException("Not enough military points to do this");
        }
    }

    /**
     * this method subtract a victoryMalus if the player has enough victory points
     * @param victoryMalus
     * @throws NotEnoughVictoryPointsException
     */
    public void subtractVictoryPoints(Integer victoryMalus) throws NotEnoughVictoryPointsException {

        if(this.points.get(PointType.Victory)>=victoryMalus){
            this.points.put(PointType.Victory,getVictoryPoints()-victoryMalus);
        }
        else{
            throw  new NotEnoughVictoryPointsException("Not enough military points to do this");
        }
    }

    /**
     * this method subtract a faithMalus if the player has enough faith points
     * @param faithMalus
     * @throws NotEnoughFaithPointsException
     */
    public void subtractFaithPoints(Integer faithMalus) throws NotEnoughFaithPointsException {

        if(this.points.get(PointType.Faith)>=faithMalus){
            this.points.put(PointType.Faith,getFaithPoints()-faithMalus);
        }
        else {
            throw new NotEnoughFaithPointsException("Not enough faith points to do this");
        }
    }
}

