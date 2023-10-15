public interface Bot {

    int MAX_ROW_MOVEMENT_ALLOWED = 8;
    int MAX_COL_MOVEMENT_ALLOWED = 8;

    // move bot
    public int[] move(char[][] board);
}
