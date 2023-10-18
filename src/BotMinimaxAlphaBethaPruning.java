import java.util.PriorityQueue;

public class BotMinimaxAlphaBethaPruning implements Bot {
    private final char botChar;
    private final char nonBotChar;
    private double numberOfEmptyCoordinateInit = 0;

    public BotMinimaxAlphaBethaPruning(char givenChar, char nonBotChar) {
        this.botChar = givenChar;
        this.nonBotChar = nonBotChar;
    }

    private static class Tuple<A, B> {
        private A first;
        private B second;

        public Tuple(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return this.first;
        }

        public B getSecond() {
            return this.second;
        }

        public void setFirst(A first) {
            this.first = first;
        }

        public void setSecond(B second) {
            this.second = second;
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }


    private static class NodeQueueElement implements Comparable<NodeQueueElement> {
        private final int priority;
        private final char[][] board;
        private final int depth;
        private final boolean isMaximizing;
        private final int alpha;
        private final int beta;
        private final int i;
        private final int j;


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
            return Integer.compare(o.priority, this.priority);
        }

        public char[][] getBoard() {
            return board;
        }

        public int getDepth() {
            return depth;
        }

        public boolean isMaximizing() {
            return isMaximizing;
        }

        public int getAlpha() {
            return alpha;
        }

        public int getBeta() {
            return beta;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }
    }

