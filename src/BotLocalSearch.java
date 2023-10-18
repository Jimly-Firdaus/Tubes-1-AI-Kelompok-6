import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BotLocalSearch implements Bot {
    private final char botChar;
    private double temperature;

    private static final double MOVE_THRESHOLD_PROBABILITY = 0.5;
    private static final double TEMPERATURE_RATE_DECREASE = 0.9;

    public BotLocalSearch(char givenChar) {
        this.botChar = givenChar;
        this.temperature = 2.0;
    }

    private double moveAcceptanceProbability(double currentScore, double newScore, double temperature) {
        if (newScore > currentScore + 1) {
            return 1.0;
        }
        double probability = Math.exp((-1) * (newScore - currentScore) / temperature);
        System.out.println(probability);
        System.out.println("T: " + this.temperature);
        return probability;
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
        long startTime = System.currentTimeMillis(); // get the start time

        int currentScore = this.evaluate(board, this.botChar);
        List<int[]> successors = new ArrayList<>();

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
            // 5 seconds limit
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 5000) {
                break;
            }

            int randomPoint = rand.nextInt(successors.size());
            char[][] modifiedBoard = this.patchNewChar(successors.get(randomPoint)[0], successors.get(randomPoint)[1],
                    board, this.botChar);

            int evaluation = this.evaluate(modifiedBoard, this.botChar);

            this.temperature *= TEMPERATURE_RATE_DECREASE;

            // if found good neighbor
            if (evaluation > currentScore + 1) {
                System.out.println("eval: " + evaluation);
                System.out.println("cur: " + currentScore);
                System.out.println("NYAMMM");
                return successors.get(randomPoint);
            }

            // if found bad neighbor
            if (this.moveAcceptanceProbability(currentScore, evaluation,
                    this.temperature) > MOVE_THRESHOLD_PROBABILITY) {
                System.out.println("eval: " + evaluation);
                System.out.println("cur: " + currentScore);
                System.out.println("Baddie");
                return successors.get(randomPoint);
            }
        }

        System.out.println("Random HEHE");

        int randomPoint = rand.nextInt((!successors.isEmpty()) ? successors.size() : 1);
        return successors.get(randomPoint);
    }

    private boolean isEmptyCoordinate(int i, int j, char[][] board) {
        return (board[i][j] == ' ');
    }

    private char[][] patchNewChar(int i, int j, char[][] board, char character) {
        char[][] newBoard = Arrays.stream(board).map(char[]::clone).toArray(char[][]::new);
        newBoard[i][j] = character;

        int[][] directions = { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };

        for (int[] direction : directions) {
            int newI = i + direction[0];
            int newJ = j + direction[1];

            // Change its adjacent too
            if (newI >= 0 && newI < MAX_ROW_MOVEMENT_ALLOWED && newJ >= 0 && newJ < MAX_COL_MOVEMENT_ALLOWED) {
                if (newBoard[newI][newJ] != character && newBoard[newI][newJ] != ' ') {
                    newBoard[newI][newJ] = character;
                }
            }
        }

        for (int i1 = 0; i1 < MAX_ROW_MOVEMENT_ALLOWED; i1++) {
            for (int j1 = 0; j1 < MAX_COL_MOVEMENT_ALLOWED; j1++) {
                if (j1 == 0) {
                    System.out.print("| ");
                }
                System.out.print(newBoard[i1][j1] + " | ");
            }
            System.out.println("\n---------------------------------");
        }
        System.out.println();

        return newBoard;
    }
}
