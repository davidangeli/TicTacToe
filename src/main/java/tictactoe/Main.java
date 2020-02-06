package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final int SIZE = 10;
    private static final int AIDEPTH = 3;
    private static final Scanner scanner = new Scanner(System.in);
    private final static State game = new State(SIZE, Player.HUMAN);

    public static void main(String[] args) {

        while (game.getWinner().isEmpty()) {
            playerMove();
            try {
                int[] step = AI.getNextStep(game, AIDEPTH);
                game.makeStep(step[0],step[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int j = 0; j < SIZE; j++){
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
        game.makeStep(i,j);
    }
}