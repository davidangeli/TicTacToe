import tictactoe.Player;
import org.junit.jupiter.api.Test;
import tictactoe.games.TicTacToe;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringTest {

    @Test
    public void testScoring(){
        TicTacToe mygame = new TicTacToe(Player.COMPUTER);
        mygame.makeStep(new TicTacToe.Step(0,0));
        mygame.makeStep(new TicTacToe.Step(2,2));
        mygame.makeStep(new TicTacToe.Step(0,1));
        mygame.makeStep(new TicTacToe.Step(3,1));
        mygame.makeStep(new TicTacToe.Step(0,2));
        mygame.makeStep(new TicTacToe.Step(3,2));
        mygame.makeStep(new TicTacToe.Step(0,3));
        mygame.makeStep(new TicTacToe.Step(3,3));
        mygame.makeStep(new TicTacToe.Step(0,4));
        mygame.makeStep(new TicTacToe.Step(4,4));
        mygame.makeStep(new TicTacToe.Step(1,2));

        for (int[] arr : mygame.getTable()) {
            System.out.println(Arrays.toString(arr));
        }
        for (int[] arr : mygame.getSeries()) {
            System.out.println(Arrays.toString(arr));
        }

        int score = -4;
        assertEquals(score, mygame.getScore());
    }
}
