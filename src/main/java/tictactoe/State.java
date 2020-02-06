package tictactoe;

import lombok.Data;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

@Data
public class State {

    private final static int WINS = 5;
    private final static int CUTDISTANCE = 1;
    private final int[][] table;
    private final int size;
    private Player whosTurn;
    private int score = 0;
    private LinkedList<int[]> steps = new LinkedList<>();
    private Optional<Player> winner = Optional.empty();

    public State (int size, Player starts) {
        this.size = size;
        whosTurn = starts;
        table = new int[size][size];
    }

    /**
     * Constructor from an existing tictactoe.State with making an in-game step.
     * @param other The original game tictactoe.State.
     * @param row The row index of the steps being made.
     * @param col The column index of the steps being made.
     */
    public State (State other, int row, int col) {
        size = other.size;
        whosTurn = other.whosTurn;
        table = new int[other.table.length][other.table.length];
        for (int i=0; i <other.table.length; i++){
            table[i] = other.table[i].clone();
        }
        steps = new LinkedList<>();
        for (int[] a : other.steps) {
            steps.add(a.clone());
        }
        makeStep(row, col);
    }

    /**
     * Plays a singe step, marking a field with one or two depending on the actual player.
     * @param row
     * @param col
     */
    public void makeStep(int row, int col) {
        assert(row>=0 && row<size && col>=0 && col<size) : "Wrong indexes.";
        assert(table[row][col] == 0) : "Field is not empty.";

        table[row][col] = whosTurn.ordinal()+1;
        steps.add(new int[]{row, col});
        whosTurn = whosTurn.next();
        score = Scoring.stateScore(this, WINS);
    }

    /**
     * Returns a collection of all the possible (~viable) next states from this one.
     * @return An ArrayList of States.
     */
    public ArrayList<State> getNextPossibleStates (){
        ArrayList<State> result = new ArrayList<>();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (table[i][j] == 0){

                    //cut options with no marked field in distance of CUTDISTANCE
                    int sum = 0;
                    for (int ii = Math.max(0,i-CUTDISTANCE); ii < Math.min(size,i+CUTDISTANCE+1); ii++){
                        for (int jj = Math.max(0,j-CUTDISTANCE); jj < Math.min(size,j+CUTDISTANCE+1); jj++){
                            sum += table[ii][jj] != 0 ? 1: 0;
                        }
                    }

                    if (sum >0) {
                        State st = new State(this, i, j);
                        result.add(st);
                    }
                }
            }
        }
        return result;
    }

}