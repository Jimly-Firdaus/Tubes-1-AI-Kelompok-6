public class BotMinimaxAlphaBethaPruning implements Bot {
    private char botChar;
    private char nonBotChar;
    private int minDepth = 3;
    private int maxDepth = 20;
    private double numberOfEmptyCoordinateInit = 0;

    public BotMinimaxAlphaBethaPruning(char givenChar, char nonBotChar){
        this.botChar = givenChar;
        this.nonBotChar = nonBotChar;
    }

    private boolean emptyNeighbor(int i, int j, char[][] board){
        boolean isEmpty = true;
        
        if (i != 0) {
            isEmpty &= isEmptyCoordinate(i - 1, j, board);
            // isempty &=bawah
        } 
        if (i != MAX_ROW_MOVEMENT_ALLOWED - 1) {
            // isemtpy &= atas
            isEmpty &= isEmptyCoordinate(i + 1, j, board);
        }
        if (j != 0) {
            // &= kanan
            isEmpty &= isEmptyCoordinate(i, j - 1, board);
        } 
        if (j != MAX_COL_MOVEMENT_ALLOWED - 1) {
            //  &=kiri
            isEmpty &= isEmptyCoordinate(i, j + 1, board);
        }

        return isEmpty;
    }

    private Tuple<Integer, Integer> bestMove(char[][] checkBoard){
        int score= 0;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Tuple<Integer, Integer> move = new Tuple<>(-1,-1);
        boolean prunning = false;
        for (int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
            for(int j= 0; j<MAX_COL_MOVEMENT_ALLOWED; j++){
                if(isEmptyCoordinate(i, j, checkBoard) && !emptyNeighbor(i, j, checkBoard)){
                    checkBoard[i][j] = this.botChar;
                    score = this.minimax(checkBoard, 0, false, alpha, beta);
                    checkBoard[i][j] = ' ';
                    if(score > bestScore){
                        bestScore = score;
                        alpha = Math.max(alpha, score);
                        if(beta <= alpha){
                            prunning = true;
                            break;
                        }else{
                            move.setFirst(i);
                            move.setSecond(j);
                        }
                    }
                }
            }
            if(prunning){
                break;
            }
        }
        return move;
    }

    private int maximumDepthInCertainState(char[][] board){
        // System.out.println("This is maximum depth");
        // System.out.println(this.numberOfEmptyCoordinateInit);
        // System.out.println((MAX_COL_MOVEMENT_ALLOWED*MAX_ROW_MOVEMENT_ALLOWED));
        // System.out.println(this.numberOfEmptyCoordinateInit/(MAX_COL_MOVEMENT_ALLOWED*MAX_ROW_MOVEMENT_ALLOWED));
        // System.out.println((this.minDepth + (this.maxDepth - this.minDepth) * (this.numberOfEmptyCoordinateInit/(MAX_COL_MOVEMENT_ALLOWED*MAX_ROW_MOVEMENT_ALLOWED))));
        // return ((int) Math.floor(this.minDepth + (this.maxDepth - this.minDepth) *  (this.numberOfEmptyCoordinateInit/(MAX_COL_MOVEMENT_ALLOWED*MAX_ROW_MOVEMENT_ALLOWED))));
        return 3;
    }

    private int numberOfEmptyCoordinate(char[][] board){
        int numberOfEmptyCoordinate = 0;
        for (int i= 0; i< MAX_ROW_MOVEMENT_ALLOWED; i++){
            for (int j= 0; j<MAX_COL_MOVEMENT_ALLOWED; j++){
                if(isEmptyCoordinate(i, j, board)){
                    numberOfEmptyCoordinate++;
                }
            }
        }
        return numberOfEmptyCoordinate;
    }

    private int evaluateScore(char[][] board){
        return (this.getBotScore(board) - this.getEnemyScore(board));
    }

    private int getEnemyScore(char[][] board){
        int score = 0;
        for(int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
            for (int j= 0; j < MAX_COL_MOVEMENT_ALLOWED; j++){
                if(board[i][j] == nonBotChar){
                    score += 1;
                }
            }
        }
        return score;
    }

    private int getBotScore(char[][] board){
        int score = 0;
        for(int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
            for (int j= 0; j < MAX_COL_MOVEMENT_ALLOWED; j++){
                if(board[i][j] == this.botChar){
                    score += 1;
                }
            }
        }
        return score;
    }

    private boolean isEmptyCoordinate(int i, int j, char[][] board) {
        return (board[i][j] == ' ');
    }

    private boolean isFullyFilled(char[][] board){
        for(int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
            for(int j= 0; j<MAX_COL_MOVEMENT_ALLOWED; j++){
                if(isEmptyCoordinate(i, j, board)){
                    return false;
                }
            }
        }
        return true;
    }

    private int minimax(char[][] board, int depth, boolean isMaximizing, int alpha, int beta){
        System.out.println("minimax");
        if(isFullyFilled(board) || depth == this.maximumDepthInCertainState(board)){
            System.out.println(this.evaluateScore(board));
            return this.evaluateScore(board);
        }

        if(isMaximizing){
            int bestScore = Integer.MIN_VALUE;
            boolean prunning = false;
            for(int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
                for(int j= 0; j < MAX_COL_MOVEMENT_ALLOWED; j++){
                    if(this.isEmptyCoordinate(i, j, board)){
                        board[i][j] = this.botChar;
                        int score = minimax(board, depth+1, false, alpha, beta);
                        board[i][j] = ' ';
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, score);
                        if(beta <= alpha){
                            prunning = true;
                            break;
                        }
                    }
                }
                if(prunning) {
                    break;
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            boolean prunning = false;
            for(int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
                for(int j= 0; j < MAX_COL_MOVEMENT_ALLOWED; j++){
                    if(this.isEmptyCoordinate(i, j, board)){
                        board[i][j] = this.nonBotChar;
                        int score = minimax(board, depth+1, true, alpha, beta);
                        board[i][j] = ' ';
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, score);
                        if(beta <= alpha){
                            prunning = true;
                            break;
                        }
                    }
                }
                if(prunning){
                    break;
                }
            }
            return bestScore;
        }
    }

    @Override
    public int[] move(char[][] board) {
        this.numberOfEmptyCoordinateInit = this.numberOfEmptyCoordinate(board);
        Tuple<Integer, Integer> bestMove = this.bestMove(board);
        System.out.println(bestMove);
        return new int[] {bestMove.getFirst(), bestMove.getSecond()};
        // return new int[] {1, 1};
    }
    
}
