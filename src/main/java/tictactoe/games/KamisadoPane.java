package tictactoe.games;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import tictactoe.AbstractGamePane;
import tictactoe.Opponent;
import tictactoe.Player;
import java.util.LinkedList;

/**
 * Graphical interface for the Kamisado Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons. Size is always 8x8.
 */
public class KamisadoPane extends AbstractGamePane<Kamisado, Kamisado.Step> {
    private final KamisadoButton[][] buttons = new KamisadoButton[8][8];
    private final Button skipButton;
    private Kamisado.Tower activeTower = null;

    public KamisadoPane(Kamisado game, Opponent opponent, int width){
        super(game, opponent);
        setHgap(2);
        setVgap(2);
        this.add(whosturn, 0, 0, 8, 1);
        //kamisado field buttons
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++) {
                KamisadoButton button = new KamisadoButton(i, j, width / 10);
                buttons[i][j] = button;
                this.add(button, j, i+1, 1, 1);
            }
        }

        //skipbutton
        skipButton = new Button("Skip");
        skipButton.setOnAction(e -> {
            skipBoardStep();
            opponentsMove();
        });
        this.add(skipButton, 7, 9, 1, 1);
        enableButtons();
    }

    @Override
    protected void enableButtons(){
        //disable all
        this.getChildren().forEach(c -> {
            if (c.getClass().equals(Label.class)) {return;}
            c.setDisable(true);
            c.setStyle("");
        });
        if (game.getWinner().isPresent() || game.getWhosTurn() == Player.OPPONENT) return;

        //enable towers if color matches last step, or first move
        for (Kamisado.Tower tower : game.getPlayerTowers()[Player.PLAYER.ordinal()]) {
            if (game.getSteps().isEmpty() || game.getSteps().getLast().getValue().isEmpty() || game.getSteps().getLast().getValue().get().getToFieldColor() == tower.color) {
                buttons[tower.getI()][tower.getJ()].setDisable(false);
                buttons[tower.getI()][tower.getJ()].setStyle("-fx-border-color: #f00000; -fx-border-width: 2px;");
            }
        }

        //enable possible target fields and skip button if there's no step
        if (activeTower != null) {
            LinkedList<Kamisado.Step> nextsteps = game.getNextSteps(activeTower);
            for (Kamisado.Step step : nextsteps) {
                buttons[step.toI][step.toJ].setDisable(false);
                buttons[step.toI][step.toJ].setStyle("-fx-border-color: #f00000; -fx-border-width: 2px;");
            }
            if (nextsteps.isEmpty()) {
                skipButton.setDisable(false);
            }
        }
    }

    @Override
    protected void makeBoardStep(Kamisado.Step step){
        game.makeStep(step, true);
        buttons[step.fromI][step.fromJ].drawTower();
        buttons[step.toI][step.toJ].drawTower();
        activeTower = null;
        game.getWinner().ifPresentOrElse(
                winner -> whosturn.setText("WINNER: " + winner.toString()),
                () -> whosturn.setText(game.getWhosTurn().toString())
        );
        enableButtons();
    }

    @Override
    protected void skipBoardStep() {
        game.skipStep();
        activeTower = null;
        game.getWinner().ifPresentOrElse(
                winner -> whosturn.setText("WINNER: " + winner.toString()),
                () -> whosturn.setText(game.getWhosTurn().toString())
        );
        enableButtons();
    }

    /**
     * This subclass of javafx's Button represents a field on the board.
     */
    class KamisadoButton extends Button {
        public final int i, j, width;

        public  KamisadoButton(int i, int j, int width){
            super();
            this.i = i;
            this.j = j;
            this.width = width;
            this.setPrefSize(width,width);
            this.setBackground(new Background(new BackgroundFill(Kamisado.colorMap[i][j], null, null)));
            super.setOnAction(this::click);
            drawTower();
        }

        private void click (ActionEvent actionEvent) {
            if (game.getWinner().isPresent()) return;

            //select a tower on this field
            if (game.getTable()[i][j] != null) {
                activeTower = game.getTable()[i][j];
            }
            // move a tower
            else {
                //Player' move
                Kamisado.Step step = new Kamisado.Step(activeTower.getI(), activeTower.getJ(), i, j);
                makeBoardStep(step);
                if (game.getWinner().isPresent()) return;
                //computer's move
                opponentsMove();
            }
            enableButtons();
        }

        /**
         * Draws a tower if there is one on the field, or clears it if there is not.
         */
        public void drawTower(){
            Kamisado.Tower tower = game.getTable()[i][j];
            if (tower == null) {
                this.setGraphic(null);
                return;
            }

            Shape towershape;
            if (tower.player == Player.PLAYER) {
                towershape = new Circle(width/5);
            }
            else {
                towershape = new Rectangle(width/2, width/2);
            }
            towershape.setFill(game.getTable()[i][j].getColor());
            towershape.setStroke(Color.BLACK);
            towershape.setStrokeWidth(2);
            this.setGraphic(towershape);
        }
    }
}
