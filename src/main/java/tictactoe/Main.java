package tictactoe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tictactoe.opponent.MiniMaxAI;
import tictactoe.games.Kamisado;
import tictactoe.games.KamisadoPane;
import tictactoe.games.TicTacToe;
import tictactoe.games.TicTacToePane;
import tictactoe.opponent.RemoteOpponent;
import java.io.IOException;

/**
 * The Javafx application - user interface of the game.
 */
public class Main extends Application {
    //sets the depth of the minimax search on the game trees
    public static final int AIDEPTH = 2;
    public static final int WIDTH = 400, HEIGHT = 480;

    private Opponent opponent;
    private VBox contentBox;
    private MenuBar menuBar;
    private Menu gameMenu;
    private Menu opponentMenu;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("Tic-Tac-Toe & Kamisado");
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        setMenuBar();
        contentBox = new VBox(20);
        contentBox.setPadding(new Insets(15, 12, 15, 12));
        contentBox.getChildren().addAll( menuBar, pane);

        stage.setScene(new Scene(contentBox, WIDTH+ 24, HEIGHT));
        stage.show();
    }

    private void setMenuBar () {
        gameMenu = new Menu("Game");
        MenuItem menuItem1 = new MenuItem("TicTacToe");
        menuItem1.setOnAction(e -> setGame(menuItem1.getText(), Player.OPPONENT));
        MenuItem menuItem2 = new MenuItem("Kamisado");
        menuItem2.setOnAction(e -> setGame(menuItem2.getText(), Player.OPPONENT));

        opponentMenu = new Menu("Opponent");
        MenuItem menuItem3 = new MenuItem("Computer");
        menuItem3.setOnAction(e -> setOpponent(menuItem3.getText()));
        MenuItem menuItem4 = new MenuItem("Remote - my game");
        menuItem4.setOnAction(e -> setOpponent(menuItem4.getText()));
        MenuItem menuItem5 = new MenuItem("Remote - their game");
        menuItem5.setOnAction(e -> setOpponent(menuItem4.getText()));

        gameMenu.getItems().add(menuItem1);
        gameMenu.getItems().add(menuItem2);
        gameMenu.setDisable(true);

        opponentMenu.getItems().add(menuItem3);
        opponentMenu.getItems().add(menuItem4);
        opponentMenu.getItems().add(menuItem5);

        menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);
        menuBar.getMenus().add(opponentMenu);
    }

    private void setGame(String name, Player whostarts){
        AbstractGamePane pane;
        AbstractGame game;
        switch (name) {
            case "TicTacToe":
                game = new TicTacToe (whostarts);
                pane = new TicTacToePane((TicTacToe) game, opponent, WIDTH);
                break;
            case "Kamisado":
                game = new Kamisado( (whostarts));
                pane = new KamisadoPane((Kamisado) game, opponent, WIDTH);
                break;
            default:
                game = new TicTacToe (whostarts);
                pane = new TicTacToePane((TicTacToe) game, opponent, WIDTH);
        }
        pane.setAlignment(Pos.CENTER);
        contentBox.getChildren().set(1, pane);

        //remote opponent, our game
        if (opponent instanceof RemoteOpponent && whostarts == Player.OPPONENT) {

            try {
                ((RemoteOpponent) opponent).sendGame(game);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //opponent starts
        if (whostarts == Player.OPPONENT) pane.opponentsMove();
    }

    private void setOpponent(String name) {
        switch (name) {
            case "Computer":
                try {
                    if ( opponent != null ) opponent.discard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                opponent = new MiniMaxAI(AIDEPTH);
                gameMenu.setDisable(false);
                break;
            case "Remote - my game":
                try {
                    if ( opponent != null ) opponent.discard();
                    opponent = new RemoteOpponent();
                    gameMenu.setDisable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Remote - their game":
                try {
                    if ( opponent != null ) opponent.discard();
                    opponent = new RemoteOpponent();
                    gameMenu.setDisable(true);
                    Class<? extends AbstractGame> cl = ((RemoteOpponent)opponent).receiveClass();
                    setGame(cl.getName(), Player.PLAYER);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
