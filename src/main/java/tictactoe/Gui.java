package tictactoe;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tictactoe.games.GameState;
import tictactoe.games.TicTacToe;

public class Gui extends Application {
    private static GameState game;
    //todo: move this out
    private static final int AIDEPTH = 3;
    private final static int SIZE = 10, WINS = 5;

    public static void main(String[] args) {

        game = new TicTacToe (SIZE, WINS, Player.HUMAN);
        launch(args);


        System.out.println("winner:" + game.getWinner());
    }

    @Override
    public void start(Stage stage) {
        if (game==null) return;

        stage.setTitle("Tic-Tac-Toe");
        stage.setScene(getScene(((TicTacToe)game).getSize()));
        stage.show();
    }
    
    private Scene getScene(int size) {
        TicTacToe tictactoe = (TicTacToe) game;


        Label whosturn = new Label("Player");
        TilePane tilePane = new TilePane();


        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                TicTacToeButton button = new TicTacToeButton(new TicTacToe.Step(i,j));
                button.setPrefSize(30,30);
                button.setOnAction(value ->  {
                    // HUMAN clicks
                    if (!button.getText().equals("")) return;
                    try {
                        tictactoe.makeStep(button.getStep());
                    }
                    catch (IllegalArgumentException e)
                    {
                        e.printStackTrace();
                    }
                    button.setText("X");
                    whosturn.setText("Computer");

                    // COMPUTER turns
                    try {
                        TicTacToe.Step step = AI.getNextStep(tictactoe, AIDEPTH);
                        for (Node n : tilePane.getChildren()){
                            if (n.getId().equals(step.i+","+step.j)) {
                                ((Button) n).setText("O");
                            }
                        }
                        try {
                            tictactoe.makeStep(step);
                        }
                        catch (IllegalArgumentException e)
                        {
                            e.printStackTrace();
                        }
                        whosturn.setText("Player");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                tilePane.getChildren().add(button);
            }
        }

        return new Scene(new VBox(whosturn,tilePane), 320, 480);

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    static class TicTacToeButton extends Button {
        public  TicTacToeButton(TicTacToe.Step step){
            super();
            this.step = step;
            super.setId(step.i+","+ step.j);
        }
        TicTacToe.Step step;
    }
}
