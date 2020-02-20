import tictactoe.Opponent;
import tictactoe.Player;
import org.junit.jupiter.api.Test;
import tictactoe.ai.MiniMaxAI;
import tictactoe.games.TicTacToe;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
//TODO: implement again reasonably

public class ScoringTest {


    @Test
    public void testScoring(){
        TicTacToe game = new TicTacToe(Player.PLAYER);
        Opponent opponent = new MiniMaxAI(2);
        game.makeStep(new TicTacToe.Step(0,0), true);
        Optional<TicTacToe.Step> aistep = opponent.getNextStep(game);
        aistep.ifPresent(st -> game.makeStep(st, true));

        assertEquals(0, game.getScore());
        assertEquals(2, game.getSteps().size());
    }
}
