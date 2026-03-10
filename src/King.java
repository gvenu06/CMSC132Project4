import java.util.ArrayList;

public class King extends Piece
{

    public King(boolean isWhite, int boardRow, int boardCol)
    {
        super(isWhite, boardRow, boardCol, 0, "King");
    }

    @Override
    public String getSymbol()
    {
        return isWhite() ? "\u2654" : "\u265A";
    }

    @Override
    public boolean canMoveTo(int row, int col, Board board)
    {
        int dRow = Math.abs(row - getBoardRow());
        int dCol = Math.abs(col - getBoardCol());
        if (dRow <= 1 && dCol <= 1 && (dRow + dCol) > 0 && isValidTarget(row, col, board))
        {
            return true;
        }
        if (!hasMoved() && dRow == 0 && Math.abs(col - getBoardCol()) == 2)
        {
            return canCastle(col, board);
        }
        return false;
    }

    @Override
    public Point[] getPossibleMoves(Board board)
    {
        ArrayList<Point> moves = new ArrayList<>();
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++)
        {
            int nr = getBoardRow() + dr[i];
            int nc = getBoardCol() + dc[i];
            if (nr >= 0 && nr <= 7 && nc >= 0 && nc <= 7 && isValidTarget(nr, nc, board))
            {
                moves.add(new Point(nr, nc));
            }
        }
        if (!hasMoved())
        {
            if (canCastle(getBoardCol() + 2, board))
            {
                moves.add(new Point(getBoardRow(), getBoardCol() + 2));
            }
            if (canCastle(getBoardCol() - 2, board))
            {
                moves.add(new Point(getBoardRow(), getBoardCol() - 2));
            }
        }
        return moves.toArray(new Point[0]);
    }

    private boolean canCastle(int targetCol, Board board)
    {
        if (hasMoved()) return false;
        int row = getBoardRow();
        if (targetCol == getBoardCol() + 2)
        {
            Piece rook = board.getPieceAt(row, 7);
            if (rook == null || !(rook instanceof Rook) || rook.hasMoved() || rook.isWhite() != isWhite())
            {
                return false;
            }
            for (int c = getBoardCol() + 1; c < 7; c++)
            {
                if (board.getPieceAt(row, c) != null) return false;
            }
            return true;
        }
        if (targetCol == getBoardCol() - 2)
        {
            Piece rook = board.getPieceAt(row, 0);
            if (rook == null || !(rook instanceof Rook) || rook.hasMoved() || rook.isWhite() != isWhite())
            {
                return false;
            }
            for (int c = 1; c < getBoardCol(); c++)
            {
                if (board.getPieceAt(row, c) != null) return false;
            }
            return true;
        }
        return false;
    }
}
