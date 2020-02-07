package tictactoe;

import lombok.Data;
import java.util.LinkedList;
import java.util.Optional;

/**
 * TicTacToeState class implements the tictactoe game, where an in game step represented by an int[].
 */
@Data
public class TicTacToeState implements GameState<TicTacToeState.Step> {

    private final static int SIZE = 10, WINS = 5;
    //getNextStates will only consider fields with a proximity to already marked fields
    private final static int VIABILITYDISTANCE = 1;
    private final int[][] table = new int[SIZE][SIZE];
    private int score = 0;
    private LinkedList<TicTacToeState.Step> steps = new LinkedList<>();
    private Player whosTurn;
    private Player winner;

    public TicTacToeState(Player starts) {
        whosTurn = starts;
    }

    /**
     * Constructor from an existing TicTacToeState with making an in-game step. Throws InvalidAttributeException
     * if the step can not be made.
     * @param other The original game TicTacToeState.
     * @param step The steps being made, row and column indexes in an array.
     */
    public TicTacToeState(TicTacToeState other, TicTacToeState.Step step) throws IllegalArgumentException {
        if (other.table[step.i][step.j] != 0) {
            throw new IllegalArgumentException("Field is not null.");
        }
        for (int i=0; i < SIZE; i++){
            table[i] = other.table[i].clone();
        }
        whosTurn = other.whosTurn;
        steps.addAll(other.steps);
        makeStep(step);
    }

    @Override
    public void makeStep(TicTacToeState.Step step) throws IllegalArgumentException {
        if (table[step.i][step.j] != 0) {
            throw new IllegalArgumentException ("Field is not null.");
        }
        table[step.i][step.j] = whosTurn.ordinal()+1;
        steps.add(step);
        whosTurn = whosTurn.next();
        score = Scoring.stateScore(this, WINS);
    }

    /**
     * {@inheritDoc}
     * In tictactoe, a step looks ok if there are other not 0 fields nearby already. Nearby: VIABILITYDISTANCE.
     * @param step An in game step.
     * @return True if the step in parameter is a viable option.
     */
    @Override
    public boolean isAStepViable(TicTacToeState.Step step) {
        boolean viable = false;
        int minrow = Math.max(0,step.i-VIABILITYDISTANCE);
        int maxrow = Math.min(SIZE,step.i+VIABILITYDISTANCE+1);
        int mincol = Math.max(0,step.j-VIABILITYDISTANCE);
        int maxcol = Math.min(SIZE,step.j+VIABILITYDISTANCE+1);

        for (int ii = minrow; ii < maxrow; ii++){
            int jj = mincol;
            while (jj < maxcol && table[ii][jj] == 0) {jj++;}
            if (jj < maxcol) {
                viable = true;
                break;
            }
        }
        return viable;
    }

    /**
     * {@inheritDoc}
     * In tictactoe, this list if filtered by viability.
     * @return
     */
    @Override
    public LinkedList<TicTacToeState.Step> getNextSteps(){
        LinkedList<TicTacToeState.Step> result = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (table[i][j] == 0){
                    Step step = new Step(i,j);
                    if (isAStepViable(step)) result.add(step);
                }
            }
        }
        return result;
    }

    @Override
    public LinkedList<GameState<TicTacToeState.Step>> getNextStates() {
        LinkedList<GameState<TicTacToeState.Step>> result = new LinkedList<>();
        for (Step step : getNextSteps()){
            try {
                result.add(new TicTacToeState(this, step));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Optional<Player> getWinner(){
        if (winner == null) return Optional.empty();
        return Optional.of(winner);
    }

    @Override
    public LinkedList<Step> getSteps(){
        return steps;
    }

    public static class Step {
        public final int i, j;
        public Step (int i, int j){
            if (i<0 || j<0 || i>=SIZE && j>=SIZE) {
                throw new IllegalArgumentException("Illegal step indexes");
            }
            this.i=i;
            this.j=j;
        }

        @Override
        public boolean equals(Object other) {
            if (this.getClass() != other.getClass()) return false;
            return (this.i == ((Step)other).i && this.j == ((Step)other).j);
        }
    }


}