package server.model.card.developement;

import server.model.valuable.Resource;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 08/06/2017.
 */
public class Discount implements Serializable {

    ArrayList<Resource> discount;

    public Discount(ArrayList<Resource> discount){

        this.discount = discount;

    }

    public void setDiscount(ArrayList<Resource> discount) {
        this.discount = discount;
    }

    public ArrayList<Resource> getDiscount() {
        return discount;
    }

    @Override
    public String toString() {

        String discountString = "";

        discountString += discount.toString();

        return discountString;

    }
}
