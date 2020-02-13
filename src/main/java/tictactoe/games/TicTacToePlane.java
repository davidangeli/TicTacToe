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

/**
 * Graphical interface for the TaicTactoe Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons.
 */
public class TicTacToePlane extends GridPane {
    final TicTacToe game;
    final Label whosturn;
    final TicTacToeButton[][] buttons;

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
                game.makeStep(step);
                this.setText("X");
                if (game.getWinner().isPresent()) {
                    whosturn.setText("WINNER: " + game.getWinner().get().toString());
                    return;
                }
                whosturn.setText("COMPUTER");
                //computer's move
                TicTacToe.Step step2 = AI.getNextStep(game, Main.AIDEPTH);
                game.makeStep(step2);
                buttons[step2.i][step2.j].setText("O");
                if (game.getWinner().isPresent()) {
                    whosturn.setText("WINNER: " + game.getWinner().get().toString());
                    return;
                }
                whosturn.setText("PLAYER");
            } catch (Exception e){
                e.printStackTrace();
            }
        };
    }


}
