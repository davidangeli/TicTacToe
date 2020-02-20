package tictactoe.games;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.EqualsAndHashCode;
import tictactoe.Opponent;
import tictactoe.Player;
import java.util.Optional;

/**
 * Graphical interface for the TicTactoe Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons.
 */
public class TicTacToePane extends GridPane {
    private final TicTacToe game;
    private final Opponent opponent;
    private final Label whosturn = new Label("");
    private final TicTacToeButton[][] buttons;

    public TicTacToePane(TicTacToe game, Opponent opponent, int width){
        setHgap(1);
        setVgap(1);
        this.game = game;
        this.opponent = opponent;
        this.whosturn.setText(game.getWhosTurn().toString());
        int size = game.getSize();
        this.add(whosturn, 0, 0, size, 1);
        this.buttons = new TicTacToeButton[size][size];
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                TicTacToeButton button = new TicTacToeButton(new TicTacToe.Step(i,j), width/(size+1));
                buttons[i][j] = button;
                this.add(button, j, i+1, 1, 1);
            }
        }
    }

    /**
     * Making a step on the game's board representation.
     * @param step An in-game Step object.
     */
    private void makeBoardStep(TicTacToe.Step step){
        game.makeStep(step, true);
        String mark = game.getWhosTurn() == Player.OPPONENT ? "X" : "O";
        buttons[step.i][step.j].setText(mark);
        game.getWinner().ifPresentOrElse(
                winner -> whosturn.setText("WINNER: " + winner.toString()),
                () -> whosturn.setText(game.getWhosTurn().toString())
        );
    }

    /**
     * This subclass of javafx's Button contains a preindexed TicTacToeGame.Step.
     */
    @EqualsAndHashCode(callSuper = true)
    class TicTacToeButton extends Button {
        TicTacToe.Step step;

        public  TicTacToeButton(TicTacToe.Step step, int width){
            super();
            this.step = step;
            this.setPrefSize(width,width);
            this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            super.setOnAction(click);
        }

        private EventHandler<ActionEvent> click = value -> {
            if (!this.getText().equals("") || game.getWinner().isPresent()) return;

            try {
                //Player' move
                makeBoardStep(step);
                if (game.getWinner().isPresent()) return;
                //Computer's move - no step option is not really possible in TicTacToe
                Optional<TicTacToe.Step> step2 = opponent.getNextStep(game);
                step2.ifPresent(TicTacToePane.this::makeBoardStep);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        };
    }

}
