package netobject.notification;

import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;

public class MatchNotification extends Notification {

    private final MatchNotificationType matchNotificationType;
    private ImmediateActionType actionType;
    private Player player;
    private Action action;
    private final String message;
    private Deck<LeaderCard> deck;
    private ArrayList<BonusTile> tiles;

    public MatchNotification(MatchNotificationType matchNotificationType, ImmediateActionType actionType, Player player, String message) {

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

    public MatchNotification(MatchNotificationType matchNotificationType, Deck<LeaderCard> deck, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.deck = deck;
        this.message = message;
    }

    public MatchNotification(MatchNotificationType matchNotificationType, ArrayList<BonusTile> tiles, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.tiles = tiles;
        this.message = message;
    }

    public MatchNotification(MatchNotificationType matchNotificationType, Player player, Action action, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.player = player;
        this.action = action;
        this.message = message;
    }

    public MatchNotification(MatchNotificationType matchNotificationType, Action action, String message) {
        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.action = action;
        this.message = message;    }


    public MatchNotificationType getMatchNotificationType() {
        return matchNotificationType;
    }

    public String getMessage() {
        return message;
    }

    public Player getPlayer() {
        return player;
    }

    public ImmediateActionType getActionType() {
        return actionType;
    }

    public Action getAction() {
        return action;
    }

    public Deck<LeaderCard> getDeck() {
        return deck;
    }

    public ArrayList<BonusTile> getTiles() {
        return tiles;
    }
}
