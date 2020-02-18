package tictactoe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tictactoe.ai.MiniMaxAI;
import tictactoe.games.Kamisado;
import tictactoe.games.KamisadoPlane;
import tictactoe.games.TicTacToe;
import tictactoe.games.TicTacToePlane;

public class Main extends Application {
    //sets the depth of the minimax search on the game trees
    public static final int AIDEPTH = 2;
    public static final int WIDTH = 400;

    private Label whosturn;
    private VBox contentBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("Tic-Tac-Toe");
        whosturn = new Label("");
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        MenuBar menuBar = setMenuBar();
        contentBox = new VBox(20);
        contentBox.setPadding(new Insets(15, 12, 15, 12));
        contentBox.getChildren().addAll( menuBar, whosturn, pane);

        stage.setScene(new Scene(contentBox, WIDTH+ 24, 480));
        stage.show();
    }

    private MenuBar setMenuBar () {
        Menu menu = new Menu("New Game");

        MenuItem menuItem1 = new MenuItem("TicTacToe");
        menuItem1.setOnAction(e -> {
            GridPane pane = new TicTacToePlane(new TicTacToe(Player.HUMAN, new MiniMaxAI(AIDEPTH)), whosturn, WIDTH);
            pane.setAlignment(Pos.CENTER);
            contentBox.getChildren().set(2, pane);
        });
        MenuItem menuItem2 = new MenuItem("Kamisado");
        menuItem2.setOnAction(e -> {
            KamisadoPlane pane = new KamisadoPlane(new Kamisado (Player.HUMAN, new MiniMaxAI(AIDEPTH)), whosturn, WIDTH);
            contentBox.getChildren().set(2, pane);
            pane.setAlignment(Pos.CENTER);
        });

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        return menuBar;
    }
}
