package tictactoe.games;

import javafx.util.Pair;
import tictactoe.AI;
import tictactoe.AbstractGame;
import tictactoe.Player;
import java.util.LinkedList;
import java.util.Optional;

/**
 * TicTacToe class implements the tictactoe game.
 */
public class TicTacToe extends AbstractGame<TicTacToe.Step> {
    //tictactoe specifics
    private final static int SIZE = 10, WINS = 5, VIABILITYDISTANCE = 1;
    private final int[][] table;

    public TicTacToe(Player starts, AI ai) {
        super(starts, ai);
        table = new int[SIZE][SIZE];
    }

    @Override
    public LinkedList<TicTacToe.Step> getNextSteps() {
        LinkedList<TicTacToe.Step> result = new LinkedList<>();
        if (winner != null) return result;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (table[i][j] == 0){
                    TicTacToe.Step step = new TicTacToe.Step(i,j);
                    if (isAStepViable(step)) result.add(step);
                }
            }
        }
        return result;
    }

    @Override
    protected AbstractGame<Step> getNextState(Step step) throws IllegalArgumentException {

        Player pl = steps.isEmpty() ? whosTurn : steps.getFirst().getKey();
        TicTacToe nextState = new TicTacToe(pl, ai);

        //TicTacToe Steps have only final primitive members
        steps.forEach(s -> s.getValue().ifPresentOrElse(
                st -> nextState.makeStep(st, false),
                nextState::skipStep)
        );

        nextState.makeStep(step, true);
        return nextState;
    }

    @Override
    public void makeStep(TicTacToe.Step step, boolean updateScore) throws IllegalArgumentException {
        if (step.i<0 || step.j<0 || step.i>=SIZE || step.j>=SIZE || table[step.i][step.j] != 0) {
            throw new IllegalArgumentException("Illegal step indexes.");
        }

        table[step.i][step.j] = whosTurn.ordinal()+1;
        steps.add(new Pair<>(whosTurn, Optional.of(step)));
        whosTurn = whosTurn.next();
        if (updateScore) calculateScore();
    }

    /**
     * In tictactoe, a step looks ok if there are other not 0 fields nearby already. Nearby: VIABILITYDISTANCE.
     * @param step An in game step.
     * @return True if the step in parameter is a viable option.
     */
    public boolean isAStepViable(TicTacToe.Step step) {
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

    public int getSize(){
        return SIZE;
    }

    /**
     * This nested class represents an in game step, with two integers meaning row and column.
     */
    public static class Step {
        public final int i, j;
        public Step (int i, int j){
            this.i=i;
            this.j=j;
        }

        @Override
        public boolean equals(Object other) {
            if (this.getClass() != other.getClass()) return false;
            return (this.i == ((TicTacToe.Step)other).i && this.j == ((TicTacToe.Step)other).j);
        }
    }

    /**
     * Calculates and sets score for a given state.
     */
    private void calculateScore (){
        int[][] series = countSeries();

        //checks 3 steps back from the winning length, and weights with 10000, 100, 1
        score = 0;
        for (int i = 0; i < 3; i++) {
            score += series[Player.COMPUTER.ordinal()+1][WINS-i] * (int)Math.pow(10,4-2*i);
            score -= series[Player.HUMAN.ordinal()+1][WINS-i] * (int)Math.pow(10,4-2*i);
        }

        //update winner field if won
        if (series[Player.COMPUTER.ordinal()+1][WINS] != 0 && winner == null) winner = Player.COMPUTER;
        if (series[Player.HUMAN.ordinal()+1][WINS] != 0 && winner == null) winner = Player.HUMAN;
    }

    /**
     * Counts different length series of marks in the table for both players. Open series counts 2.
     * @return Two dimensional array, first shows the player 1 and 2, second the length of series.
     */
    private int[][] countSeries () {
        //for simplicity, we grind through series of 0-s also.
        int[][] results = new int[3][SIZE+1];

        for (int n = 1-SIZE; n < SIZE; n++) {
            int countd1 = 0, countd2 = 0, countr = 1, countc = 1;
            int befored1 = -1, befored2 = -1, beforer = -1, beforec = -1;
            for (int i = 0; i <= SIZE; i++) {
                int j = i-n;
                int j2 = SIZE-j-1;

                //rows and columns checking from n=1
                if (n > 0 && i > 0 && i < SIZE){
                    //end of a series in rows
                    if (table[n][i] != table[n][i-1]) {
                        results[table[n][i-1]][countr] += seriesValue(beforer, table[n][i-1], table[n][i], countr);
                        countr = 0;
                        beforer = table[n][i-1];
                    }
                    //end of a series in columns
                    if (table[i][n] != table[i-1][n]) {
                        results[table[i-1][n]][countc] += seriesValue(beforec, table[i-1][n], table[i][n], countc);
                        countc = 0;
                        beforec = table[i-1][n];
                    }
                    countr++;
                    countc++;
                }
                //end of rows and columns
                else if (n>0) {
                    results[table[n][SIZE-1]][countr] += seriesValue(beforer, table[n][SIZE-1], -1, countr);
                    results[table[SIZE-1][n]][countc] += seriesValue(beforec, table[SIZE-1][n], -1, countc);
                }

                // diagonals check, valid indexes
                if ((j >= 0) && (j <= SIZE)){

                    // j==SIZE or i==SIZE option means out of bound index, but enables checking on series ending at boundaries
                    if (i== SIZE || j==SIZE){
                        results[table[i-1][j-1]][countd1] += seriesValue(befored1, table[i-1][j-1], -1, countd1);
                        results[table[i-1][j2+1]][countd2] += seriesValue(befored2, table[i-1][j2+1], -1, countd2);
                    }
                    //start of a diagonal
                    else if (befored1 == -1) {
                        befored1 = table[i][j];
                        befored2 = table[i][j2];
                    }
                    else {
                        //end of a series in d1
                        if (table[i][j] != table[i-1][j-1]) {
                            results[table[i-1][j-1]][countd1] += seriesValue(befored1, table[i-1][j-1], table[i][j], countd1);
                            countd1 = 0;
                            befored1 = table[i-1][j-1];
                        }
                        //end of a series in d2
                        if (table[i][j2] != table[i-1][j2+1]) {
                            results[table[i-1][j2+1]][countd2] += seriesValue(befored2, table[i-1][j2+1], table[i][j2], countd2);
                            countd2 = 0;
                            befored2 = table[i-1][j2+1];
                        }
                    }
                    countd1++;
                    countd2++;
                }
            }

        }
        return results;
    }

    /**
     * Gives a value to a series of marks based on length and openness, making a difference between fully open and partly closed ones.
     * @param before Value of field before the start of this series.
     * @param current Value of fields in this series.
     * @return integer value of 0, 1 or 2.
     */
    private int seriesValue (int before, int ending, int current, int count) {
        if (count < 2) return 0;
        //not null series, open on both side : counts double
        if (ending != 0 && before == 0 && current == 0) return 2;
        //not null series, open only on one side or winner length
        if (ending != 0 && (before == 0 || current == 0 || count >= WINS)) return 1;
        //other closed series do not count for now
        return 0;
    }
}
