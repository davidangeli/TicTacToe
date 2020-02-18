package tictactoe.games;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import lombok.EqualsAndHashCode;
import tictactoe.Player;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Graphical interface for the Kamisado Game. Extends javafx's GridPane.
 * Represents the game's table as a grid of buttons. Size is always 8x8.
 */
public class KamisadoPlane extends GridPane {
    private final Kamisado game;
    private final Label whosturn;
    private final KamisadoButton[][] buttons = new KamisadoButton[8][8];
    private final Button skipButton;
    private Kamisado.Tower activeTower = null;

    public KamisadoPlane(Kamisado game, Label whosturn, int width){
        setHgap(2);
        setVgap(2);
        this.game = game;
        this.whosturn = whosturn;
        this.whosturn.setText(game.getWhosTurn().toString());
        //kamisado field buttons
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++) {
                KamisadoButton button = new KamisadoButton(i, j, width / 10);
                buttons[i][j] = button;
                this.add(button, j, i, 1, 1);
            }
        }

        //skipbutton
        skipButton = new Button("Skip");
        skipButton.setOnAction(e -> {
            game.skipStep();
            updateBoard(Optional.empty());
            Optional<Kamisado.Step> aiStep = game.makeAIStep();
            updateBoard(aiStep);
        });
        this.add(skipButton, 7, 8, 1, 1);
        enableButtons();
    }

    /**
     * Controls which fields are enabled for the player, according to the game's actual standing.
     */
    private void enableButtons(){
        //disable all
        this.getChildren().forEach(c -> {
            c.setDisable(true);
            c.setStyle("");
        });
        if (game.getWinner().isPresent()) return;

        //enable towers if color matches last step, or first move
        for (Kamisado.Tower tower : game.getPlayerTowers()[Player.HUMAN.ordinal()]) {
            if (game.getSteps().isEmpty() || game.getSteps().getLast().getValue().isEmpty() || game.getSteps().getLast().getValue().get().getToFieldColor() == tower.color) {
                buttons[tower.getI()][tower.getJ()].setDisable(false);
                buttons[tower.getI()][tower.getJ()].setStyle("-fx-border-color: #f00000; -fx-border-width: 2px;");
            }}

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

    //TODO: rethink without the use of Optional<> as parameter
    /**
     * Making a step on the game's board representation.
     * @param step An in-game Step object.
     */
    private void updateBoard(Optional<Kamisado.Step> step){
        step.ifPresent( st -> {
            buttons[st.fromI][st.fromJ].drawTower();
            buttons[st.toI][st.toJ].drawTower();
            activeTower = null;
        });

        game.getWinner().ifPresentOrElse(
                winner -> whosturn.setText("WINNER: " + winner.toString()),
                () -> whosturn.setText(game.getWhosTurn().toString())
        );
    }

    /**
     * This subclass of javafx's Button represents a field on the board.
     */
    @EqualsAndHashCode(callSuper = true)
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
                try {
                    //Player' move
                    Kamisado.Step step = new Kamisado.Step(activeTower.getI(), activeTower.getJ(), i, j);
                    game.makeStep(step, true);
                    updateBoard(Optional.of(step));
                    if (game.getWinner().isPresent()) return;
                    //computer's move
                    Optional<Kamisado.Step> aiStep = game.makeAIStep();
                    updateBoard(aiStep);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            enableButtons();
        }

        public void drawTower(){
            Kamisado.Tower tower = game.getTable()[i][j];
            if (tower == null) {
                this.setGraphic(null);
                return;
            }

            Shape towershape;
            if (tower.player == Player.HUMAN) {
                towershape = new Circle(width/5);
                //((Circle)towershape).setRadius(width/5);
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
