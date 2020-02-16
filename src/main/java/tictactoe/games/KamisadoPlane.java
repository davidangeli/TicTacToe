package tictactoe.games;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import lombok.EqualsAndHashCode;
import tictactoe.AI;
import tictactoe.Main;
import tictactoe.Player;
import java.util.Optional;

/**
 * Graphical interface for the Kamisado Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons.
 */
public class KamisadoPlane extends GridPane {
    private final Kamisado game;
    private final Label whosturn;
    private final KamisadoButton[][] buttons = new KamisadoButton[8][8];
    private Kamisado.Tower activeTower = null;

    public KamisadoPlane(Kamisado game, Label whosturn, int width){
        this.game = game;
        this.whosturn = whosturn;
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++) {
                KamisadoButton button = new KamisadoButton(i, j, width / 8);
                button.setBackground(new Background(new BackgroundFill(Kamisado.colorMap[i][j], null, null)));
                button.drawTower();
                buttons[i][j] = button;
                this.add(button, j, i, 1, 1);
            }
        }
        enableButtons();
    }

    private void enableButtons(){
        for (KamisadoButton[] arr: buttons) {
            for (Button b : arr) {
                b.setDisable(true);
                b.setStyle("-fx-border-width: 1px;");
            }

        }
        //if (game.getWhosTurn() == Player.COMPUTER) {return;}

        //enable towers
        for (Kamisado.Tower tower : game.getPlayerTowers()[Player.HUMAN.ordinal()]) {
            buttons[tower.getI()][tower.getJ()].setDisable(false);
            buttons[tower.getI()][tower.getJ()].setStyle("-fx-border-width: 2px;");

        }

        //enable possible target fields
        if (activeTower != null) {
            for (Kamisado.Step step : game.getNextSteps(activeTower)) {
                buttons[step.toI][step.toJ].setDisable(false);
                buttons[step.toI][step.toJ].setStyle("-fx-border-width: 3px;");
            }
        }
    }

    /**
     * Making a step on the game's board representation.
     * @param step An in-game Step object.
     */
    private void makePlaneStep(Kamisado.Step step){

        game.makeStep(step);
        buttons[step.fromI][step.fromJ].drawTower();
        buttons[step.toI][step.toJ].drawTower();

        game.getWinner().ifPresentOrElse(
                winner -> whosturn.setText("WINNER: " + winner.toString()),
                () -> whosturn.setText(game.getWhosTurn().toString())
        );
    }


    /**
     * This subclass of javafx's Button contains a preindexed TicTacToeGame.Step.
     */
    @EqualsAndHashCode(callSuper = true)
    class KamisadoButton extends Button {
        public final int i, j;

        public  KamisadoButton(int i, int j, int width){
            super();
            this.i = i;
            this.j = j;
            this.setPrefSize(width,width);
            super.setOnAction(this::click);
        }

        private void click (ActionEvent actionEvent) {
            if (game.getWinner().isPresent()) return;

            //select a tower on this field
            if (game.getTable()[i][j] != null) {
                activeTower = game.getTable()[i][j];
            }
            // move a tower
            else {
                try {
                    //Player' move
                    makePlaneStep(new Kamisado.Step(activeTower.getI(), activeTower.getJ(), i, j));
                    activeTower = null;
                    //computer's move
                    if (game.getWinner().isPresent()) return;
                    Optional<Kamisado.Step> aiStep = AI.getNextStep(game, Main.AIDEPTH);
                    aiStep.ifPresentOrElse(
                            KamisadoPlane.this::makePlaneStep,
                            game::skipStep
                    );
                    whosturn.setText(game.getWhosTurn().toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            enableButtons();
        }

        public void drawTower(){
            if (game.getTable()[i][j] == null) {
                this.setText("");
                //this.setShape(null);
            }
            else {
                this.setText(Kamisado.colorMap[i][j].toString());
                //Circle circle = new Circle();
                //circle.setRadius(this.getWidth()/20);
                //circle.setFill(this.step.tower.getColor());
                //this.setShape(circle);
            }
        }
    }
}
