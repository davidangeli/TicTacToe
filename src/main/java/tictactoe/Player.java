package tictactoe;

public enum Player {
    COMPUTER,
    HUMAN;

    private static Player[] vals = values();

    public Player next() {
        return vals[(this.ordinal()+1) % vals.length];
    }
}