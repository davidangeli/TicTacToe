package tictactoe.games;

import javafx.scene.paint.Color;
import lombok.Data;
import tictactoe.Game;
import tictactoe.Player;
import java.util.*;

/**
 * Kamisado class implements the kamisado game.
 */
@Data
public class Kamisado implements Game<Kamisado.Step> {

    private static final Color[][] colorMap =  {
            { Color.ORANGE, Color.BLUE, Color.PURPLE, Color.PINK, Color.YELLOW, Color.RED, Color.GREEN, Color.BROWN },
            { Color.RED, Color.ORANGE, Color.PINK, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BROWN, Color.PURPLE },
            { Color.GREEN, Color.PINK, Color.ORANGE, Color.RED, Color.PURPLE, Color.BROWN, Color.YELLOW, Color.BLUE },
            { Color.PINK, Color.PURPLE, Color.BLUE, Color.ORANGE, Color.BROWN, Color.GREEN, Color.RED, Color.YELLOW },
            { Color.YELLOW, Color.RED, Color.GREEN, Color.BROWN, Color.ORANGE, Color.BLUE, Color.PURPLE, Color.PINK },
            { Color.BLUE, Color.YELLOW, Color.BROWN, Color.PURPLE, Color.RED, Color.ORANGE, Color.PINK, Color.GREEN },
            { Color.PURPLE, Color.BROWN, Color.YELLOW, Color.BLUE, Color.GREEN, Color.PINK, Color.ORANGE, Color.RED } ,
            { Color.BROWN, Color.GREEN, Color.RED, Color.YELLOW, Color.PINK, Color.PURPLE, Color.BLUE, Color.ORANGE }
    };
    private final Tower[][] table = new Tower[8][8];
    private int score = 0;
    private LinkedList<Step> steps = new LinkedList<>();
    private final Tower[][] playerTowers = new Tower[2][8];
    private Player whosTurn;
    private Player winner;

    public Kamisado (Player starts) {
        whosTurn = starts;

        //set up towers to table's first (COMPUTER) and last (HUMAN) row, with colors matching the colormap
        for (Player pl : Player.values()) {
            for (int j = 0; j < 8; j++) {
                int i = pl.ordinal()*7;
                Tower tower = new Tower(colorMap[i][j], i, j);
                table[i][j] = tower;
                playerTowers[pl.ordinal()][j] = tower;
            }
        }
    }

    /**
     * Constructor from an existing Kamisado with making an in-game step. Throws InvalidAttributeException
     * if the step can not be made.
     * @param other The original game Kamisado.
     * @param step The steps being made.
     */
    public Kamisado (Kamisado other, Step step) {
        if (other.table[step.i][step.j] != null) throw new IllegalArgumentException("Field is not null.");

        whosTurn = other.whosTurn;
        for (int i=0; i < 8; i++){
            table[i] = other.table[i].clone();
        }
        playerTowers[Player.COMPUTER.ordinal()] = other.playerTowers[Player.COMPUTER.ordinal()].clone();
        playerTowers[Player.HUMAN.ordinal()] = other.playerTowers[Player.HUMAN.ordinal()].clone();

        steps.addAll(other.steps);
        makeStep(step);
    }

    @Override
    public void makeStep(Step step) throws IllegalArgumentException {
        if (step.i<0 || step.j<0 || step.i>=8 || step.j>=8 || table[step.i][step.j] != null) {
            throw new IllegalArgumentException("Illegal step indexes");
        }

        int i = step.tower.getI(), j = step.tower.getJ();
        table[i][j] = null;
        table[step.i][step.j] = step.tower;
        step.tower.setI(step.i);
        step.tower.setJ(step.j);
        steps.add(step);
        whosTurn = whosTurn.next();
        calculateScore();
    }

    /**
     * {@inheritDoc}
     * In tictactoe, this list is not filtered: contains all possible steps.
     * @return
     */
    @Override
    public LinkedList<Step> getNextSteps() {
        LinkedList<Step> result = new LinkedList<>();
        if (winner != null) return result;

        for (Tower tower : playerTowers[whosTurn.ordinal()]) {
            result.addAll(getNextSteps(tower));
        }
        return result;
    }

    /**
     * Collect all possible steps for one tower, according to the Kamisado rules.
     * @param tower The tower who's possible steps we collect.
     * @return LinkedList<Step>
     */
    private LinkedList<Step> getNextSteps(Tower tower) {
        LinkedList<Step> result = new LinkedList<>();

        // if the color does not match, empty list
        if (!steps.isEmpty() && steps.getLast().getColor() != tower.color)
        return result;

        // else
        int dir = whosTurn == Player.COMPUTER ? 1 : -1;
        int i, j;
        // diagonal 1
        i = tower.getI() + dir;
        j = tower.getJ();
        while (i >= 0 && i < 8 && ++j < 8 && table[i][j] == null) {
            result.add(new Step(tower, i, j));
            i += dir;
        }
        // diagonal 2
        i = tower.getI() + dir;
        j = tower.getJ();
        while (i >= 0 && i < 8 && --j >= 0 && table[i][j] == null) {
            result.add(new Step(tower, i, j));
            i += dir;
        }
        // straight
        i = tower.getI() + dir;
        j = tower.getJ();
        while (i >= 0 && i < 8 && table[i][j] == null) {
            result.add(new Step(tower, i, j));
            i += dir;
        }
        return result;
    }

    @Override
    public LinkedList<Game<Step>> getNextStates() {
        LinkedList<Game<Step>> result = new LinkedList<>();
        for (Step step : getNextSteps()){
            try {
                result.add(new Kamisado(this, step));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Optional<Player> getWinner() {
        if (winner == null) return Optional.empty();
        return Optional.of(winner);
    }

    /**
     * Calculates and sets score for a given state.
     */
    private void calculateScore() {

        for (Player pl : Player.values()) {

            int x = pl == Player.COMPUTER ? 1 : -1;
            int goal = pl == Player.COMPUTER ? 7 : 0;
            int distr = 4;
            //valuation based on towers' row positions
            for (Tower tower : playerTowers[pl.ordinal()]) {
                //winning : computer at row 7, human at row 0
                if (tower.getI() == goal) {
                    score += x * 100000;
                    winner = pl;
                    return;
                }
                //getting closer
                score += x * Math.abs(goal-tower.getI()) * 10;
                //more distribution between neighbors is good?
                score += x * Math.abs(tower.getI() - distr) * 20;
                distr = tower.getI();
            }
        }

        //idea 1: value the gamestate counting the possible winning or losing strikes from it
    }

    /**
     * This class represents a tower in game, with two integers meaning row and column.
     */
    @Data
    class Tower {
        public final Color color;
        private int i, j;
        Tower (Color color, int i, int j) {
            this.color = color;
            this.i = i;
            this.j = j;
        }
    }

    /**
     * This nested class represents an in game step, with two integers meaning row and column.
     */
    static class Step {
        public final Tower tower;
        public final int i, j;
        Step (Tower tower, int i, int j) {
            this.tower = tower;
            this.i = i;
            this.j = j;
        }

        Color getColor() {
            return colorMap[i][j];
        }


    }
}
