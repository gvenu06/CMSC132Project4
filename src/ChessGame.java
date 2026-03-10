import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChessGame extends Game
{

    private class GameState
    {
        Player currentPlayer;
        boolean isCheck;
        boolean isCheckmate;
        boolean isStalemate;
        List<Piece> capturedWhite;
        List<Piece> capturedBlack;

        GameState(Player white)
        {
            this.currentPlayer = white;
            this.isCheck = false;
            this.isCheckmate = false;
            this.isStalemate = false;
            this.capturedWhite = new ArrayList<>();
            this.capturedBlack = new ArrayList<>();
        }
    }

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 750;
    private static final int SQUARE_SIZE = 75;
    private static final int BOARD_OFFSET_X = 80;
    private static final int BOARD_OFFSET_Y = 60;

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private GameState gameState;
    private boolean whiteToMove;
    private boolean boardFlipped;

    private int cursorRow;
    private int cursorCol;
    private int selectedRow;
    private int selectedCol;
    private boolean pieceSelected;
    private List<Point> currentLegalMoves;

    private boolean puzzleMode;
    private PuzzleLoader puzzleLoader;
    private PuzzleSolver puzzleSolver;
    private String statusMessage;
    private int statusTimer;

    private boolean gameOver;
    private boolean showMenu;

    public ChessGame()
    {
        super("Chess - Fog of War", WINDOW_WIDTH, WINDOW_HEIGHT);

        board = new Board();
        whitePlayer = new Player("White", true);
        blackPlayer = new Player("Black", false);
        gameState = new GameState(whitePlayer);
        whiteToMove = true;
        boardFlipped = false;

        cursorRow = 6;
        cursorCol = 4;
        selectedRow = -1;
        selectedCol = -1;
        pieceSelected = false;
        currentLegalMoves = new ArrayList<>();

        puzzleMode = false;
        puzzleLoader = new PuzzleLoader();
        puzzleSolver = new PuzzleSolver(puzzleLoader);
        statusMessage = "";
        statusTimer = 0;

        gameOver = false;
        showMenu = true;

        this.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }
        });

        board.setupStandardBoard();
    }

    private void handleKeyPress(KeyEvent e)
    {
        int code = e.getKeyCode();

        if (showMenu)
        {
            if (code == KeyEvent.VK_1)
            {
                showMenu = false;
                gameOver = false;
                puzzleMode = false;
                whiteToMove = true;
                boardFlipped = false;
                board.setupStandardBoard();
                cursorRow = 6;
                cursorCol = 4;
                pieceSelected = false;
                statusMessage = "White's turn - Use arrow keys and Enter";
                statusTimer = 120;
            } else if (code == KeyEvent.VK_2)
            {
                showMenu = false;
                gameOver = false;
                puzzleMode = true;
                whiteToMove = true;
                boardFlipped = false;
                board.toggleFogOfWar();
                puzzleLoader.loadPuzzle(board);
                cursorRow = 4;
                cursorCol = 4;
                pieceSelected = false;
                statusMessage = "Puzzle " + (puzzleLoader.getCurrentPuzzle() + 1) + " - Find the best move!";
                statusTimer = 120;
            } else if (code == KeyEvent.VK_3)
            {
                showMenu = false;
                gameOver = false;
                puzzleMode = false;
                whiteToMove = true;
                boardFlipped = false;
                board.setupStandardBoard();
                board.toggleFogOfWar();
                cursorRow = 6;
                cursorCol = 4;
                pieceSelected = false;
                statusMessage = "Fog of War disabled - Standard chess";
                statusTimer = 120;
            }
            return;
        }

        if (gameOver)
        {
            if (code == KeyEvent.VK_R)
            {
                resetGame();
            } else if (code == KeyEvent.VK_M)
            {
                showMenu = true;
                gameOver = false;
                if (!board.isFogOfWar())
                {
                    board.toggleFogOfWar();
                }
            }
            return;
        }

        if (code == KeyEvent.VK_ESCAPE)
        {
            showMenu = true;
            if (!board.isFogOfWar())
            {
                board.toggleFogOfWar();
            }
            return;
        }

        if (code == KeyEvent.VK_F)
        {
            board.toggleFogOfWar();
            statusMessage = board.isFogOfWar() ? "Fog of War ON" : "Fog of War OFF";
            statusTimer = 60;
            return;
        }

        if (code == KeyEvent.VK_H && puzzleMode)
        {
            statusMessage = "Hint: " + puzzleSolver.getHint();
            statusTimer = 90;
            return;
        }

        int dRow = 0, dCol = 0;
        if (code == KeyEvent.VK_UP) dRow = boardFlipped ? 1 : -1;
        if (code == KeyEvent.VK_DOWN) dRow = boardFlipped ? -1 : 1;
        if (code == KeyEvent.VK_LEFT) dCol = boardFlipped ? 1 : -1;
        if (code == KeyEvent.VK_RIGHT) dCol = boardFlipped ? -1 : 1;

        if (dRow != 0 || dCol != 0)
        {
            int newRow = cursorRow + dRow;
            int newCol = cursorCol + dCol;
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7)
            {
                cursorRow = newRow;
                cursorCol = newCol;
            }
            return;
        }

        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE)
        {
            if (!pieceSelected)
            {
                Piece piece = board.getPieceAt(cursorRow, cursorCol);
                if (piece != null && piece.isWhite() == whiteToMove)
                {
                    selectedRow = cursorRow;
                    selectedCol = cursorCol;
                    pieceSelected = true;
                    board.clearHighlights();
                    currentLegalMoves = board.getLegalMoves(selectedRow, selectedCol);
                    for (Point m : currentLegalMoves)
                    {
                        board.setHighlight(m.getX(), m.getY(), true);
                    }
                    statusMessage = piece.getPieceName() + " selected - " + currentLegalMoves.size() + " moves";
                    statusTimer = 60;
                }
            } else
            {
                if (cursorRow == selectedRow && cursorCol == selectedCol)
                {
                    pieceSelected = false;
                    board.clearHighlights();
                    currentLegalMoves.clear();
                    statusMessage = "";
                    return;
                }

                boolean isLegal = false;
                for (Point m : currentLegalMoves)
                {
                    if (m.getX() == cursorRow && m.getY() == cursorCol)
                    {
                        isLegal = true;
                        break;
                    }
                }

                if (isLegal)
                {
                    if (puzzleMode)
                    {
                        boolean correct = puzzleSolver.checkSolution(selectedRow, selectedCol,
                                cursorRow, cursorCol);
                        Piece.MoveResult result = board.makeMove(selectedRow, selectedCol,
                                cursorRow, cursorCol);
                        boolean mated = puzzleSolver.checkIfCheckmate(board, whiteToMove);

                        if (correct || mated)
                        {
                            whitePlayer.addPuzzleSolved();
                            statusMessage = "Correct! Press N for next puzzle";
                            statusTimer = 300;
                            pieceSelected = false;
                            board.clearHighlights();
                            currentLegalMoves.clear();
                        } else
                        {
                            statusMessage = "Incorrect. Press R to retry";
                            statusTimer = 300;
                            pieceSelected = false;
                            board.clearHighlights();
                            currentLegalMoves.clear();
                        }
                    } else
                    {
                        Piece.MoveResult result = board.makeMove(selectedRow, selectedCol,
                                cursorRow, cursorCol);
                        if (result != null && result.isValid)
                        {
                            pieceSelected = false;
                            board.clearHighlights();
                            currentLegalMoves.clear();

                            whiteToMove = !whiteToMove;
                            boardFlipped = !boardFlipped;

                            if (result.isCapture && result.capturedPiece != null)
                            {
                                List<Piece> captured = result.capturedPiece.isWhite()
                                        ? board.getWhiteCaptured() : board.getBlackCaptured();
                                captured.sort((p1, p2) -> p2.getPointValue() - p1.getPointValue());
                            }

                            if (board.isCheckmate(whiteToMove))
                            {
                                gameState.isCheckmate = true;
                                gameOver = true;
                                if (whiteToMove)
                                {
                                    blackPlayer.addWin();
                                    whitePlayer.addLoss();
                                    statusMessage = "Checkmate! Black wins! (R=restart, M=menu)";
                                } else
                                {
                                    whitePlayer.addWin();
                                    blackPlayer.addLoss();
                                    statusMessage = "Checkmate! White wins! (R=restart, M=menu)";
                                }
                                statusTimer = 600;
                            } else if (board.isStalemate(whiteToMove))
                            {
                                gameState.isStalemate = true;
                                gameOver = true;
                                statusMessage = "Stalemate! Draw! (R=restart, M=menu)";
                                statusTimer = 600;
                            } else if (board.isInCheck(whiteToMove))
                            {
                                gameState.isCheck = true;
                                statusMessage = (whiteToMove ? "White" : "Black") + " is in CHECK!";
                                statusTimer = 90;
                            } else
                            {
                                gameState.isCheck = false;
                                statusMessage = (whiteToMove ? "White" : "Black") + "'s turn";
                                statusTimer = 60;
                            }
                        }
                    }
                } else
                {
                    Piece piece = board.getPieceAt(cursorRow, cursorCol);
                    if (piece != null && piece.isWhite() == whiteToMove)
                    {
                        selectedRow = cursorRow;
                        selectedCol = cursorCol;
                        board.clearHighlights();
                        currentLegalMoves = board.getLegalMoves(selectedRow, selectedCol);
                        for (Point m : currentLegalMoves)
                        {
                            board.setHighlight(m.getX(), m.getY(), true);
                        }
                        statusMessage = piece.getPieceName() + " selected - " + currentLegalMoves.size() + " moves";
                        statusTimer = 60;
                    }
                }
            }
            return;
        }

        if (puzzleMode)
        {
            if (code == KeyEvent.VK_N)
            {
                puzzleLoader.nextPuzzle();
                puzzleLoader.loadPuzzle(board);
                pieceSelected = false;
                board.clearHighlights();
                currentLegalMoves.clear();
                cursorRow = 4;
                cursorCol = 4;
                statusMessage = "Puzzle " + (puzzleLoader.getCurrentPuzzle() + 1) + " - Find the best move!";
                statusTimer = 120;
            } else if (code == KeyEvent.VK_R)
            {
                puzzleLoader.loadPuzzle(board);
                pieceSelected = false;
                board.clearHighlights();
                currentLegalMoves.clear();
                statusMessage = "Puzzle reset - Try again!";
                statusTimer = 90;
            }
        }

        if (code == KeyEvent.VK_R && !puzzleMode)
        {
            resetGame();
        }
    }

    private void resetGame()
    {
        board.setupStandardBoard();
        whiteToMove = true;
        boardFlipped = false;
        pieceSelected = false;
        board.clearHighlights();
        currentLegalMoves.clear();
        cursorRow = 6;
        cursorCol = 4;
        gameOver = false;
        gameState.isCheck = false;
        gameState.isCheckmate = false;
        gameState.isStalemate = false;
        statusMessage = "Game reset - White's turn";
        statusTimer = 90;
    }

    @Override
    public void paint(Graphics brush)
    {
        Graphics2D g2 = (Graphics2D) brush;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(new Color(40, 40, 45));
        g2.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (showMenu)
        {
            drawMenu(g2);
            return;
        }

        board.drawBoard(g2, BOARD_OFFSET_X, BOARD_OFFSET_Y, SQUARE_SIZE, boardFlipped, whiteToMove);
        drawCursor(g2);
        drawSelection(g2);
        drawSidebar(g2);
        drawStatusBar(g2);

        if (statusTimer > 0)
        {
            statusTimer--;
        }
    }

    private void drawMenu(Graphics2D g2)
    {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 35),
                WINDOW_WIDTH, WINDOW_HEIGHT, new Color(50, 40, 60));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Serif", Font.BOLD, 56));
        FontMetrics fm = g2.getFontMetrics();
        String title = "\u265A Chess \u2654";
        g2.drawString(title, (WINDOW_WIDTH - fm.stringWidth(title)) / 2, 140);

        g2.setColor(new Color(180, 180, 200));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fm = g2.getFontMetrics();
        String subtitle = "with Fog of War";
        g2.drawString(subtitle, (WINDOW_WIDTH - fm.stringWidth(subtitle)) / 2, 175);

        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        fm = g2.getFontMetrics();

        String[] options =
        {
            "1 - New Game (Fog of War)",
            "2 - Puzzle Mode",
            "3 - Standard Chess"
        };

        int startY = 280;
        for (int i = 0; i < options.length; i++)
        {
            int y = startY + i * 65;

            g2.setColor(new Color(60, 55, 75, 200));
            int boxW = 380;
            int boxX = (WINDOW_WIDTH - boxW) / 2;
            g2.fillRoundRect(boxX, y - 30, boxW, 50, 15, 15);

            g2.setColor(new Color(100, 90, 120));
            g2.drawRoundRect(boxX, y - 30, boxW, 50, 15, 15);

            g2.setColor(new Color(230, 230, 240));
            g2.drawString(options[i], (WINDOW_WIDTH - fm.stringWidth(options[i])) / 2, y);
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(new Color(150, 150, 170));
        fm = g2.getFontMetrics();

        String[] controls =
        {
            "Arrow Keys = Move cursor | Enter/Space = Select",
            "F = Toggle Fog | H = Hint (Puzzle) | ESC = Menu",
            "R = Restart | N = Next Puzzle"
        };

        int cy = 530;
        for (String line : controls)
        {
            g2.drawString(line, (WINDOW_WIDTH - fm.stringWidth(line)) / 2, cy);
            cy += 22;
        }

        g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
        g2.setColor(new Color(120, 120, 140));
        fm = g2.getFontMetrics();
        String credit = "CMSC132 Project - Justin Liao & Ganeshan Venu";
        g2.drawString(credit, (WINDOW_WIDTH - fm.stringWidth(credit)) / 2, WINDOW_HEIGHT - 30);

        drawPlayerStats(g2, 620);
    }

    private void drawPlayerStats(Graphics2D g2, int startY)
    {
        if (whitePlayer.getWins() == 0 && blackPlayer.getWins() == 0
                && whitePlayer.getPuzzlesSolved() == 0)
                {
            return;
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.setColor(new Color(200, 200, 210));
        FontMetrics fm = g2.getFontMetrics();
        String header = "Player Stats";
        g2.drawString(header, (WINDOW_WIDTH - fm.stringWidth(header)) / 2, startY);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.setColor(new Color(170, 170, 190));
        fm = g2.getFontMetrics();

        String wStats = "White - W:" + whitePlayer.getWins() + " L:" + whitePlayer.getLosses()
                + " Puzzles:" + whitePlayer.getPuzzlesSolved();
        String bStats = "Black - W:" + blackPlayer.getWins() + " L:" + blackPlayer.getLosses();
        g2.drawString(wStats, (WINDOW_WIDTH - fm.stringWidth(wStats)) / 2, startY + 22);
        g2.drawString(bStats, (WINDOW_WIDTH - fm.stringWidth(bStats)) / 2, startY + 42);
    }

    private void drawCursor(Graphics2D g2)
    {
        int displayRow = boardFlipped ? 7 - cursorRow : cursorRow;
        int displayCol = boardFlipped ? 7 - cursorCol : cursorCol;
        int sx = BOARD_OFFSET_X + displayCol * SQUARE_SIZE;
        int sy = BOARD_OFFSET_Y + displayRow * SQUARE_SIZE;

        g2.setColor(new Color(255, 255, 100, 100));
        g2.fillRect(sx, sy, SQUARE_SIZE, SQUARE_SIZE);

        g2.setColor(new Color(255, 255, 0));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(sx + 1, sy + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
    }

    private void drawSelection(Graphics2D g2)
    {
        if (!pieceSelected) return;

        int displayRow = boardFlipped ? 7 - selectedRow : selectedRow;
        int displayCol = boardFlipped ? 7 - selectedCol : selectedCol;
        int sx = BOARD_OFFSET_X + displayCol * SQUARE_SIZE;
        int sy = BOARD_OFFSET_Y + displayRow * SQUARE_SIZE;

        g2.setColor(new Color(0, 150, 255, 120));
        g2.fillRect(sx, sy, SQUARE_SIZE, SQUARE_SIZE);

        g2.setColor(new Color(0, 150, 255));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(sx + 1, sy + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
    }

    private void drawSidebar(Graphics2D g2)
    {
        int sideX = BOARD_OFFSET_X + SQUARE_SIZE * 8 + 25;
        int sideY = BOARD_OFFSET_Y;

        g2.setColor(new Color(55, 55, 65));
        g2.fillRoundRect(sideX, sideY, 195, SQUARE_SIZE * 8, 10, 10);
        g2.setColor(new Color(80, 80, 95));
        g2.drawRoundRect(sideX, sideY, 195, SQUARE_SIZE * 8, 10, 10);

        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.setColor(new Color(230, 230, 240));

        if (puzzleMode)
        {
            g2.drawString("Puzzle Mode", sideX + 15, sideY + 30);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(180, 180, 200));
            g2.drawString("Puzzle #" + (puzzleLoader.getCurrentPuzzle() + 1), sideX + 15, sideY + 55);
            g2.drawString("Solved: " + whitePlayer.getPuzzlesSolved(), sideX + 15, sideY + 75);

            g2.setColor(new Color(150, 150, 170));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("H = Hint", sideX + 15, sideY + 105);
            g2.drawString("N = Next Puzzle", sideX + 15, sideY + 122);
            g2.drawString("R = Retry", sideX + 15, sideY + 139);
        } else
        {
            String turnText = whiteToMove ? "White's Turn" : "Black's Turn";
            g2.drawString(turnText, sideX + 15, sideY + 30);

            if (gameState.isCheck && !gameState.isCheckmate)
            {
                g2.setColor(new Color(255, 100, 100));
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("CHECK!", sideX + 15, sideY + 55);
            }

            g2.setColor(new Color(200, 200, 210));
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString("Captured:", sideX + 15, sideY + 90);

            g2.setFont(new Font("Serif", Font.PLAIN, 22));
            List<Piece> bCap = board.getBlackCaptured();
            int cx = sideX + 15;
            int cy = sideY + 115;
            for (int i = 0; i < bCap.size(); i++)
            {
                g2.setColor(new Color(100, 100, 100));
                g2.drawString(bCap.get(i).getSymbol(), cx, cy);
                cx += 22;
                if (cx > sideX + 175)
                {
                    cx = sideX + 15;
                    cy += 25;
                }
            }

            cy += 35;
            cx = sideX + 15;
            List<Piece> wCap = board.getWhiteCaptured();
            for (int i = 0; i < wCap.size(); i++)
            {
                g2.setColor(new Color(220, 220, 220));
                g2.drawString(wCap.get(i).getSymbol(), cx, cy);
                cx += 22;
                if (cx > sideX + 175)
                {
                    cx = sideX + 15;
                    cy += 25;
                }
            }

            g2.setColor(new Color(150, 150, 170));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            int infoY = sideY + SQUARE_SIZE * 8 - 85;
            g2.drawString("F = Toggle Fog", sideX + 15, infoY);
            g2.drawString("R = Restart", sideX + 15, infoY + 17);
            g2.drawString("ESC = Menu", sideX + 15, infoY + 34);

            String fogStatus = board.isFogOfWar() ? "Fog: ON" : "Fog: OFF";
            g2.setColor(board.isFogOfWar() ? new Color(100, 200, 100) : new Color(200, 100, 100));
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(fogStatus, sideX + 15, infoY + 60);
        }
    }

    private void drawStatusBar(Graphics2D g2)
    {
        int barY = BOARD_OFFSET_Y + SQUARE_SIZE * 8 + 25;

        g2.setColor(new Color(50, 50, 60));
        g2.fillRoundRect(BOARD_OFFSET_X, barY, SQUARE_SIZE * 8, 35, 8, 8);

        if (statusMessage.length() > 0 && statusTimer > 0)
        {
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));

            if (statusMessage.contains("Checkmate") || statusMessage.contains("CHECK"))
            {
                g2.setColor(new Color(255, 100, 100));
            } else if (statusMessage.contains("Correct") || statusMessage.contains("wins"))
            {
                g2.setColor(new Color(100, 255, 100));
            } else if (statusMessage.contains("Incorrect"))
            {
                g2.setColor(new Color(255, 150, 50));
            } else
            {
                g2.setColor(new Color(200, 200, 220));
            }

            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(statusMessage,
                    BOARD_OFFSET_X + (SQUARE_SIZE * 8 - fm.stringWidth(statusMessage)) / 2,
                    barY + 23);
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(120, 120, 140));
        String pos = (char)('a' + cursorCol) + "" + (8 - cursorRow);
        g2.drawString(pos, BOARD_OFFSET_X + SQUARE_SIZE * 8 - 25, barY + 23);
    }

    public static void main(String[] args)
    {
        new ChessGame();
    }
}
