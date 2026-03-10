public class Player
{

    private String name;
    private boolean isWhite;
    private int wins;
    private int losses;
    private int puzzlesSolved;

    public Player(String name, boolean isWhite)
    {
        this.name = name;
        this.isWhite = isWhite;
        this.wins = 0;
        this.losses = 0;
        this.puzzlesSolved = 0;
    }

    public String getName()
    {
        return name;
    }

    public boolean isWhite()
    {
        return isWhite;
    }

    public int getWins()
    {
        return wins;
    }

    public int getLosses()
    {
        return losses;
    }

    public int getPuzzlesSolved()
    {
        return puzzlesSolved;
    }

    public void addWin()
    {
        wins++;
    }

    public void addLoss()
    {
        losses++;
    }

    public void addPuzzleSolved()
    {
        puzzlesSolved++;
    }

    @Override
    public String toString()
    {
        return name + " (" + (isWhite ? "White" : "Black") + ")";
    }
}
