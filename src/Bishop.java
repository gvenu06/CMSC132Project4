import java.util.ArrayList;

public class Bishop extends Piece
{

    public Bishop(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 3, "Bishop");
    }

    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2657" : "\u265D";
    }

    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();
        if (Math.abs(dRow) != Math.abs(dCol) || dRow == 0) return false;
        int stepR = Integer.signum(dRow);
        int stepC = Integer.signum(dCol);
        int r = getBoardRow() + stepR;
        int c = getBoardCol() + stepC;
        while (r != row || c != col)
        {
            if (board.getPieceAt(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        addSlidingMoves(moves, board, -1, -1);
        addSlidingMoves(moves, board, -1, 1);
        addSlidingMoves(moves, board, 1, -1);
        addSlidingMoves(moves, board, 1, 1);
        return moves.toArray(new Point[0]);
    }
}
