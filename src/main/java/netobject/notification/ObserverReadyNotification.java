package netobject.notification;

import client.controller.network.Observer;
import client.controller.network.ObserverType;

/*
 * @author  ab3llini
 * @since   29/06/17.
 */
public class ObserverReadyNotification extends Notification {

    private final ObserverType observerType;

    public ObserverReadyNotification(ObserverType observer) {

        super(NotificationType.Controller);

        this.observerType = observer;

    }

    public ObserverType getObserverType() {
        return observerType;
    }
}
