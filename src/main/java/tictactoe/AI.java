package tictactoe;

import java.util.Comparator;
import java.util.Optional;

/**
 * Static collection of functions normally attributed to an "AI".
 */
public abstract class AI {

    /**
     * Selects next step for the computer based on a minimax algorithm. If selection fails, throws Exception.
     * @param state The actual game state on which the computer gets the next step.
     * @return Selected step. Empty if can not move.
     */
    public static <T> Optional<T> getNextStep (Game<T> state, int depth) {
        /*int maxscore = Integer.MIN_VALUE;
        LinkedList<GameState<T>> stl = state.getNextStates();
        for (GameState<T> st: stl) {
            int minmax = minimaxStep(st, depth);
            if (minmax > maxscore) {
                step = st.getSteps().getLast();
                maxscore = minmax;
            }
        }*/
        Optional<Game<T>> nextstate = state.getNextStates().parallelStream()
                                                    .max(Comparator.comparing(st -> minimaxStep(st, depth)));

        if (nextstate.isEmpty()) return Optional.empty();
        return nextstate.get().getSteps().getLast().getValue();
    }

    /**
     * Classic minimax algorithm on a tictactoe.State and then next possible steps.
     * @param state The starting state.
     * @param depth The algorithm goes this deep on the game's state tree.
     * @return A corrected value of the state (and the last step).
     */
    private static <T> int minimaxStep(Game<T> state, int depth){

        if (depth == 0) return state.getScore();

        int minmax = (state.getWhosTurn() == Player.COMPUTER) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Game<T> st : state.getNextStates()) {
            if (state.getWhosTurn() == Player.COMPUTER) {
                minmax = Math.max (minmax, minimaxStep(st,depth-1));
            }
            else {
                minmax = Math.min(minmax, minimaxStep(st,depth-1));
            }
        }
        return minmax;
    }


}
