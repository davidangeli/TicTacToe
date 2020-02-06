import org.junit.jupiter.api.Test;
import tictactoe.AI;
import tictactoe.Player;
import tictactoe.State;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AITest {
    private static final int SIZE = 10;
    private static final int AIDEPTH = 3;

    @Test
    public void testAI() throws Exception {
        State mygame = new State(SIZE, Player.HUMAN);
        int [] step;

        //1
        mygame.makeStep(SIZE/2,SIZE/2);
        step = AI.getNextStep(mygame, AIDEPTH);
        mygame.makeStep(step[0],step[1]);

        //2-3-4-5
        for (int i = 0; i < 5; i++) {
            //imitate human step
            step = mygame.getNextPossibleStates().get(0).getSteps().getLast();
            mygame.makeStep(step[0], step[1]);
            // make ai step
            step = AI.getNextStep(mygame, AIDEPTH);
            mygame.makeStep(step[0], step[1]);
        }

        assertArrayEquals(mygame.getSteps().element(), new int[]{SIZE/2,SIZE/2});
        assertEquals(mygame.getSteps().size(), 12);
    }

}