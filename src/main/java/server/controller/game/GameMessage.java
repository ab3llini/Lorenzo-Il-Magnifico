package server.controller.game;

/*
 * @author  ab3llini
 * @since   30/05/17.
 */
public enum GameMessage {

    TimeoutExpired("Timeout for the move expired."),
    MoveEnabled("It is your turn to make the move."),
    MoveDisabled("It is not your turn anymore."),
    InvalidAction("You can't perform that move.");

    private final String literal;

    GameMessage(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
