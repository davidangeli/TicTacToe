package tictactoe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tictactoe.games.TicTacToe;
import tictactoe.games.TicTacToePlane;

public class Main extends Application {
    private static TicTacToe game;
    public static final int AIDEPTH = 3;
    private final static int SIZE = 10, WINS = 5;

    public static void main(String[] args) {

        game = new TicTacToe (SIZE, WINS, Player.HUMAN);
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        if (game==null) return;

        stage.setTitle("Tic-Tac-Toe");
        Label whosturn = new Label("Player");
        TicTacToePlane plane = new TicTacToePlane(game, whosturn, 300);

        stage.setScene(new Scene(new VBox(whosturn,plane), 320, 480));
        stage.show();
    }
}
