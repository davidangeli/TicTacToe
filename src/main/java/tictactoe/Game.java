package tictactoe;

import java.util.LinkedList;
import java.util.Optional;

//TODO: rethink usage of optional in parameters.

/**
 * Describes requirements for a 2 player game stategraph node.
 * @param <T> Type of an in-game step's representation.
 */
public interface Game<T> {

    /**
     * Returns the value of the current game state.
     * @return An integer value.
     */
    int getScore();

    /**
     * Returns a collection of all the possible and viable next steps from this one. Based on implementation,
     * could be an already filtered list.
     * @return An LinkedList of Steps.
     */
    LinkedList<T> getNextSteps();

    /**
     * Returns a collection of all the possible (~viable) next states from this one. Based on implementation,
     * could be an already filtered list.
     * @return An LinkedList of States.
     */
    LinkedList<Game<T>> getNextStates();

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
     * Gets the list of all steps made in the game so far. Optional.empty means skipped move.
     * @return A Linked list of Step objects.
     */
    LinkedList<Optional<T>> getSteps();

    /**
     * This method implements a single step in the game, changing the current GameState. Optional.empty means skip.
     * Throws InvalidAttributesException if the step has invalid attributes
     * (eg. negative values or out of bound values, etc).
     * @param step The current move.
     */
    void makeStep(Optional<T> step) throws IllegalArgumentException;
}
