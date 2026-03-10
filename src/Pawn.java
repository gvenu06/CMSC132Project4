import java.util.ArrayList;

public class Pawn extends Piece
{

    private boolean justDoubleMoved;

    public Pawn(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 1, "Pawn");
        this.justDoubleMoved = false;
    }

    public boolean justDoubleMoved()
    {
        return justDoubleMoved;
    }

    public void setJustDoubleMoved(boolean val)
    {
        this.justDoubleMoved = val;
    }

    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2659" : "\u265F";
    }

    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        int direction = isWhite() ? -1 : 1;
        int dRow = row - getBoardRow();
        int dCol = col - getBoardCol();

        if (dCol == 0 && dRow == direction && board.getPieceAt(row, col) == null)
        {
            return true;
        }

        int startRow = isWhite() ? 6 : 1;
        if (dCol == 0 && dRow == 2 * direction && getBoardRow() == startRow
                && board.getPieceAt(getBoardRow() + direction, col) == null
                && board.getPieceAt(row, col) == null)
                {
            return true;
        }

        if (Math.abs(dCol) == 1 && dRow == direction)
        {
            Piece target = board.getPieceAt(row, col);
            if (target != null && target.isWhite() != isWhite())
            {
                return true;
            }
            Piece adjacent = board.getPieceAt(getBoardRow(), col);
            if (adjacent instanceof Pawn && adjacent.isWhite() != isWhite()
                    && ((Pawn) adjacent).justDoubleMoved())
                    {
                return true;
            }
        }

        return false;
    }

    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int r = getBoardRow();
        int c = getBoardCol();

        if (r + direction >= 0 && r + direction <= 7 && board.getPieceAt(r + direction, c) == null)
        {
            moves.add(new Point(r + direction, c));
            int startRow = isWhite() ? 6 : 1;
            if (r == startRow && board.getPieceAt(r + 2 * direction, c) == null)
            {
                moves.add(new Point(r + 2 * direction, c));
            }
        }

        for (int dc = -1; dc <= 1; dc += 2)
        {
            int nc = c + dc;
            int nr = r + direction;
            if (nc >= 0 && nc <= 7 && nr >= 0 && nr <= 7)
            {
                Piece target = board.getPieceAt(nr, nc);
                if (target != null && target.isWhite() != isWhite())
                {
                    moves.add(new Point(nr, nc));
                }
                Piece adjacent = board.getPieceAt(r, nc);
                if (adjacent instanceof Pawn && adjacent.isWhite() != isWhite()
                        && ((Pawn) adjacent).justDoubleMoved())
                        {
                    moves.add(new Point(nr, nc));
                }
            }
        }

        return moves.toArray(new Point[0]);
    }

    @Override
    public void moveTo(int row, int col)
    {
        justDoubleMoved = (Math.abs(row - getBoardRow()) == 2);
        super.moveTo(row, col);
    }
}
