import tictactoe.Player;
import org.junit.jupiter.api.Test;
import tictactoe.ai.MiniMaxAI;
import tictactoe.games.TicTacToe;

import static org.junit.jupiter.api.Assertions.assertEquals;
//TODO: implement again

public class ScoringTest {

    @Test
    public void testScoring(){
        TicTacToe mygame = new TicTacToe(Player.COMPUTER, new MiniMaxAI(2));
        mygame.makeStep(new TicTacToe.Step(0,0), true);
        mygame.makeAIStep();

        System.out.println("Scoringtest score: " + mygame.getScore());
        assertEquals(2, mygame.getSteps().size());
    }
}
