package tictactoe.games;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.EqualsAndHashCode;
import tictactoe.AI;
import tictactoe.Main;
import tictactoe.Player;

/**
 * Graphical interface for the Kamisado Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons.
 * TODO: draw towers on buttons, color everything, fill up buttons array
 */
public class KamisadoPlane {
    private final Kamisado game;
    private final Label whosturn;
    private final KamisadoButton[][] buttons = new KamisadoButton[8][8];
    private Kamisado.Tower activeTower = null;

    public KamisadoPlane(Kamisado game, Label whosturn, int width){
        this.game = game;
        this.whosturn = whosturn;
    }

    private void moveTower(Kamisado.Step step){
        int oldI = step.tower.getI();
        int oldJ = step.tower.getJ();
        buttons[oldI][oldJ].step = new Kamisado.Step(null, oldI, oldJ);
        buttons[step.i][step.j].step = step;
    }

    private void enableButtons(){
        for (KamisadoButton[] arr: buttons) {
            for (Button b : arr) {b.setDisable(true);}
        }
        if (game.getWhosTurn() == Player.COMPUTER) {return;}

        if (activeTower == null) {
            //enable towers
            for (Kamisado.Tower tower : game.getPlayerTowers()[Player.HUMAN.ordinal()]) {
                buttons[tower.getI()][tower.getJ()].setDisable(false);

            }
        } else {
            //enable possible target fields
            for (Kamisado.Step step : game.getNextSteps(activeTower)) {
                buttons[step.i][step.j].setDisable(false);
            }
        }
    }


    /**
     * This subclass of javafx's Button contains a preindexed TicTacToeGame.Step.
     */
    @EqualsAndHashCode(callSuper = true)
    class KamisadoButton extends Button {
        Kamisado.Step step;

        public  KamisadoButton(Kamisado.Step step, int width){
            super();
            this.step = step;
            this.setPrefSize(width,width);
            super.setOnAction(click);
        }

        private EventHandler<ActionEvent> click = value -> {
            if (!this.getText().equals("") || game.getWinner().isPresent()) return;

            //select a tower
            if (this.step.tower != null) {
                activeTower = this.step.tower;
                return;
            }
            // move a tower
            else {
                try {
                    //Player' move
                    Kamisado.Step newStep = new Kamisado.Step(activeTower, this.step.i, this.step.j);
                    moveTower(newStep);
                    game.makeStep(newStep);
                    if (game.getWinner().isPresent()) {
                        whosturn.setText("WINNER: " + game.getWinner().get().toString());
                        return;
                    }
                    whosturn.setText("COMPUTER");
                    //computer's move
                    Kamisado.Step aiStep = AI.getNextStep(game, Main.AIDEPTH);
                    moveTower(aiStep);
                    game.makeStep(aiStep);
                    if (game.getWinner().isPresent()) {
                        whosturn.setText("WINNER: " + game.getWinner().get().toString());
                        return;
                    }
                    whosturn.setText("PLAYER");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            enableButtons();
        };
    }
}
