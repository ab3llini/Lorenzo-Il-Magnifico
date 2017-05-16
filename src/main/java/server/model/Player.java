package server.model;

import exception.*;
import server.model.board.ColorType;
import server.model.board.FamilyMember;
import server.model.board.PersonalBoard;

import java.util.ArrayList;

/*
 * Created by alberto on 10/05/17.
 */
public class Player {
    private String username;
    private PersonalBoard personalBoard;
    private Integer victoryPoints;
    private Integer faithPoints;
    private Integer militaryPoints;
    private Integer coins;
    private Integer stones;
    private Integer wood;
    private Integer servants;
    private ArrayList<FamilyMember> familyMembers;

    public Player(String username){
        this.username=username;
        this.faithPoints=0;
        this.victoryPoints=0;
        this.militaryPoints=0;
    }

    public void setPersonalBoard(PersonalBoard personalBoard) {
        this.personalBoard = personalBoard;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public String getUsername() {
        return username;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getServants() {
        return servants;
    }

    public void setServants(Integer servants) {
        this.servants = servants;
    }

    public Integer getStones() {
        return stones;
    }

    public void setStones(Integer stones) {
        this.stones = stones;
    }

    public Integer getWood() {
        return wood;
    }

    public void setWood(Integer wood) {
        this.wood = wood;
    }

    public Integer getMilitaryPoints() {
        return militaryPoints;
    }

    public Integer getVictoryPoints() {
        return victoryPoints;
    }

    public Integer getFaithPoints() {
        return faithPoints;
    }

    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(ArrayList<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void addCoins(Integer coinsPlus){
        this.coins=this.coins+coinsPlus;
    }

    public void addWood(Integer woodPlus){
        this.wood=this.wood+woodPlus;
    }

    public void addStones(Integer stonesPlus){
        this.stones=this.stones+stonesPlus;
    }

    public void addServants(Integer servantsPlus){
        this.servants=this.servants+servantsPlus;
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
            if(member.getForce()>=(forceRequired-this.servants))
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

        if(this.coins>=coinsMalus){
            this.coins=this.coins-coinsMalus;
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

        if(this.wood>=woodMalus){
            this.wood=this.wood-woodMalus;
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

        if(this.stones>=stonesMalus){
            this.stones=this.stones-stonesMalus;
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

        if(this.servants>=servantsMalus){
            this.servants=this.servants-servantsMalus;
        }
        else {
            throw new NotEnoughServantsException("Not enough servants to do this");
        }
    }
}

