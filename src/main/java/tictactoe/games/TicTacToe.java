package tictactoe.games;

import lombok.Data;
import tictactoe.Game;
import tictactoe.Player;
import java.util.LinkedList;
import java.util.Optional;

/**
 * TicTacToeState class implements the tictactoe game.
 */
@Data
public class TicTacToe implements Game<TicTacToe.Step> {

    //getNextStates will only consider fields with a proximity to already marked fields
    private final static int VIABILITYDISTANCE = 1;

    private final int size, wins;
    private final int[][] table;
    private int[][] series;
    private int score = 0;
    private LinkedList<TicTacToe.Step> steps = new LinkedList<>();
    private Player whosTurn;
    private Player winner;

    public TicTacToe(int size, int wins, Player starts) {
        this.size = size;
        this.wins = wins;
        whosTurn = starts;
        table = new int[size][size];
    }

    /**
     * Constructor from an existing TicTacToeState with making an in-game step. Throws InvalidAttributeException
     * if the step can not be made.
     * @param other The original game TicTacToeState.
     * @param step The steps being made, row and column indexes in an array.
     */
    public TicTacToe(TicTacToe other, TicTacToe.Step step) throws IllegalArgumentException {
        if (other.table[step.i][step.j] != 0) throw new IllegalArgumentException("Field is not null.");

        this.size = other.size;
        this.wins = other.wins;
        whosTurn = other.whosTurn;
        table = new int[size][size];
        for (int i=0; i < size; i++){
            table[i] = other.table[i].clone();
        }

        steps.addAll(other.steps);
        makeStep(step);
    }

    @Override
    public void makeStep(TicTacToe.Step step) throws IllegalArgumentException {
        if (step.i<0 || step.j<0 || step.i>=size || step.j>=size || table[step.i][step.j] != 0) {
            throw new IllegalArgumentException("Illegal step indexes");
        }

        table[step.i][step.j] = whosTurn.ordinal()+1;
        steps.add(step);
        whosTurn = whosTurn.next();
        calculateScore();
    }

    /**
     * In tictactoe, a step looks ok if there are other not 0 fields nearby already. Nearby: VIABILITYDISTANCE.
     * @param step An in game step.
     * @return True if the step in parameter is a viable option.
     */
    public boolean isAStepViable(TicTacToe.Step step) {
        boolean viable = false;
        int minrow = Math.max(0,step.i-VIABILITYDISTANCE);
        int maxrow = Math.min(size,step.i+VIABILITYDISTANCE+1);
        int mincol = Math.max(0,step.j-VIABILITYDISTANCE);
        int maxcol = Math.min(size,step.j+VIABILITYDISTANCE+1);

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
    public LinkedList<TicTacToe.Step> getNextSteps(){
        LinkedList<TicTacToe.Step> result = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 0){
                    Step step = new Step(i,j);
                    if (isAStepViable(step)) result.add(step);
                }
            }
        }
        return result;
    }

    @Override
    public LinkedList<Game<Step>> getNextStates() {
        LinkedList<Game<Step>> result = new LinkedList<>();
        for (Step step : getNextSteps()){
            try {
                result.add(new TicTacToe(this, step));
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
            return (this.i == ((Step)other).i && this.j == ((Step)other).j);
        }
    }

    /**
     * Calculates score for a given state.
     */
    private void calculateScore (){
        //int[][] series = countSeries();
        series = countSeries();

        //checks 3 steps back from the winning length, and weights with 10000, 100, 1
        score = 0;
        for (int i = 0; i < 3; i++) {
            score += series[Player.COMPUTER.ordinal()+1][wins-i] * (int)Math.pow(10,4-2*i);
            score -= series[Player.HUMAN.ordinal()+1][wins-i] * (int)Math.pow(10,4-2*i);
        }

        //update winner field if won
        if (series[Player.COMPUTER.ordinal()+1][wins] != 0 && winner == null) setWinner(Player.COMPUTER);
        if (series[Player.HUMAN.ordinal()+1][wins] != 0 && winner == null)    setWinner(Player.HUMAN);
    }

    /**
     * Counts different length series of marks in the table for both players. Open series counts 2.
     * @return Two dimensional array, first shows the player 1 and 2, second the length of series.
     */
    private int[][] countSeries () {
        //for simplicity, we grind through series of 0-s also.
        int[][] results = new int[3][size+1];

        //check rows and columns, starting from index in inner loops 1
        for (int i = 0; i < size; i++){
            int countr = 1, countc = 1;
            int beforer = table[i][0], beforec = table[0][i];
            for (int j = 1; j < size; j++){

                //end of a series in rows
                if (table[i][j] != table[i][j-1]) {
                    results[table[i][j-1]][countr] += seriesValue(beforer, table[i][j-1], table[i][j], countr);
                    countr = 0;
                    beforer = table[i][j-1];
                }
                countr++;

                //end of a series in columns
                if (table[j][i] != table[j - 1][i]) {
                    results[table[j - 1][i]][countc] += seriesValue(beforec, table[j - 1][i], table[j][i], countc);
                    countc = 0;
                    beforec = table[j - 1][i];
                }
                countc++;
            }
            //end of a row and column
            results[table[i][size-1]][countr] += seriesValue(beforer, table[i][size-1], -1, countr);
            results[table[size-1][i]][countc] += seriesValue(beforec, table[size-1][i], -1, countc);
        }

        //check diagonally, starting from index 0 in both loops
        for (int n = 1-size; n < size; n++) {
            int count = 0, count2 = 0;
            int before = -1, before2 = -1;
            for (int i = 0; i < size; i++) {
                int j = i-n;
                int j2 = size-j-1;
                //j==size option means out of bound index, but enables checking on series ending at boundaries
                if ((j >= 0) && (j <= size)){

                    //end of diagonals
                    if (j==size){
                        results[table[i-1][j-1]][count] += seriesValue(before, table[i-1][j-1], -1, count);
                        results[table[i-1][j2+1]][count2] += seriesValue(before2, table[i-1][j2+1], -1, count2);
                    }
                    //start of a diagonal
                    else if (before == -1) {
                        before = table[i][j];
                        before2 = table[i][j2];
                    }
                    else {
                        //end of a series in d1
                        if (table[i][j] != table[i-1][j-1]) {
                            results[table[i-1][j-1]][count] += seriesValue(before, table[i-1][j-1], table[i][j], count);
                            count = 0;
                            before = table[i-1][j-1];
                        }
                        //end of a series in d2
                        if (table[i][j2] != table[i-1][j2+1]) {
                            results[table[i-1][j2+1]][count2] += seriesValue(before2, table[i-1][j2+1], table[i][j2], count2);
                            count2 = 0;
                            before2 = table[i-1][j2+1];
                        }
                    }
                    count++;
                    count2++;
                }
            }

        }
        return results;
    }

    /**
     * Gives a value to a series of marks based on length and openness, making a difference between fully open and partly closed ones.
     * @param before Value of field before the sart of this series.
     * @param current Value of fields in this series.
     * @return integer value of 0, 1 or 2.
     */
    private int seriesValue (int before, int ending, int current, int count) {
        if (count < 2) return 0;
        //not null series, open on both side : counts double
        if (ending != 0 && before == 0 && current == 0) return 2;
        //not null series, open only on one side or winner length
        if (ending != 0 && (before == 0 || current == 0 || count >= wins)) return 1;
        //other closed series do not count for now
        return 0;
    }


}