package tictactoe.games;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import lombok.Data;
import tictactoe.Game;
import tictactoe.Player;
import java.util.*;

/**
 * Kamisado class implements the kamisado game.
 */
@Data
public class Kamisado implements Game<Kamisado.Step> {

    public static final Color[][] colorMap =  {
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
    private LinkedList<Pair<Player, Optional<Step>>> steps = new LinkedList<>();
    private final Tower[][] playerTowers = new Tower[2][8];
    private Player whosTurn;
    private Player winner;

    public Kamisado (Player starts) {
        whosTurn = starts;

        //set up towers to table's first (COMPUTER) and last (HUMAN) row, with colors matching the colormap
            for (int j = 0; j < 8; j++) {
                table[0][j] = new Tower(colorMap[0][j], Player.COMPUTER, 0, j);
                table[7][j] = new Tower(colorMap[7][j], Player.HUMAN, 7, j);
                playerTowers[Player.COMPUTER.ordinal()][j] = table[0][j] ;
                playerTowers[Player.HUMAN.ordinal()][j] = table[7][j] ;
            }
    }

    /**
     * Constructor from an existing Kamisado with making an in-game step. Throws InvalidAttributeException
     * if the step can not be made.
     * @param other The original game Kamisado.
     * @param step The steps being made.
     */
    private Kamisado (Kamisado other, Player starts, Step step) {
        this(starts);
        other.steps.forEach(s -> s.getValue().ifPresentOrElse(
                this::makeStep,
                this::skipStep));
        makeStep(step);
    }

    @Override
    public void makeStep(Step step) throws IllegalArgumentException {
        if (step.toI < 0 || step.toJ < 0 || step.toI >= 8 || step.toJ >= 8 || table[step.toI][step.toJ] != null) {
            throw new IllegalArgumentException("Illegal step indexes");
        }

        //move tower
        table[step.toI][step.toJ] = table[step.fromI][step.fromJ];
        table[step.fromI][step.fromJ] = null;
        //set tower's values
        table[step.toI][step.toJ].setI(step.toI);
        table[step.toI][step.toJ].setJ(step.toJ);

        steps.add(new Pair<>(whosTurn, Optional.of(step)));
        whosTurn = whosTurn.next();
        calculateScore();
    }

    @Override
    public void skipStep() {
        steps.add(new Pair<>(whosTurn, Optional.empty()));
        whosTurn = whosTurn.next();
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
    public LinkedList<Step> getNextSteps(Tower tower) {
        LinkedList<Step> result = new LinkedList<>();

        // if the color does not match, empty list
        if (!steps.isEmpty() && steps.getLast().getValue().isPresent() && steps.getLast().getValue().get().getToFieldColor() != tower.color)
        return result;

        // else
        int dir = whosTurn == Player.COMPUTER ? 1 : -1;
        int i, j;
        // diagonal 1
        i = tower.getI() + dir;
        j = tower.getJ();
        while (i >= 0 && i < 8 && ++j < 8 && table[i][j] == null) {
            result.add(new Step(tower.getI(), tower.getJ(), i, j));
            i += dir;
        }
        // diagonal 2
        i = tower.getI() + dir;
        j = tower.getJ();
        while (i >= 0 && i < 8 && --j >= 0 && table[i][j] == null) {
            result.add(new Step(tower.getI(), tower.getJ(), i, j));
            i += dir;
        }
        // straight
        i = tower.getI() + dir;
        j = tower.getJ();
        while (i >= 0 && i < 8 && table[i][j] == null) {
            result.add(new Step(tower.getI(), tower.getJ(), i, j));
            i += dir;
        }
        return result;
    }

    @Override
    public LinkedList<Game<Step>> getNextStates() {
        LinkedList<Game<Step>> result = new LinkedList<>();
        for (Step step : getNextSteps()){
            try {
                Player pl = steps.isEmpty() ? whosTurn : steps.getFirst().getKey();
                result.add(new Kamisado(this, pl, step));
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
    static class Tower {
        public final Color color;
        public final Player player;
        private int i, j;
        Tower (Color color, Player player, int i, int j) {
            this.player = player;
            this.color = color;
            this.i = i;
            this.j = j;
        }
    }

    /**
     * This nested class represents an in game step, with two integers meaning row and column.
     */
    @Data
    static class Step {
        public final int fromI, toI, fromJ, toJ;
        Step (int fromI, int fromJ, int toI, int toJ) {
            this.fromI = fromI;
            this.fromJ = fromJ;
            this.toI = toI;
            this.toJ = toJ;
        }

        Color getToFieldColor() {
            return colorMap[toI][toJ];
        }
        Color getFromFieldColor() {
            return colorMap[fromI][fromJ];
        }


    }
}
