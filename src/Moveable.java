public interface Moveable
{

    boolean canMoveTo(int row, int col, Board board);

    void moveTo(int row, int col);

    Point[] getPossibleMoves(Board board);
}
