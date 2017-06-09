package server.model.valuable;

import server.utility.UnicodeChars;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Resource extends Valuable implements Serializable {

    private ResourceType type;

    public Resource(ResourceType type, Integer amount) {
        super(amount);
        this.type = type;
    }


    public ResourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        String resource= "";

        if(type == ResourceType.Coins)
            resource += "Coins "+ UnicodeChars.Coins;

        if(type == ResourceType.Servants)
            resource += "Servants "+UnicodeChars.Servants;

        if(type == ResourceType.Stones)
            resource += "Stones "+UnicodeChars.Stones;

        if(type == ResourceType.Wood)
            resource += "Wood "+UnicodeChars.Wood;

        resource += " : ";
        
        resource += this.getAmount();

        return resource;
    }
}
