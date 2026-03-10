import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class Game extends Canvas
{

    private boolean isRunning;
    private int width;
    private int height;

    public Game(String title, int width, int height)
    {
        this.width = width;
        this.height = height;
        this.isRunning = true;

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        this.setPreferredSize(new Dimension(width, height));
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.setFocusable(true);
        this.requestFocusInWindow();

        Timer timer = new Timer(33, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
            }
        });
        timer.start();
    }

    public int getWindowWidth()
    {
        return width;
    }

    public int getWindowHeight()
    {
        return height;
    }

    @Override
    public abstract void paint(Graphics brush);
}
