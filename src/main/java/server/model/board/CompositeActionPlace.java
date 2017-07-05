package server.model.board;

import server.model.effect.EffectSurplus;
import server.model.effect.ImmediateEffect;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class CompositeActionPlace extends ActionPlace implements Serializable {

    protected ArrayList<FamilyMember> familyMembers;

    public CompositeActionPlace(EffectSurplus effectSurplus, Integer entryForce, Integer minPlayers) {
        super(effectSurplus, entryForce, minPlayers);
        familyMembers = new ArrayList<FamilyMember>();
    }

    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(ArrayList<FamilyMember> places) {

        this.familyMembers = places;
    }

    public void placeFamilyMember(FamilyMember familyMember){
        this.familyMembers.add(familyMember);
    }

    public void clean(){

        this.familyMembers.removeAll(familyMembers);

    }
}
