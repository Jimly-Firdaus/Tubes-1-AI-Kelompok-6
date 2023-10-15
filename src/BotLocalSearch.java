import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BotLocalSearch implements Bot {
    private char botChar;
    private double temperature;

    // TODO: change this to appropriate value
    private final double MOVE_THRESHOLD_PROBABILITY = 0.1;
    private final double TEMPERATURE_RATE_DECREASE = 0.1;

    public BotLocalSearch(char givenChar) {
        this.botChar = givenChar;

        // TODO: change this to appropriate value
        this.temperature = 2.0;
    }

    private double moveAcceptanceProbability(double currentScore, double newScore, double temperature) {
        if (newScore > currentScore) {
            return 1.0;
        }
        return Math.exp((newScore - currentScore) / temperature);
    }

    private int evaluate(char[][] checkedBoard, char checkChar) {
        int score = 0;
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (checkChar == checkedBoard[i][j]) {
                    score++;
                }
            }
        }

        return score;
    }

    public int[] move(char[][] board) {
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (j == 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " | ");
            }
            System.out.println("\n---------------------------------");
        }
        System.out.println();

        int currentScore = this.evaluate(board, this.botChar);
        List<int[]> successors = new ArrayList<int[]>();

        // invoke all successsors
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (isEmptyCoordinate(i, j, board)) {
                    successors.add(new int[] { i, j });
                }
            }
        }

        Random rand = new Random();

        while (this.temperature > 0) {
            int randomPoint = rand.nextInt(successors.size());
            char[][] modifiedBoard = this.patchNewChar(successors.get(randomPoint)[0], successors.get(randomPoint)[1],
                    board, this.botChar);

            int evaluation = this.evaluate(modifiedBoard, this.botChar);

            // TODO: change this to appropriate value
            this.temperature -= this.TEMPERATURE_RATE_DECREASE;

            // if found good neighbor
            if (evaluation > currentScore)
                return successors.get(randomPoint);

            // if found bad neighbor
            if (this.moveAcceptanceProbability(currentScore, evaluation,
                    this.temperature) > this.MOVE_THRESHOLD_PROBABILITY) {
                return successors.get(randomPoint);
            }
        }

        int randomPoint = rand.nextInt(successors.size());
        return successors.get(randomPoint);
    }

    private boolean isEmptyCoordinate(int i, int j, char[][] board) {
        return (board[i][j] == ' ');
    }

    private char[][] patchNewChar(int i, int j, char[][] board, char character) {
        board[i][j] = character;

        int[][] directions = { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };

        for (int[] direction : directions) {
            int newI = i + direction[0];
            int newJ = j + direction[1];

            // Change its adjacent too
            if (newI >= 0 && newI < MAX_ROW_MOVEMENT_ALLOWED && newJ >= 0 && newJ < MAX_COL_MOVEMENT_ALLOWED) {
                if (board[newI][newJ] != character && board[newI][newJ] != ' ') {
                    board[newI][newJ] = character;
                }
            }
        }

        return board;
    }
}
