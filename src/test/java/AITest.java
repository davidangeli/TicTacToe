import org.junit.jupiter.api.Test;
import tictactoe.Opponent;
import tictactoe.opponent.MiniMaxAI;
import tictactoe.Player;
import tictactoe.games.TicTacToe;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
//TODO: implement again reasonably

public class AITest {

    @Test
    public void testAI() {
        TicTacToe game = new TicTacToe(Player.PLAYER);
        Opponent opponent = new MiniMaxAI(2);
        int size = game.getSize();

        //1 imitate human step, then an ai step
        game.makeStep(new TicTacToe.Step(size/2 - 1,size/2 - 1), true);
        try {
            Optional<TicTacToe.Step> aistep = opponent.getNextStep(game);
            aistep.ifPresent(st -> game.makeStep(st, true));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //2-3-4-5
        for (int i = 0; i < 5; i++) {
            game.makeStep(game.getNextSteps().getLast(), true);
            try {
                Optional<TicTacToe.Step> aistep = opponent.getNextStep(game);
                aistep.ifPresent(st -> game.makeStep(st, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assertEquals(game.getSteps().element().getValue().get(), new TicTacToe.Step(size/2 - 1,size/2 - 1));
        assertEquals(12, game.getSteps().size());
    }

}