public class BotLocalSearch implements Bot {
    
    // private int[][] gameState;

    public int[] move(char[][] board) {


        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                System.out.print(board[i][j] + " - ");
            }
            System.out.println("");
        }


        return new int[]{(int) (Math.random()*8), (int) (Math.random()*8)};
        // return null;
    }

}