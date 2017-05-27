package netobject.notification;

import netobject.NetObject;
import netobject.NetObjectType;

/*
 * @author  ab3llini
 * @since   26/05/17.
 */
public abstract class Notification extends NetObject {

    private final NotificationType notificationType;


    public Notification(NotificationType notificationType) {

        super(NetObjectType.Notification);

        this.notificationType = notificationType;

    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}
