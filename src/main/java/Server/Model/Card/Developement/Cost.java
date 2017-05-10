package Server.Model.Card.Developement;/*
 * Created by alberto on 09/05/17.
 */

import Server.Model.valuable.Resource;

import java.util.ArrayList;

public class Cost {
    private ArrayList<Resource> resources;
    private MilitaryCost military;


    public Cost(ArrayList<Resource> resources, MilitaryCost military) {
        this.resources = resources;
        this.military = military;
    }
}
