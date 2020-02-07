package tictactoe;

import java.util.LinkedList;
import java.util.Optional;
/**
 * Describes requirements for a 2 player game stategraph node.
 * @param <T> Type of an in-game step's representation.
 */
public interface GameState<T> {

    /**
     * Returns the value of the current game state.
     * @return An integer value.
     */
    int getScore();

    /**
     * A filter to use to thin out not viable next steps. E.g. a corner field on start in a TicTacToe would not be one.
     * @return True if the Step is a real option.
     */
    boolean isAStepViable(T step);

    /**
     * Returns a collection of all the possible (~viable) next steps from this one.
     * @return An LinkedList of Steps.
     */
    LinkedList<T> getNextSteps();

    /**
     * Returns a collection of all the possible (~viable) next states from this one.
     * @return An LinkedList of States.
     */
    LinkedList<GameState<T>> getNextStates();

    // use of Optional might be reviewed later

    /**
     * Returns the COMPUTER or the HUMAN player if the game has been won. Empty if noone won yet.
     * @return The winner Player.
     */
    Optional<Player> getWinner();

    /**
     * Returns with the Player who should make the next step.
     * @return The Player who's turn it is.
     */
    Player getWhosTurn();

    /**
     * Gets the list of all steps made in the game so far.
     * @return A Linked list of Step objects.
     */
    LinkedList<T> getSteps();

    /**
     * This method implements a singel step in the game, changing the current GameState. Throws InvalidAttributesException
     * if the step has invalid attributes (eg. negative values or out of bound values, etc).
     * @param step The current move.
     */
    void makeStep(T step) throws IllegalArgumentException;
}
