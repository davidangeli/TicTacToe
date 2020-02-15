import org.junit.jupiter.api.Test;
import tictactoe.AI;
import tictactoe.Player;
import tictactoe.games.TicTacToe;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AITest {
    private static final int AIDEPTH = 3;

    @Test
    public void testAI() {
        TicTacToe mygame = new TicTacToe(10,5,Player.HUMAN);
        int size = mygame.getTable().length;

        //1 imitate human step, then an ai step
        mygame.makeStep(Optional.of(new TicTacToe.Step(size/2,size/2)));
        mygame.makeStep(AI.getNextStep(mygame, AIDEPTH));

        //2-3-4-5
        for (int i = 0; i < 5; i++) {
            mygame.makeStep(Optional.of(mygame.getNextSteps().getLast()));
            mygame.makeStep(AI.getNextStep(mygame, AIDEPTH));
        }

        assertEquals(mygame.getSteps().element().get(), new TicTacToe.Step(size/2,size/2));
        assertEquals(mygame.getSteps().size(), 12);
    }

}