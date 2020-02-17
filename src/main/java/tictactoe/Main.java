package tictactoe;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tictactoe.games.Kamisado;
import tictactoe.games.KamisadoPlane;
import tictactoe.games.TicTacToe;
import tictactoe.games.TicTacToePlane;

public class Main extends Application {
    //sets the depth of the minimax search on the game trees
    public static final int AIDEPTH = 2;
    //tictactoe specifics
    private final static int SIZE = 10, WINS = 5;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("Tic-Tac-Toe");
        Label whosturn = new Label("PLAYER");
        //TicTacToePlane plane = new TicTacToePlane(new TicTacToe (SIZE, WINS, Player.HUMAN), whosturn, 300);
        KamisadoPlane plane = new KamisadoPlane(new Kamisado(Player.HUMAN), whosturn, 400);

        plane.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(whosturn,plane);
        vbox.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(vbox, 420, 480));
        stage.show();
    }
}
