package tictactoe;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * The AI interface declares the methods we except from an AI implementation.
 */
public interface Opponent {

    /**
     * On a given two-player table game, and its current state, this method selects the next step -of the computer, usually.
     * @param state The actual game(state).
     * @param <T> The type parameter defining an in-game step.
     * @return Optional<T>. I it's empty, that means the method was not able to chose a step.
     */
    <T> Optional<T> getNextStep(AbstractGame<T> state) throws IOException, TimeoutException, ClassNotFoundException;

    void discard();

}
