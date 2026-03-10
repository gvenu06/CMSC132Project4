import java.util.ArrayList;

public class Knight extends Piece
{

    public Knight(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 3, "Knight");
    }

    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2658" : "\u265E";
    }

    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        if (!isValidTarget(row, col, board)) return false;
        int dRow = Math.abs(row - getBoardRow());
        int dCol = Math.abs(col - getBoardCol());
        return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
    }

    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        int[][] offsets = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] off : offsets)
        {
            int nr = getBoardRow() + off[0];
            int nc = getBoardCol() + off[1];
            if (nr >= 0 && nr <= 7 && nc >= 0 && nc <= 7 && isValidTarget(nr, nc, board))
            {
                moves.add(new Point(nr, nc));
            }
        }
        return moves.toArray(new Point[0]);
    }
}
