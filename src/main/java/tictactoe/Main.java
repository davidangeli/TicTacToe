package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final int AIDEPTH = 3;
    private static final Scanner scanner = new Scanner(System.in);
    private final static TicTacToeState game = new TicTacToeState(Player.HUMAN);

    public static void main(String[] args) {

        while (game.getWinner().isEmpty()) {
            playerMove();
            try {
                game.makeStep(AI.getNextStep(game, AIDEPTH));
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int j = 0; j < game.getTable().length; j++){
                System.out.println(Arrays.toString(game.getTable()[j]));
            }
        }

        System.out.println("winner:" + game.getWinner());
        scanner.close();
    }

    public static void playerMove(){
        int i, j;
        System.out.println("Enter row, column:");
        i = scanner.nextInt();
        j = scanner.nextInt();
        scanner.nextLine();
        game.makeStep(new TicTacToeState.Step(i,j));
    }
}