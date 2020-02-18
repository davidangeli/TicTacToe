import org.junit.jupiter.api.Test;
import tictactoe.ai.MiniMaxAI;
import tictactoe.Player;
import tictactoe.games.TicTacToe;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AITest {
    private static final int AIDEPTH = 3;

    @Test
    public void testAI() {
        TicTacToe mygame = new TicTacToe(Player.HUMAN, new MiniMaxAI(2));
        int size = mygame.getSize();

        //1 imitate human step, then an ai step
        mygame.makeStep(new TicTacToe.Step(size/2 - 1,size/2 - 1), true);
        mygame.makeAIStep();

        //2-3-4-5
        for (int i = 0; i < 5; i++) {
            mygame.makeStep(mygame.getNextSteps().getLast(), true);
            mygame.makeAIStep();
        }

        assertEquals(mygame.getSteps().element().getValue().get(), new TicTacToe.Step(size/2 - 1,size/2 - 1));
        assertEquals(12, mygame.getSteps().size());
    }

}