package tictactoe.ai;

import tictactoe.Opponent;
import tictactoe.AbstractGame;
import tictactoe.Player;
import java.util.Comparator;
import java.util.Optional;

/**
 * Class implementing the AI interface with the classic miniMax solution.
 */
public class MiniMaxAI implements Opponent {
    private final int depth;

    public MiniMaxAI(int depth) {
        this.depth = depth;
    }

    /**
     * Selects next step for the computer based on a minimax algorithm. If selection fails, throws Exception.
     * @param state The actual game state on which the computer gets the next step.
     * @return Selected step. Empty if can not move.
     */
    public <T> Optional<T> getNextStep (AbstractGame<T> state) {

        Optional<AbstractGame<T>> nextstate = state.getNextStates().parallelStream()
                                                    .max(Comparator.comparing(st -> minimaxStep(st, depth)));

        if (nextstate.isEmpty()) return Optional.empty();
        return nextstate.get().getSteps().getLast().getValue();
    }

    /**
     * Classic minimax algorithm on a tictactoe.State and then next possible steps.
     * @param state The starting state.
     * @param rDepth The algorithm goes this deep on the game's state tree, in recursive terms.
     * @return A corrected value of the state (and the last step).
     */
    private <T> int minimaxStep(AbstractGame<T> state, int rDepth){

        if (rDepth == 0) return state.getScore();

        int minmax = (state.getWhosTurn() == Player.OPPONENT) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (AbstractGame<T> st : state.getNextStates()) {
            if (state.getWhosTurn() == Player.OPPONENT) {
                minmax = Math.max (minmax, minimaxStep(st,rDepth-1));
            }
            else {
                minmax = Math.min(minmax, minimaxStep(st,rDepth-1));
            }
        }
        return minmax;
    }
}
