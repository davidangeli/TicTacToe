import tictactoe.Player;
import tictactoe.Scoring;
import org.junit.jupiter.api.Test;
import tictactoe.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringTest {

    @Test
    public void testScoring(){
        State mygame = new State(6, Player.COMPUTER);
        mygame.makeStep(0,0);
        mygame.makeStep(2,2);
        mygame.makeStep(0,1);
        mygame.makeStep(3,1);
        mygame.makeStep(0,2);
        mygame.makeStep(3,2);
        mygame.makeStep(0,3);
        mygame.makeStep(3,3);
        mygame.makeStep(0,4);
        mygame.makeStep(4,4);
        mygame.makeStep(1,2);

        int score = 1*10000-4*1;

        assertEquals(score, Scoring.stateScore(mygame, 5));
    }
}
