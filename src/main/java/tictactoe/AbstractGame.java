package tictactoe;

import javafx.util.Pair;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;

/**
 * An abstract class of a two player board game.
 * @param <T> This type parameter represents an in-game step's type.
 */
public abstract class AbstractGame<T> implements Serializable {
    protected int score = 0;
    protected final LinkedList<Pair<Player, Optional<T>>> steps = new LinkedList<>();
    protected Player whosTurn;
    protected Player winner;

    public AbstractGame (Player starts) {
        this.whosTurn = starts;
    }

    /**
     * Returns the value of the current game state.
     * @return An integer value.
     */
    public final int getScore() {
        return score;
    }

    /**
     * Returns a collection of all the possible and viable next steps from this one. Based on implementation,
     * could be an already filtered list.
     * @return An LinkedList of Steps.
     */
    public abstract LinkedList<T> getNextSteps();

    /**
     * Returns a new game state from this one, making one in-game step.
     * @param step An in game step.
     * @return An LinkedList of game states.
     */
    protected abstract AbstractGame<T> getNextState(T step) throws IllegalArgumentException;

    /**
     * Returns a collection of all the possible (~viable) next states from this one. Based on implementation,
     * could be an already filtered list.
     *
     * @return An LinkedList of game states.
     */
    public final LinkedList<AbstractGame<T>> getNextStates() {
        LinkedList<AbstractGame<T>> result = new LinkedList<>();
        for (T step : getNextSteps()){
            try {
                result.add(getNextState(step));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Returns the COMPUTER or the HUMAN player if the game has been won. Empty if noone won yet.
     * @return The winner Player.
     */
    public final Optional<Player> getWinner() {
        if (winner == null) return Optional.empty();
        return Optional.of(winner);
    }

    /**
     * Returns with the Player who should make the next step.
     * @return The Player who's turn it is.
     */
    public final Player getWhosTurn() {
        return whosTurn;
    }

    /**
     * Gets the list of all steps made in the game so far. Optional.empty means skipped move.
     * @return A Linked list of Step objects.
     */
    public final LinkedList<Pair<Player, Optional<T>>> getSteps() {
        return steps;
    }

    /**
     * This method implements a single step in the game, changing the current GameState. Optional.empty means skip.
     * Throws InvalidAttributesException if the step has invalid attributes
     * (eg. negative values or out of bound values, etc).
     * @param step The current move.
     */
    public abstract void makeStep(T step, boolean updateScore) throws IllegalArgumentException;

    /**
     * This method should be called when a Player can not move, and so passes.
     */
    public void skipStep() {
        steps.add(new Pair<>(whosTurn, Optional.empty()));
        whosTurn = whosTurn.next();

    }
}
