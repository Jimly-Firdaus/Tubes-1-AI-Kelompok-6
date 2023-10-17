import java.util.PriorityQueue;

public class BotMinimaxAlphaBethaPruning implements Bot {
    private char botChar;
    private char nonBotChar;
    private int minDepth = 5;
    private int maxDepth = 10;
    private double numberOfEmptyCoordinateInit = 0;

    public BotMinimaxAlphaBethaPruning(char givenChar, char nonBotChar){
        this.botChar = givenChar;
        this.nonBotChar = nonBotChar;
    }

    public static class NodeQueueElement implements Comparable<NodeQueueElement> {
        private int priority;
        public char[][] board;
        public int depth;
        public boolean isMaximizing;
        public int alpha;
        public int beta;
        public int i;
        public int j;


        public NodeQueueElement(int priority, char[][] board, int depth, boolean isMaximizing, int alpha, int beta, int i, int j) {
            this.priority = priority;
            this.board = board;
            this.depth = depth;
            this.isMaximizing = isMaximizing;
            this.alpha = alpha;
            this.beta = beta;
            this.i = i;
            this.j = j;
        }

        @Override
        public int compareTo(NodeQueueElement o) {
            if (this.priority < o.priority) {
                return 1;
            } else if (this.priority == o.priority) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private boolean emptyNeighbor(int i, int j, char[][] board){
        boolean isEmpty = true;
        
        if (i != 0) {
            isEmpty &= isEmptyCoordinate(i - 1, j, board);
        }
        if (i != MAX_ROW_MOVEMENT_ALLOWED - 1) {
            isEmpty &= isEmptyCoordinate(i + 1, j, board);
        }
        if (j != 0) {
            isEmpty &= isEmptyCoordinate(i, j - 1, board);
        } 
        if (j != MAX_COL_MOVEMENT_ALLOWED - 1) {
            isEmpty &= isEmptyCoordinate(i, j + 1, board);
        }

        return isEmpty;
    }

    private int countSurroundingEnemyTile(int i, int j, char[][] board) {
        int surroundingEnemyCount = 0;

        if (i != 0) {
            if (board[i - 1][j] == nonBotChar) {
                surroundingEnemyCount++;
            }
        }
        if (i != MAX_ROW_MOVEMENT_ALLOWED - 1) {
            if (board[i + 1][j] == nonBotChar) {
                surroundingEnemyCount++;
            }
        }
        if (j != 0) {
            if (board[i][j - 1] == nonBotChar) {
                surroundingEnemyCount++;
            }
        }
        if (j != MAX_COL_MOVEMENT_ALLOWED - 1) {
            if (board[i][j + 1] == nonBotChar) {
                surroundingEnemyCount++;
            }
        }

        return surroundingEnemyCount;
    }

    private Tuple<Integer, Integer> bestMove(char[][] checkBoard){
        int score= 0;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Tuple<Integer, Integer> move = new Tuple<>(-1,-1);
        PriorityQueue<NodeQueueElement> pq = new PriorityQueue<>();
        for (int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
            for(int j= 0; j<MAX_COL_MOVEMENT_ALLOWED; j++){
                if(isEmptyCoordinate(i, j, checkBoard) && !emptyNeighbor(i, j, checkBoard)){
                    checkBoard[i][j] = this.botChar;
                    if(countSurroundingEnemyTile(i, j, checkBoard) != 0){
                        pq.add(new NodeQueueElement(countSurroundingEnemyTile(i, j, checkBoard), checkBoard, 0, false, alpha, beta, i, j));
                    }
                }
            }
        }
        if(pq.isEmpty()){
            for (int i= 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++){
            for(int j= 0; j<MAX_COL_MOVEMENT_ALLOWED; j++){
                if(isEmptyCoordinate(i, j, checkBoard) && !emptyNeighbor(i, j, checkBoard)){
                    checkBoard[i][j] = this.botChar;
                    pq.add(new NodeQueueElement(countSurroundingEnemyTile(i, j, checkBoard), checkBoard, 0, false, alpha, beta, i, j));
                }
            }
        }
        }
        while (!pq.isEmpty()) {
            NodeQueueElement n = pq.poll();

            score = this.minimax(n.board, n.depth, n.isMaximizing, n.alpha, n.beta);

            checkBoard[n.i][n.j] = ' ';
            if(score > bestScore){
                bestScore = score;
                alpha = Math.max(alpha, score);
                if(beta <= alpha){
                    pq.clear();
                    break;
                }else{
                    move.setFirst(n.i);
                    move.setSecond(n.j);
                }
            }
        }

        return move;
    }

    private int maximumDepthInCertainState(char[][] board){
        int result = (int) Math.floor(minDepth + (maxDepth - minDepth) * (1 - Math.min(1, Math.max(0, numberOfEmptyCoordinateInit / (MAX_COL_MOVEMENT_ALLOWED * MAX_ROW_MOVEMENT_ALLOWED)))));
        System.out.println(result);
        return result;
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
        if(isFullyFilled(board) || depth == this.maximumDepthInCertainState(board)){
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
    }
    
}
