import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class Board
{

    private class Square
    {
        int row;
        int col;
        Piece occupant;
        boolean isHighlighted;

        Square(int row, int col)
        {
            this.row = row;
            this.col = col;
            this.occupant = null;
            this.isHighlighted = false;
        }
    }

    private Square[][] squares;
    private List<Piece> whiteCaptured;
    private List<Piece> blackCaptured;
    private Piece lastMovedPiece;
    private boolean fogOfWar;

    public Board()
    {
        squares = new Square[8][8];
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                squares[r][c] = new Square(r, c);
            }
        }
        whiteCaptured = new ArrayList<>();
        blackCaptured = new ArrayList<>();
        lastMovedPiece = null;
        fogOfWar = true;
    }

    public Piece getPieceAt(int row, int col)
    {
        if (row < 0 || row > 7 || col < 0 || col > 7) return null;
        return squares[row][col].occupant;
    }

    public void placePiece(Piece piece)
    {
        squares[piece.getBoardRow()][piece.getBoardCol()].occupant = piece;
    }

    public Piece removePiece(int row, int col)
    {
        Piece p = squares[row][col].occupant;
        squares[row][col].occupant = null;
        return p;
    }

    public List<Piece> getWhiteCaptured()
    {
        return whiteCaptured;
    }

    public List<Piece> getBlackCaptured()
    {
        return blackCaptured;
    }

    public Piece getLastMovedPiece()
    {
        return lastMovedPiece;
    }

    public boolean isFogOfWar()
    {
        return fogOfWar;
    }

    public void toggleFogOfWar()
    {
        fogOfWar = !fogOfWar;
    }

    public void setHighlight(int row, int col, boolean highlighted)
    {
        if (row >= 0 && row <= 7 && col >= 0 && col <= 7)
        {
            squares[row][col].isHighlighted = highlighted;
        }
    }

    public void clearHighlights()
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                squares[r][c].isHighlighted = false;
            }
        }
    }

    public void setupStandardBoard()
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                squares[r][c].occupant = null;
            }
        }
        whiteCaptured.clear();
        blackCaptured.clear();

        placePiece(new Rook(false, 0, 0));
        placePiece(new Knight(false, 0, 1));
        placePiece(new Bishop(false, 0, 2));
        placePiece(new Queen(false, 0, 3));
        placePiece(new King(false, 0, 4));
        placePiece(new Bishop(false, 0, 5));
        placePiece(new Knight(false, 0, 6));
        placePiece(new Rook(false, 0, 7));
        for (int c = 0; c < 8; c++)
        {
            placePiece(new Pawn(false, 1, c));
        }

        placePiece(new Rook(true, 7, 0));
        placePiece(new Knight(true, 7, 1));
        placePiece(new Bishop(true, 7, 2));
        placePiece(new Queen(true, 7, 3));
        placePiece(new King(true, 7, 4));
        placePiece(new Bishop(true, 7, 5));
        placePiece(new Knight(true, 7, 6));
        placePiece(new Rook(true, 7, 7));
        for (int c = 0; c < 8; c++)
        {
            placePiece(new Pawn(true, 6, c));
        }
    }

    public void clearBoard()
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                squares[r][c].occupant = null;
            }
        }
        whiteCaptured.clear();
        blackCaptured.clear();
    }

    public Piece.MoveResult makeMove(int fromRow, int fromCol, int toRow, int toCol)
    {
        Piece piece = getPieceAt(fromRow, fromCol);
        if (piece == null)
        {
            return piece != null ? piece.new MoveResult(false, false, null) : null;
        }

        if (!piece.canMoveTo(toRow, toCol, this))
        {
            return piece.new MoveResult(false, false, null);
        }

        Piece captured = null;
        boolean isCapture = false;

        if (piece instanceof Pawn && Math.abs(toCol - fromCol) == 1 && getPieceAt(toRow, toCol) == null)
        {
            captured = removePiece(fromRow, toCol);
            isCapture = true;
        }

        if (getPieceAt(toRow, toCol) != null)
        {
            captured = removePiece(toRow, toCol);
            isCapture = true;
        }

        if (captured != null)
        {
            if (captured.isWhite())
            {
                whiteCaptured.add(captured);
            } else
            {
                blackCaptured.add(captured);
            }
        }

        removePiece(fromRow, fromCol);
        piece.moveTo(toRow, toCol);
        placePiece(piece);

        if (piece instanceof King && Math.abs(toCol - fromCol) == 2)
        {
            if (toCol > fromCol)
            {
                Piece rook = removePiece(fromRow, 7);
                if (rook != null)
                {
                    rook.moveTo(fromRow, toCol - 1);
                    placePiece(rook);
                }
            } else
            {
                Piece rook = removePiece(fromRow, 0);
                if (rook != null)
                {
                    rook.moveTo(fromRow, toCol + 1);
                    placePiece(rook);
                }
            }
        }

        if (piece instanceof Pawn)
        {
            int promoRow = piece.isWhite() ? 0 : 7;
            if (toRow == promoRow)
            {
                removePiece(toRow, toCol);
                Queen queen = new Queen(piece.isWhite(), toRow, toCol);
                queen.setRotation(360);
                placePiece(queen);
            }
        }

        clearEnPassantFlags(piece);
        lastMovedPiece = piece;

        return piece.new MoveResult(true, isCapture, captured);
    }

    private void clearEnPassantFlags(Piece justMoved)
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = squares[r][c].occupant;
                if (p instanceof Pawn && p != justMoved)
                {
                    ((Pawn) p).setJustDoubleMoved(false);
                }
            }
        }
    }

    public boolean isMoveSafe(int fromRow, int fromCol, int toRow, int toCol)
    {
        Piece piece = getPieceAt(fromRow, fromCol);
        if (piece == null) return false;

        Piece captured = getPieceAt(toRow, toCol);
        Piece enPassantCaptured = null;

        if (piece instanceof Pawn && Math.abs(toCol - fromCol) == 1 && captured == null)
        {
            enPassantCaptured = getPieceAt(fromRow, toCol);
            if (enPassantCaptured != null)
            {
                squares[fromRow][toCol].occupant = null;
            }
        }

        squares[fromRow][fromCol].occupant = null;
        Piece originalTarget = squares[toRow][toCol].occupant;
        squares[toRow][toCol].occupant = piece;
        int origRow = piece.getBoardRow();
        int origCol = piece.getBoardCol();
        piece.setBoardRow(toRow);
        piece.setBoardCol(toCol);

        boolean inCheck = isInCheck(piece.isWhite());

        piece.setBoardRow(origRow);
        piece.setBoardCol(origCol);
        squares[fromRow][fromCol].occupant = piece;
        squares[toRow][toCol].occupant = originalTarget;

        if (enPassantCaptured != null)
        {
            squares[fromRow][toCol].occupant = enPassantCaptured;
        }

        return !inCheck;
    }

    public boolean isInCheck(boolean isWhite)
    {
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = squares[r][c].occupant;
                if (p instanceof King && p.isWhite() == isWhite)
                {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
            if (kingRow >= 0) break;
        }
        if (kingRow < 0) return false;

        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = squares[r][c].occupant;
                if (p != null && p.isWhite() != isWhite)
                {
                    if (p.canMoveTo(kingRow, kingCol, this))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(boolean isWhite)
    {
        if (!isInCheck(isWhite)) return false;
        return !hasLegalMoves(isWhite);
    }

    public boolean isStalemate(boolean isWhite)
    {
        if (isInCheck(isWhite)) return false;
        return !hasLegalMoves(isWhite);
    }

    private boolean hasLegalMoves(boolean isWhite)
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = squares[r][c].occupant;
                if (p != null && p.isWhite() == isWhite)
                {
                    Point[] moves = p.getPossibleMoves(this);
                    for (Point m : moves)
                    {
                        if (isMoveSafe(r, c, m.getX(), m.getY()))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<Point> getLegalMoves(int row, int col)
    {
        Piece piece = getPieceAt(row, col);
        if (piece == null) return new ArrayList<>();
        Point[] possible = piece.getPossibleMoves(this);
        List<Point> legalMoves = Arrays.stream(possible)
                .filter(move -> isMoveSafe(row, col, move.getX(), move.getY()))
                .collect(Collectors.toList());
        return legalMoves;
    }

    public boolean[][] getVisibleSquares(boolean isWhite)
    {
        boolean[][] visible = new boolean[8][8];
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = squares[r][c].occupant;
                if (p != null && p.isWhite() == isWhite)
                {
                    visible[r][c] = true;
                    Point[] moves = p.getPossibleMoves(this);
                    for (Point m : moves)
                    {
                        visible[m.getX()][m.getY()] = true;
                    }
                }
            }
        }
        return visible;
    }

    public List<Piece> getPieces(boolean isWhite)
    {
        List<Piece> pieces = new ArrayList<>();
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = squares[r][c].occupant;
                if (p != null && p.isWhite() == isWhite)
                {
                    pieces.add(p);
                }
            }
        }
        return pieces;
    }

    public void drawBoard(Graphics brush, int offsetX, int offsetY, int squareSize,
                          boolean flipped, boolean whiteToMove)
                          {
        Graphics2D g2 = (Graphics2D) brush;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean[][] visible = null;
        if (fogOfWar)
        {
            visible = getVisibleSquares(whiteToMove);
        }

        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                int displayRow = flipped ? 7 - r : r;
                int displayCol = flipped ? 7 - c : c;
                int sx = offsetX + displayCol * squareSize;
                int sy = offsetY + displayRow * squareSize;

                boolean isLight = (r + c) % 2 == 0;
                Color lightColor = new Color(240, 217, 181);
                Color darkColor = new Color(181, 136, 99);
                g2.setColor(isLight ? lightColor : darkColor);
                g2.fillRect(sx, sy, squareSize, squareSize);

                if (fogOfWar && visible != null && !visible[r][c])
                {
                    g2.setColor(new Color(30, 30, 30, 200));
                    g2.fillRect(sx, sy, squareSize, squareSize);
                }

                if (squares[r][c].isHighlighted)
                {
                    g2.setColor(new Color(100, 200, 100, 120));
                    g2.fillRect(sx, sy, squareSize, squareSize);
                }

                Piece piece = squares[r][c].occupant;
                if (piece != null)
                {
                    if (!fogOfWar || (visible != null && visible[r][c]))
                    {
                        piece.drawPiece(g2, sx, sy, squareSize);
                    }
                }
            }
        }

        g2.setColor(new Color(60, 40, 20));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(offsetX, offsetY, squareSize * 8, squareSize * 8);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(200, 200, 200));
        for (int c = 0; c < 8; c++)
        {
            int dc = flipped ? 7 - c : c;
            String label = String.valueOf((char)('a' + c));
            g2.drawString(label, offsetX + dc * squareSize + squareSize / 2 - 4,
                         offsetY + 8 * squareSize + 15);
        }
        for (int r = 0; r < 8; r++)
        {
            int dr = flipped ? 7 - r : r;
            String label = String.valueOf(8 - r);
            g2.drawString(label, offsetX - 15, offsetY + dr * squareSize + squareSize / 2 + 4);
        }
    }
}
