package server.model.valuable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Resource extends Valuable {
    private ResourceType type;

    public Resource(ResourceType type, Integer amount) {
        super(amount);
        this.type = type;
    }
}
