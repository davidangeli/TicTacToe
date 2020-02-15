package tictactoe.games;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import lombok.EqualsAndHashCode;
import tictactoe.AI;
import tictactoe.Main;
import tictactoe.Player;
import java.util.Optional;

/**
 * Graphical interface for the TicTactoe Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons.
 */
public class TicTacToePlane extends GridPane {
    private final TicTacToe game;
    private final Label whosturn;
    private final TicTacToeButton[][] buttons;

    public TicTacToePlane(TicTacToe game, Label whosturn, int width){
        this.game = game;
        this.whosturn = whosturn;
        this.buttons = new TicTacToeButton[game.getSize()][game.getSize()];
        for (int i=0; i<game.getSize(); i++){
            for (int j=0; j<game.getSize(); j++){
                TicTacToeButton button = new TicTacToeButton(new TicTacToe.Step(i,j), width/game.getSize());
                buttons[i][j] = button;
                this.add(button, j, i, 1, 1);
            }
        }
    }

    /**
     * making a step on the game's board representation.
     * @param step An in-game Step object.
     */
    private void makePlaneStep(TicTacToe.Step step){
        String mark = game.getWhosTurn() == Player.COMPUTER ? "O" : "X";
        game.makeStep(step);
        buttons[step.i][step.j].setText(mark);
        game.getWinner().ifPresentOrElse(
                winner -> whosturn.setText(winner.toString()),
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
            super.setOnAction(click);
        }

        private EventHandler<ActionEvent> click = value -> {
            if (!this.getText().equals("") || game.getWinner().isPresent()) return;

            try {
                //Player' move
                makePlaneStep(step);
                if (game.getWinner().isPresent()) return;
                //Computer's move - no step option is not really possible in TicTacToe
                Optional<TicTacToe.Step> step2 = AI.getNextStep(game, Main.AIDEPTH);
                step2.ifPresent(s -> makePlaneStep(step));
            } catch (Exception e){
                e.printStackTrace();
            }
        };
    }


}
