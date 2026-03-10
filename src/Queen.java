import java.util.ArrayList;

public class Queen extends Piece
{

    public Queen(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 9, "Queen");
    }

    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2655" : "\u265B";
    }

    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();
        if (dRow == 0 && dCol == 0) return false;
        boolean straight = (dRow == 0 || dCol == 0);
        boolean diagonal = (Math.abs(dRow) == Math.abs(dCol));
        if (!straight && !diagonal) return false;
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
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] d : dirs)
        {
            addSlidingMoves(moves, board, d[0], d[1]);
        }
        return moves.toArray(new Point[0]);
    }
}
