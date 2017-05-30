package server.model.valuable;

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
}
