package com.gamedata;

import com.Main;
import com.pages.GamePage;
import com.style.GameCellButton;

import javax.swing.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.style.GameCellButton.cellStateColors;


/**
 * Represents a running game, stores data and handles game mechanics.
 */
public class Game implements Serializable {

    private static final ArrayList<Game> games = new ArrayList<>(); // Access list for statistics and saving
    private static final Random randomizer = new Random(); // One randomizer is enough for a run of the game

    private transient GamePage gamePage; // Page shouldn't be saved to memory, last tbc game will be handled differently

    // Status
    private GameStates state = GameStates.TBC; // Every game instantiated will first go through this state
    private int usedRowCount;
    private long usedMillis;
    private Instant startTime;
    private Instant finishTime;

    private JFrame attachedFrame;

    private final String equation;

    private final Cell[][] gameBoard;
    private final int rows = 6;
    private final int cols;
    private int selectedRow;
    private int selectedCol;

    /**
     * Creates and handles display of a new game run, initializes game board
     *
     * @param attachedFrame frame the game run belongs to
     */
    public Game(JFrame attachedFrame) {
        games.add(this);
        this.attachedFrame = attachedFrame;
        this.equation = generateEquation();
        this.startTime = Instant.now();
        this.cols = equation.length();
        this.gameBoard = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameBoard[i][j] = new Cell();
            }
        }

        for (int i = 0; i < cols; i++) {
            this.gameBoard[0][i].setState(CellStates.ACTIVE);
        }

        this.selectedRow = 0;
        this.selectedCol = 0;
        this.gameBoard[0][0].setState(CellStates.SELECTED);

        this.gamePage = new GamePage(attachedFrame, this);
        this.attachedFrame.setContentPane(this.gamePage.createPage());
        this.attachedFrame.revalidate();
        this.attachedFrame.repaint();

    }

    /**
     * Generates and arithmetic equation based on a random node count (operation) by a node tree algorith, brute forces
     * to fall within the specified range
     *
     * @return Equation as a String
     */
    public static String generateEquation() {

        String expression = "";
        String equation = "";
        int targetLength = randomizer.nextInt(3) + 7;
        // Creating a tree for all possible expressions based on operands reduces the brute force time to the minimal.
        do {
            int operandCount = randomizer.nextInt(3) + 2;
            int limit = operandCount < 3 ? 999 : 99;
            TreeNode finalTree = buildEquationTree(operandCount, limit);
            expression = finalTree.toString();
            int resultInt = (int) evaluateExpression(expression);
            String result = String.valueOf(resultInt);
            if (resultInt < 0) {
                continue;
            }
            equation = expression + "=" + result;
        } while (equation.length() != targetLength);

        System.out.println(equation);
        return equation;
    }

    /**
     * Represents a node on the tree created from either another subtree or value on each branch
     */
    private static class TreeNode {

        private String left;
        private String right;
        private String operand;


        public TreeNode(String left, String right, String operand) {
            this.left = left;
            this.right = right;
            this.operand = operand;
        }

        public TreeNode(TreeNode left, TreeNode right, String operand) {
            this(left.toString(), right.toString(), operand);
        }


        public TreeNode(int left, TreeNode right, String operand) {
            this(String.valueOf(left), right.toString(), operand);
        }


        public TreeNode(TreeNode left, int right, String operand) {
            this(left.toString(), String.valueOf(right), operand);
        }


        public TreeNode(int left, int right, String operand) {
            this(String.valueOf(left), String.valueOf(right), operand);
        }

        @Override
        public String toString() {
            return left + operand + right;
        }
    }

    /**
     * Recursive algorithm to build an equation at the top of the tree, each node represents an operation with inputs,
     * subtrees if possible
     * <a href="https://stackoverflow.com/questions/32936045/generate-a-random-math-equation-using-random-numbers-and-operators-in-javascript">Idea</a>
     *
     * @param nodeCount Operations required
     * @param limit     Limit of the integers, Some iterations are impossible for 3 digit numbers
     * @return
     */
    private static TreeNode buildEquationTree(int nodeCount, int limit) {

        int numLeft = (int) Math.ceil(nodeCount / 2D);
        int numRight = (int) Math.floor(nodeCount / 2D);

        String[] allowedOperands = {"+", "*", "-"};
        String operand = allowedOperands[randomizer.nextInt(allowedOperands.length)];

        int leftInt = randomizer.nextInt(limit) + 1;
        int rightInt = randomizer.nextInt(limit) + 1;

        if (numLeft <= 1 && numRight <= 1) {
            if (leftInt > rightInt && leftInt % rightInt == 0) { // No non integer divisions
                allowedOperands = new String[]{"+", "*", "-", "/"};
                operand = allowedOperands[randomizer.nextInt(allowedOperands.length)];
            }
            return new TreeNode(leftInt, rightInt, operand);
        } else if (numLeft <= 1) {
            return new TreeNode(randomizer.nextInt(limit) + 1, buildEquationTree(numRight, limit), operand);
        } else if (numRight <= 1) {
            return new TreeNode(buildEquationTree(numLeft, limit), randomizer.nextInt(limit) + 1, operand);
        }

        TreeNode leftSubTree = buildEquationTree(numLeft, limit);
        TreeNode rightSubTree = buildEquationTree(numRight, limit);

        return new TreeNode(leftSubTree, rightSubTree, operand);
    }

    /**
     * Splits an expression into parts to account for arithmetic priority, expression is divided into sums of values,
     * those sums either stand alone or have to be implemented for multiplication and division. All multiplication and
     * division operators are split and each sub sum is evaluated in order.
     *
     * @param expression Mathematical expression to evaluate
     * @return result value, double for debugging
     */
    public static double evaluateExpression(String expression) {
        String[] summations = expression.split("\\+|(?=-)");

        double sum = 0D;

        for (String summation : summations) {
            if (summation.contains("/") || summation.contains("*")) {
                String[] multiplicatives = summation.split("/|\\*");
                double multiplication = Double.parseDouble(multiplicatives[0]);
                Matcher m = Pattern.compile("/|\\*").matcher(summation);
                int group = 0;
                while (m.find() && group < (multiplicatives.length - 1)) {
                    if (summation.charAt(m.start()) == '*') {
                        multiplication = multiplication * Double.parseDouble(multiplicatives[group + 1]);
                    } else {
                        multiplication = multiplication / Double.parseDouble(multiplicatives[group + 1]);
                    }
                    group++;
                }
                sum += multiplication;
            } else {
                sum += Double.parseDouble(summation);
            }
        }

        return sum;

    }

    /**
     * Calculates and returns an access map of past game statistics
     *
     * @return LinkedHashMap of all games' statistics
     */
    public static LinkedHashMap<String, Double> getGameStatistics() {
        double unfinishedGames = 0D;
        double successfulGames = 0D;
        double unsuccessfulGames = 0D;
        double averageRowsUsed = 0D;
        double averageTimeUsed = 0D;

        for (Game game : Game.games) {
            switch (game.state) {
                case SUCCESSFUL -> {
                    successfulGames++;
                    averageRowsUsed += game.usedRowCount;
                    averageTimeUsed += game.calculateDuration();
                }

                case UNSUCCESSFUL -> unsuccessfulGames++;

                case TBC -> unfinishedGames++;
            }
        }

        if (successfulGames != 0) {
            averageRowsUsed = averageRowsUsed / successfulGames;
            averageTimeUsed = averageTimeUsed / successfulGames;
        } else {
            averageRowsUsed = 0D;
            averageTimeUsed = 0D;
        }

        LinkedHashMap<String, Double> statisticsMap = new LinkedHashMap<>();
        statisticsMap.put("Unfinished Games", unfinishedGames);
        statisticsMap.put("Successful Games", successfulGames);
        statisticsMap.put("Unsuccessful Games", unsuccessfulGames);
        statisticsMap.put("Average Rows Used (Successful Games)", averageRowsUsed);
        statisticsMap.put("Average Time Used (Successful Games)", averageTimeUsed);

        return statisticsMap;
    }

    public static Game getLastGame() {

        if (games.size() == 0) {
            return null;
        }

        Game lastGame = games.get(games.size() - 1);

        // If the last game is read from serialized memory, game page will not exist for the sakes of performance
        // Thus it has to be initialized if it is continuable
        if (lastGame.gamePage == null && lastGame.state == GameStates.TBC) {
            lastGame.gamePage = new GamePage(lastGame.attachedFrame, lastGame);
        }
        return games.get(games.size() - 1);
    }


    public long getUsedMillis() {
        return usedMillis;
    }

    public void setUsedMillis(long usedMillis) {
        this.usedMillis = usedMillis;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long calculateDuration() {

        return usedMillis + Duration.between(startTime, Instant.now()).toMillis();
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Instant finishTime) {
        this.finishTime = finishTime;
    }

    public GamePage getGamePage() {
        return gamePage;
    }

    public GameStates getState() {
        return state;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void setSelectedCol(int selectedCol) {
        if (getSelectedCell().getState() == CellStates.SELECTED) {
            getSelectedCell().setState(CellStates.ACTIVE);
        }

        this.selectedCol = selectedCol;
        getSelectedCell().setState(CellStates.SELECTED);
    }

    public Cell[] getActiveRow() {
        return gameBoard[getSelectedRow()];
    }


    public Cell getSelectedCell() {
        return gameBoard[getSelectedRow()][getSelectedCol()];
    }


    public Cell[][] getGameBoard() {
        return gameBoard;
    }


    public int getCols() {
        return cols;
    }

    public String getGuess() {
        String guess;
        char[] guessChars = new char[getActiveRow().length];
        for (int i = 0; i < getActiveRow().length; i++) {
            guessChars[i] = getActiveRow()[i].getValue();
        }
        guess = String.valueOf(guessChars);
        return guess;
    }

    /**
     * Evaluates and styles guessed cells accordingly
     *
     * @return true if all values are matched
     */
    public boolean evaluateGuess() {
        String guess = getGuess();
        boolean allCorrect = true;

        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == equation.charAt(i)) {
                getActiveRow()[i].setState(CellStates.CORRECT);
            } else {
                allCorrect = false;
                if (equation.contains(String.valueOf(guess.charAt(i)))) {
                    getActiveRow()[i].setState(CellStates.PARTIAL);

                } else {
                    getActiveRow()[i].setState(CellStates.INCORRECT);

                }
            }
        }

        usedRowCount++;

        return allCorrect;

    }

    /**
     * Checks if guess is a valid expression
     *
     * @return
     */
    public boolean guessComputes() {
        String guess = getGuess();

        if (!guess.contains("=") ||
                (
                        !guess.contains("+") &&
                                !guess.contains("-") &&
                                !guess.contains("/") &&
                                !guess.contains("*")
                )
        ) {
            return false;
        }

        // Last or first symbol can't be equals or non digit (minus excluded)
        if (
                !Character.isDigit(guess.charAt(0)) && guess.charAt(0) != '-' // Start, exclude negative number
                        || !Character.isDigit(guess.charAt(guess.length() - 1)) // End
        ) {
            return false;
        }

        for (int i = 0; i < guess.length() - 1; i++) {
            if (
                    !Character.isDigit(guess.charAt(i))  // Find an operator
                            && (( // Detect double operator
                            !(Character.isDigit(guess.charAt(i + 1))  // Check if its next to another one
                                    || guess.charAt(i + 1) == '-')) // Exclude negative number after equals sign
                            || ( // Detect integer with leading 0
                                i < equation.length() - 2 // Avoid index error
                                    && !Character.isDigit(equation.charAt(i)) // Check for non digit trailing character
                                    && equation.charAt(i + 1) == '0'    // Check for leading 0
                                    && Character.isDigit(equation.charAt(i + 2))  // Find a trailing digit
                    ))
            ) {
                return false;
            }
        }

        String expression = guess.split("=")[0];
        double result = evaluateExpression(expression);
        if (result%1!=0||!String.valueOf((int) result).equals(guess.split("=")[1])) {
            return false;
        }

        return true;

    }

    public void finishGame(int finishState) {

        finishTime = Instant.now();
        long duration = calculateDuration();

        switch (finishState) {
            case 1 -> {
                // VICTORY
                JOptionPane.showMessageDialog(null, String.format("Denemeler: %d, Süre: %02d:%02d", usedRowCount, (int) ((duration / 1000) / 60), (int) (duration / 1000) % 60), "Kazandýn!", JOptionPane.PLAIN_MESSAGE);
                this.state = GameStates.SUCCESSFUL;
            }

            case 0 -> {
                // LOSS
                JOptionPane.showMessageDialog(null, String.format("Denemeler: %d, Süre: %02d:%02d, \nDenklem: %s", usedRowCount, (int) ((duration / 1000) / 60), (int) (duration / 1000) % 60, equation), "Kaybettin!", JOptionPane.PLAIN_MESSAGE);

                this.state = GameStates.UNSUCCESSFUL;
            }

            case -1 -> {
                // TBC
                this.usedMillis += Duration.between(startTime, finishTime).toMillis();
                this.state = GameStates.TBC;
            }
        }

        Main.mainFrame.setContentPane(Main.initialPage.createPage());

    }


    /**
     * Represents current state of the game, used to classify past runs.
     */
    public enum GameStates {
        SUCCESSFUL,
        UNSUCCESSFUL,
        TBC,
    }

    /**
     * Represents the state of a cell, special operations are done based on these states.
     */
    public enum CellStates {
        INACTIVE,
        ACTIVE,
        SELECTED,
        CORRECT,
        INCORRECT,
        PARTIAL,
    }

    /**
     * Object representing a cell on the game board, holds a character, a button and state, has special styling
     */
    public class Cell {

        private CellStates state;
        private char value;
        private final GameCellButton button;

        /**
         * Default constructor for the cell, button is kept inactive and represents empty space char
         *
         * @see Cell#Cell(CellStates, char)
         */
        public Cell() {
            this(CellStates.INACTIVE, ' ');
        }

        /**
         * Constructs a cell at given state and holding given char
         *
         * @param state Determines behavior and styling of cell
         * @param value the char cell holds
         */
        public Cell(CellStates state, char value) {
            this.state = state;
            this.value = value;
            this.button = new GameCellButton(String.valueOf(value), 18);
            this.button.setIdleBackGroundColor(cellStateColors.get(state));
        }


        public CellStates getState() {
            return state;
        }

        public void setState(CellStates state) {
            this.state = state;
            this.button.setIdleBackGroundColor(cellStateColors.get(state)); // Instead of refreshing whole board
        }

        public char getValue() {
            return value;
        }

        public void setValue(char value) {
            this.value = value;
            this.button.setText(String.valueOf(value)); // Instead of refreshing whole board
        }

        public GameCellButton getButton() {
            return button;
        }

    }


    @Override
    public String toString() {
        return "Game{" +
                "equation='" + equation + '\'' +
                ", startTime=" + startTime +
                ", state=" + state +
                ", usedRowCount=" + usedRowCount +
                ", finishTime=" + finishTime +
                '}';
    }
}
