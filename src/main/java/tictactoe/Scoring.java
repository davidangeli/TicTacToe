package tictactoe;

import java.util.Optional;

/**
 * Static collection of functions that evaluate a state of game.
 */
public abstract class Scoring {

    private Scoring(){
    }

    /**
     * Calculates score for a given state.
     * @param state
     * @return
     */
    public static int stateScore (State state, int wins){
        assert (wins > 2) : "Winning series length LT 2.";
        assert (wins < state.getSize()) : "Winning size not LT table size.";

        //int[][] range = getRange(state, wins-1);
        int[][] series = countSeries(state.getTable(), wins);

        //checks 3 steps back from the winning length, and weights with 10000, 100, 1
        int score = 0;
        for (int i = 0; i < 3; i++) {
            score += series[Player.COMPUTER.ordinal()+1][wins-i] * (int)Math.pow(10,4-2*i);
            score -= series[Player.HUMAN.ordinal()+1][wins-i] * (int)Math.pow(10,4-2*i);
        }

        if (series[Player.COMPUTER.ordinal()+1][wins] != 0) state.setWinner(Optional.of(Player.COMPUTER));
        if (series[Player.HUMAN.ordinal()+1][wins] != 0) state.setWinner(Optional.of(Player.HUMAN));

        return score;
    }

    /**
     * Counts different length series of marks in a range for both players.
     * @param range
     * @return Two dimensional array, first shows the player 1 and 2, second the length of series.
     */
    private static int[][] countSeries (int[][] range, int wins) {
        //for simplicity, we grind through series of 0-s also.
        int[][] results = new int[3][range.length+1];

        //check rows and columns, starting from index in inner loops 1
        for (int i = 0; i < range.length; i++){
            int countr = 1, countc = 1;
            int beforer = range[i][0], beforec = range[0][i];
            for (int j = 1; j < range.length; j++){

                //end of a series in rows or end of row
                if (range[i][j] != range[i][j-1]) {
                    results[range[i][j-1]][countr] += seriesValue(beforer, range[i][j-1], range[i][j], countr, wins);
                    countr = 0;
                    beforer = range[i][j-1];
                }
                countr++;

                //end of a series in columns
                if (range[j][i] != range[j - 1][i]) {
                    results[range[j - 1][i]][countc] += seriesValue(beforec, range[j - 1][i], range[j][i], countc, wins);
                    countc = 0;
                    beforec = range[j - 1][i];
                }
                countc++;

                //end of a row and column
                if (j == (range.length - 1)) {
                    results[range[i][j]][countr] += seriesValue(beforer, range[i][j], -1, countr, wins);
                    results[range[i][j]][countc] += seriesValue(beforec, range[i][j], -1, countc, wins);
                }
            }
        }

        //check diagonally, starting from index 0 in both loops
        for (int n = -range.length; n <= range.length; n++) {
            int count = 0, count2 = 0;
            int before = -1, before2 = -1;
            for (int i = 0; i < range.length; i++){
                int j = i-n;
                int j2 = range.length-j-1;
                if ((j >= 0) && (j < range.length)){

                    //start of a diagonal
                    if (before == -1) {
                        before = range[i][j];
                        before2 = range[i][j2];
                    }
                    else {
                        //end of a series in d1
                        if (range[i][j] != range[i-1][j-1]) {
                            //System.out.println("d1 result: " + i + "," + j + "(" + count + "):" + seriesValue(before, range[i-1][j-1], range[i][j]));
                            results[range[i-1][j-1]][count] += seriesValue(before, range[i-1][j-1], range[i][j], count, wins);
                            count = 0;
                            before = range[i-1][j-1];
                        }
                        //end of a series in d2
                        if (range[i][j2] != range[i-1][j2+1]) {
                            //System.out.println("d2 result: " + i + "," + j2 + "(" + count2 + "):" + seriesValue(before2, range[i-1][j2+1], range[i][j2]));
                            results[range[i-1][j2+1]][count2] += seriesValue(before2, range[i-1][j2+1], range[i][j2], count2, wins);
                            count2 = 0;
                            before2 = range[i-1][j2+1];
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
     * Gives a value to a series of marks based on length and openness.
     * @param before
     * @param current
     * @return
     */
    private static int seriesValue (int before, int ending, int current, int count, int wins) {
        if (count < 2) return 0;
        //not null series, open on both side : counts double
        if (ending != 0 && before == 0 && current == 0) return 2;
        //not null series, open only on one side or winner length
        if (ending != 0 && (before == 0 || current == 0 || count >= wins)) return 1;
        //other closed series do not count for now
        return 0;
    }


}

