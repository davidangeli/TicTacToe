package tictactoe;

import tictactoe.games.GameState;

import java.util.LinkedList;
/**
 * Static collection of functions normally attributed to an "tictactoe.AI".
 */
public abstract class AI {

    private AI (){
    }

    /**
     * Selects next step for the computer based on a minimax algorithm. If selection fails, throws Exception.
     * @param state The actual game state on which the computer gets the next step.
     * @return Selected step.
     * @throws Exception thrown when a step could not be selected.
     */
    public static <T> T getNextStep (GameState<T> state, int depth) throws Exception {
        int maxscore = Integer.MIN_VALUE;
        T step = null;

        LinkedList<GameState<T>> stl = state.getNextStates();
        for (GameState<T> st: stl) {
            int minmax = minimaxStep(st, depth);
            //System.out.println(minmax);
            if (minmax > maxscore) {
                step = st.getSteps().getLast();
                maxscore = minmax;
            }
        }

        if (step == null) throw new Exception ("No steps selected.");
        return step;
    }

    /**
     * Classic minimax algorithm on a tictactoe.State and then next possible steps.
     * @param state The starting state.
     * @param depth The algorithm goes this deep on the game's state tree.
     * @return A corrected value of the state (and the last step).
     */
    private static <T> int minimaxStep(GameState<T> state, int depth){

        if (depth == 0) return state.getScore();

        int minmax = (state.getWhosTurn() == Player.COMPUTER) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (GameState<T> st : state.getNextStates()) {
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
