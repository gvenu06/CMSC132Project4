import java.awt.*;
import java.util.ArrayList;

public abstract class Piece extends Polygon implements Moveable
{

    public class MoveResult
    {
        public boolean isValid;
        public boolean isCapture;
        public Piece capturedPiece;

        public MoveResult(boolean isValid, boolean isCapture, Piece capturedPiece)
        {
            this.isValid = isValid;
            this.isCapture = isCapture;
            this.capturedPiece = capturedPiece;
        }
    }

    private boolean isWhite;
    private int boardRow;
    private int boardCol;
    private int pointValue;
    private boolean hasMoved;
    private String pieceName;

    public Piece(boolean isWhite, int boardRow, int boardCol, int pointValue, String pieceName)
    {
        super(new Point[]{new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1)},
              new Point(0, 0), 0);
        this.isWhite = isWhite;
        this.boardRow = boardRow;
        this.boardCol = boardCol;
        this.pointValue = pointValue;
        this.hasMoved = false;
        this.pieceName = pieceName;
    }

    public boolean isWhite()
    {
        return isWhite;
    }

    public int getBoardRow()
    {
        return boardRow;
    }

    public int getBoardCol()
    {
        return boardCol;
    }

    public int getPointValue()
    {
        return pointValue;
    }

    public boolean hasMoved()
    {
        return hasMoved;
    }

    public String getPieceName()
    {
        return pieceName;
    }

    @Override
    public void moveTo(int row, int col)
    {
        this.boardRow = row;
        this.boardCol = col;
        this.hasMoved = true;
    }

    public void setBoardRow(int row)
    {
        this.boardRow = row;
    }

    public void setBoardCol(int col)
    {
        this.boardCol = col;
    }

    public void setHasMoved()
    {
        this.hasMoved = true;
    }

    public abstract String getSymbol();

    protected boolean isValidTarget(int row, int col, Board board)
    {
        if (row < 0 || row > 7 || col < 0 || col > 7)
        {
            return false;
        }
        Piece occupant = board.getPieceAt(row, col);
        return occupant == null || occupant.isWhite() != this.isWhite;
    }

    protected void addSlidingMoves(ArrayList<Point> moves, Board board, int dRow, int dCol)
    {
        int r = boardRow + dRow;
        int c = boardCol + dCol;
        while (r >= 0 && r <= 7 && c >= 0 && c <= 7)
        {
            Piece occupant = board.getPieceAt(r, c);
            if (occupant == null)
            {
                moves.add(new Point(r, c));
            } else if (occupant.isWhite() != this.isWhite)
            {
                moves.add(new Point(r, c));
                break;
            } else
            {
                break;
            }
            r += dRow;
            c += dCol;
        }
    }

    public void drawPiece(Graphics brush, int screenX, int screenY, int squareSize)
    {
        Graphics2D g2 = (Graphics2D) brush;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Font pieceFont = new Font("Serif", Font.PLAIN, (int)(squareSize * 0.75));
        g2.setFont(pieceFont);

        String symbol = getSymbol();
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);
        int textHeight = fm.getAscent();

        int drawX = screenX + (squareSize - textWidth) / 2;
        int drawY = screenY + (squareSize + textHeight) / 2 - fm.getDescent();

        g2.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g2.drawString(symbol, drawX, drawY);

        g2.setColor(isWhite ? Color.DARK_GRAY : Color.GRAY);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawString(symbol, drawX + 1, drawY + 1);

        g2.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g2.drawString(symbol, drawX, drawY);
    }
}
