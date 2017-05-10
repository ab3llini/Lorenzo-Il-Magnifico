package Server.Model.valuable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Resource extends Valuable {
    private ResourceType type;

    public Resource(Integer amount, ResourceType type) {
        super(amount);
        this.type = type;
    }
}
