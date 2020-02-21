package tictactoe;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public abstract class AbstractGamePane<G extends AbstractGame, T> extends GridPane {
    protected final Label whosturn = new Label("");
    protected final Opponent opponent;
    protected final G game;

    public AbstractGamePane(G game, Opponent opponent){
        this.opponent = opponent;
        this.game = game;
        this.whosturn.setText(game.getWhosTurn().toString());
    }

    /**
     * Calls the game's step action and updates information on the board according to the step made.
     * @param step An in-game Step object.
     */
    protected abstract void makeBoardStep(T step);

    /**
     * Calls the game's skip action and updates information on the board after that.
     */
    protected abstract void skipBoardStep();


    /**
     * Kicks out the game with waiting for the opponent to start.
     */
    public void opponentsMove() {
        try {
            Optional<T> step2;
            step2 = (Optional<T>)opponent.getNextStep(game);
            step2.ifPresentOrElse(this::makeBoardStep, this::skipBoardStep);
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Controls which fields & controls are enabled for the player, according to the game's actual standing.
     */
    protected abstract void enableButtons();

}
