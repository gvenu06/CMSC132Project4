import java.util.ArrayList;

public class Rook extends Piece
{

    public Rook(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 5, "Rook");
    }

    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2656" : "\u265C";
    }

    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();
        if (dRow != 0 && dCol != 0) return false;
        if (dRow == 0 && dCol == 0) return false;
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
        addSlidingMoves(moves, board, -1, 0);
        addSlidingMoves(moves, board, 1, 0);
        addSlidingMoves(moves, board, 0, -1);
        addSlidingMoves(moves, board, 0, 1);
        return moves.toArray(new Point[0]);
    }
}
