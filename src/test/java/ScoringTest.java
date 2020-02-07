import tictactoe.Player;
import tictactoe.Scoring;
import org.junit.jupiter.api.Test;
import tictactoe.TicTacToeState;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringTest {

    @Test
    public void testScoring(){
        TicTacToeState mygame = new TicTacToeState(Player.COMPUTER);
        mygame.makeStep(new TicTacToeState.Step(0,0));
        mygame.makeStep(new TicTacToeState.Step(2,2));
        mygame.makeStep(new TicTacToeState.Step(0,1));
        mygame.makeStep(new TicTacToeState.Step(3,1));
        mygame.makeStep(new TicTacToeState.Step(0,2));
        mygame.makeStep(new TicTacToeState.Step(3,2));
        mygame.makeStep(new TicTacToeState.Step(0,3));
        mygame.makeStep(new TicTacToeState.Step(3,3));
        mygame.makeStep(new TicTacToeState.Step(0,4));
        mygame.makeStep(new TicTacToeState.Step(4,4));
        mygame.makeStep(new TicTacToeState.Step(1,2));

        int score = 1*10000-4*1;

        assertEquals(score, Scoring.stateScore(mygame, 5));
    }
}
