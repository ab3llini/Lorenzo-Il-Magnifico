package netobject.notification;

import netobject.action.ActionType;
import server.model.board.Player;

public class MatchNotification extends Notification {

    private final MatchNotificationType matchNotificationType;
    private ActionType actionType;
    private Player player;
    private final String message;

    public MatchNotification(MatchNotificationType matchNotificationType, ActionType actionType, Player player, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.actionType = actionType;
        this.player = player;
        this.message = message;
    }

    public MatchNotification(MatchNotificationType matchNotificationType, Player player, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.player = player;
        this.message = message;
    }

    public MatchNotification(MatchNotificationType matchNotificationType, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.message = message;
    }

    public MatchNotificationType getMatchNotificationType() {
        return matchNotificationType;
    }

    public String getMessage() {
        return message;
    }

    public Player getPlayer() {
        return player;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
