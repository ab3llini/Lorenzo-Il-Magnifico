package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import server.model.valuable.Resource;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;

public class Cost implements Serializable {

    private ArrayList<Resource> resources;
    private MilitaryCost military;


    public Cost(ArrayList<Resource> resources, MilitaryCost military) {
        this.resources = resources;
        this.military = military;
    }

    /**
     * clone the cost
     * @param cost
     */
    public Cost(Cost cost){

        ArrayList<Resource> resources = new ArrayList<>();
        MilitaryCost militaryCost;

        for (Resource resource: cost.getResources()) {
            resources.add(new Resource(resource.getType(),resource.getAmount()));
        }

        militaryCost = new MilitaryCost(cost.getMilitary().getRequired(),cost.getMilitary().getMalus());

        this.resources  = resources;
        this.military = militaryCost;

    }

    public MilitaryCost getMilitary() {
        return military;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }


    @Override
    public String toString() {

        String cost= "";

        for (Resource resource: this.resources) {

            if(resources.indexOf(resource) == resources.size()-1) {

                cost += resource.toString();
            }

            else
            {
                cost += resource.toString()+", ";
            }
        }

        if(military.getRequired()>0)

            cost += "Military Points "+ UnicodeChars.MilitaryPoints+" -> Required : "+military.getRequired()+" Malus : "+military.getMalus();

        return cost;
    }


}