    private boolean emptyNeighbor(int i, int j, char[][] board) {
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

    private int surroundingNonEmptyTilesCount(int i, int j, char[][] board) {
        int surroundingNonEmptyTilesCount = 0;

        if (i != 0) {
            if (!isEmptyCoordinate(i - 1, j, board)) {
                surroundingNonEmptyTilesCount++;
            }
        }
        if (i != MAX_ROW_MOVEMENT_ALLOWED - 1) {
            if (!isEmptyCoordinate(i + 1, j, board)) {
                surroundingNonEmptyTilesCount++;
            }
        }
        if (j != 0) {
            if (!isEmptyCoordinate(i, j - 1, board)) {
                surroundingNonEmptyTilesCount++;
            }
        }
        if (j != MAX_COL_MOVEMENT_ALLOWED - 1) {
            if (!isEmptyCoordinate(i, j + 1, board)) {
                surroundingNonEmptyTilesCount++;
            }
        }

        return surroundingNonEmptyTilesCount;
    }

    private Tuple<Integer, Integer> bestMove(char[][] checkBoard) {
        int score;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Tuple<Integer, Integer> move = new Tuple<>(-1, -1);
        PriorityQueue<NodeQueueElement> pq = new PriorityQueue<>();
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (isEmptyCoordinate(i, j, checkBoard) && !emptyNeighbor(i, j, checkBoard)) {
                    if (countSurroundingEnemyTile(i, j, checkBoard) != 0) {
                        checkBoard[i][j] = this.botChar;
                        pq.add(new NodeQueueElement(countSurroundingEnemyTile(i, j, checkBoard), checkBoard, 0, false, alpha, beta, i, j));
                    }
                }
            }
        }
        System.out.println("This is empty validation " + pq.isEmpty());
        if (pq.isEmpty()) {
            for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
                for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                    System.out.println(i + " " + j + " " + isEmptyCoordinate(i, j, checkBoard));
                    if (isEmptyCoordinate(i, j, checkBoard)) {
                        checkBoard[i][j] = this.botChar;
                        pq.add(new NodeQueueElement(countSurroundingEnemyTile(i, j, checkBoard), checkBoard, 0, false, alpha, beta, i, j));
                    }
                }
            }
        }

        System.out.println("This is second empty validation " + pq.isEmpty());
        while (!pq.isEmpty()) {
            NodeQueueElement n = pq.poll();

            score = this.minimax(n.getBoard(), n.getDepth(), n.isMaximizing(), n.getAlpha(), n.getBeta());
            System.out.println(n.getI() + " " + n.getJ() + " " + score);

            checkBoard[n.getI()][n.getJ()] = ' ';
            if (score > bestScore) {
                bestScore = score;
                alpha = Math.max(alpha, bestScore);
                if (beta <= alpha) {
                    pq.clear();
                    break;
                } else {
                    move.setFirst(n.getI());
                    move.setSecond(n.getJ());
                }
            }
        }

        return move;
    }

    private int maximumDepthInCertainState() {
        int maxDepth = 10;
        int minDepth = 3;
        return (int) Math.floor(minDepth + (maxDepth - minDepth) * (1 - Math.min(1, Math.max(0, numberOfEmptyCoordinateInit / (MAX_COL_MOVEMENT_ALLOWED * MAX_ROW_MOVEMENT_ALLOWED)))));
    }

    private int numberOfEmptyCoordinate(char[][] board) {
        int numberOfEmptyCoordinate = 0;
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (isEmptyCoordinate(i, j, board)) {
                    numberOfEmptyCoordinate++;
                }
            }
        }
        return numberOfEmptyCoordinate;
    }

    private int nonChangeablePoints(char[][] board) {
        int nonChangeableTilesCountBot = 0;
        int nonChangeableTilesCountEnemy = 0;
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                int numberBot = this.surroundingNonEmptyTilesCount(i, j, board);
                if (numberBot == 4) {
                    if (board[i][j] == this.botChar) {
                        nonChangeableTilesCountBot += 1;
                    } else if (board[i][j] == this.nonBotChar) {
                        nonChangeableTilesCountEnemy += 1;
                    }
                }
            }
        }
        return (nonChangeableTilesCountBot - nonChangeableTilesCountEnemy);
    }

    private int evaluateScore(char[][] board) {
        return ((this.getBotScore(board) - this.getEnemyScore(board)) + this.nonChangeablePoints(board));
    }

    private int getEnemyScore(char[][] board) {
        int score = 0;
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (board[i][j] == nonBotChar) {
                    score += 1;
                }
            }
        }
        return score;
    }

    private int getBotScore(char[][] board) {
        int score = 0;
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (board[i][j] == this.botChar) {
                    score += 1;
                }
            }
        }
        return score;
    }

    private boolean isEmptyCoordinate(int i, int j, char[][] board) {
        return (board[i][j] == ' ');
    }

    private boolean isFullyFilled(char[][] board) {
        for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
            for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                if (isEmptyCoordinate(i, j, board)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int minimax(char[][] board, int depth, boolean isMaximizing, int alpha, int beta) {
        if (isFullyFilled(board) || depth == this.maximumDepthInCertainState()) {
            return this.evaluateScore(board);
        }

        int bestScore;
        boolean prunning = false;
        if (isMaximizing) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
                for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                    if (this.isEmptyCoordinate(i, j, board)) {
                        board[i][j] = this.botChar;
                        int score = minimax(board, depth + 1, false, alpha, beta);
                        board[i][j] = ' ';
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) {
                            prunning = true;
                            break;
                        }
                    }
                }
                if (prunning) {
                    break;
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < MAX_ROW_MOVEMENT_ALLOWED; i++) {
                for (int j = 0; j < MAX_COL_MOVEMENT_ALLOWED; j++) {
                    if (this.isEmptyCoordinate(i, j, board)) {
                        board[i][j] = this.nonBotChar;
                        int score = minimax(board, depth + 1, true, alpha, beta);
                        board[i][j] = ' ';
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) {
                            prunning = true;
                            break;
                        }
                    }
                }
                if (prunning) {
                    break;
                }
            }
        }
        return bestScore;
    }

    @Override
    public int[] move(char[][] board) {
        System.out.println("-----------------------------------Minimax---------------------------------");
        this.numberOfEmptyCoordinateInit = this.numberOfEmptyCoordinate(board);
        System.out.println("This is maximum depth in certain state: " + this.maximumDepthInCertainState());
        Tuple<Integer, Integer> bestMove = this.bestMove(board);
        System.out.println("This is the best move: " + bestMove);
        return new int[]{bestMove.getFirst(), bestMove.getSecond()};
    }

}
