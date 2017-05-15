package netobject;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */
public class Message extends NetObject {

    public MessageType type;
    public String value;

    public Message(MessageType t, String v) {

        this.type = t;
        this.value = v;

    }

}


