package tictactoe;

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
     * @throws Exception
     */
    public static int[] getNextStep (State state, int depth) throws Exception {
        int maxscore = Integer.MIN_VALUE;
        int[] step = new int[]{-1, -1};


        for (State st: state.getNextPossibleStates()) {
            int minmax = minimaxStep(st, depth);
            //System.out.println(minmax);
            if (minmax > maxscore) {
                step = st.getSteps().getLast();
                maxscore = minmax;
            }
        }

        if (maxscore == Integer.MIN_VALUE) throw new Exception("No steps selected.");
        return step;
    }

    /**
     * Classic minimax algorithm on a tictactoe.State and then next possible steps.
     * @param state The starting state.
     * @param depth The algorithm goes this deep on the game's state tree.
     * @return A corrected value of the state (and the last step).
     */
    private static int minimaxStep(State state, int depth){

        if (depth == 0) return state.getScore();

        int minmax = (state.getWhosTurn() == Player.COMPUTER) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (State st : state.getNextPossibleStates()) {
            if (state.getWhosTurn() == Player.COMPUTER) {
                Math.max (minmax, minimaxStep(st,depth-1));
            }
            else {
                Math.min(minmax, minimaxStep(st,depth-1));
            }
        }
        return minmax;
    }


}
