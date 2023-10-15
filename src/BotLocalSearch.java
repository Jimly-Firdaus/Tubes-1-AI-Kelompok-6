public class BotLocalSearch implements Bot {
    
    // private int[][] gameState;
    private double temperature;

    private double moveAcceptanceProbability(double currentScore, double newScore, double temperature) {
        if (newScore > currentScore) {
            return 1.0;
        }
        return Math.exp((newScore - currentScore) / temperature);
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

        return new int[]{(int) (Math.random()*8), (int) (Math.random()*8)};
    }

    public boolean isEmptyCoordinate(int i, int j, char[][] board) {
        return (board[i][j] == ' ');
    }

}
