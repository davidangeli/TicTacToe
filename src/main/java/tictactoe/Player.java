package tictactoe;

/**
 * Enum used for player identification.
 */
public enum Player {
    OPPONENT,
    PLAYER;

    private final static Player[] vals = values();

    public Player next() {
        return vals[(this.ordinal()+1) % vals.length];
    }
}