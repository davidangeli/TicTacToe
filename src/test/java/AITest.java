import org.junit.jupiter.api.Test;
import tictactoe.AI;
import tictactoe.Player;
import tictactoe.games.TicTacToe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AITest {
    private static final int AIDEPTH = 3;

    @Test
    public void testAI() throws Exception {
        TicTacToe mygame = new TicTacToe(10,5,Player.HUMAN);
        int size = mygame.getTable().length;

        //1 imitate human step, then an ai step
        mygame.makeStep(new TicTacToe.Step(size/2,size/2));
        mygame.makeStep(AI.getNextStep(mygame, AIDEPTH));

        //2-3-4-5
        for (int i = 0; i < 5; i++) {
            mygame.makeStep(mygame.getNextSteps().getLast());
            mygame.makeStep(AI.getNextStep(mygame, AIDEPTH));
        }

        assertEquals(mygame.getSteps().element(), new TicTacToe.Step(size/2,size/2));
        assertEquals(mygame.getSteps().size(), 12);
    }

}