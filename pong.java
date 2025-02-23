import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class demo2 extends JPanel implements KeyListener, Runnable {
    private int paddle1Y = 150, paddle2Y = 150;
    private int ballX = 250, ballY = 50;
    private int ballXSpeed = 4, ballYSpeed = 4;
    private final int PADDLE_WIDTH = 10, PADDLE_HEIGHT = 150, NET_HEIGHT = 1080;
    private final int BALL_SIZE = 40;
    private boolean up1 = false, down1 = false, up2 = false, down2 = false;
    private int score1 = -1, score2 = -1;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    public demo2() {
        setPreferredSize(new Dimension(1920, 1080));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameStarted) { // Display the start screen
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            g.drawString("Press ENTER to Start", getWidth() / 2 - 180, getHeight() / 2);

            return;
        }
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            String winner = score1 == 5 ? "Player 1 Wins!" : "Player 2 Wins!";
            g.drawString(winner, getWidth() / 2 - 150, getHeight() / 2);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Press R to Restart", getWidth() / 2 - 120, getHeight() / 2 + 50);
            return;
        }

        g.setColor(Color.BLUE);
        g.fillRect(20, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.setColor(Color.RED);
        g.fillRect(getWidth() - 30, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.setColor(Color.WHITE);
        g.fillRect(getWidth() / 2, 0, PADDLE_WIDTH, NET_HEIGHT);
        g.setColor(Color.WHITE);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        g.drawString("Player 1: " + score1, getWidth() / 4, 50);
        g.drawString("Player 2: " + score2, getWidth() * 3 / 4, 50);
    }

    public void updateGame() {
        if (gameOver)
            return; // Stop updating the game if it's over

        if (up1 && paddle1Y > 0)
            paddle1Y -= 4;
        if (down1 && paddle1Y < getHeight() - PADDLE_HEIGHT)
            paddle1Y += 4;
        if (up2 && paddle2Y > 0)
            paddle2Y -= 4;
        if (down2 && paddle2Y < getHeight() - PADDLE_HEIGHT)
            paddle2Y += 4;

        ballX += ballXSpeed;
        ballY += ballYSpeed;

        // Ball collision with top and bottom walls
        if (ballY <= 0 || ballY >= getHeight() - BALL_SIZE)
            ballYSpeed = -ballYSpeed;

        // Ball collision with paddles
        if (ballX <= 30 && ballY >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT)
            ballXSpeed = -ballXSpeed;
        if (ballX >= getWidth() - 40 && ballY >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT)
            ballXSpeed = -ballXSpeed;

        // Scoring logic
        if (ballX <= 0) {
            score2++;
            checkGameOver();
        } else if (ballX >= getWidth()) {
            score1++;
            checkGameOver();
        }
    }

    private void checkGameOver() {
        if (score1 == 5 || score2 == 5) {
            gameOver = true;
        } else {
            resetBall();
        }
    }

    private void resetBall() {
        ballX = getWidth() / 2 - BALL_SIZE / 2;
        ballY = getHeight() / 2 - BALL_SIZE / 2;
        ballXSpeed = (Math.random() > 0.5) ? 5 : -5;
        ballYSpeed = (Math.random() > 0.5) ? 5 : -5;
    }

    private void restartGame() {
        score1 = 0;
        score2 = 0;
        paddle1Y = 150;
        paddle2Y = 150;
        gameOver = false;
        resetBall();
        repaint();
    }

    @Override
    public void run() {
        while (true) {
            updateGame();
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W)
            up1 = true;
        if (e.getKeyCode() == KeyEvent.VK_S)
            down1 = true;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            up2 = true;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down2 = true;
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver)
            restartGame();
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !gameStarted) { // Start the game when 'S' is pressed
            gameStarted = true;
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W)
            up1 = false;
        if (e.getKeyCode() == KeyEvent.VK_S)
            down1 = false;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            up2 = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down2 = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PONG GAME");
        demo2 p = new demo2();
        frame.add(p);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread gameThread = new Thread(p);
        gameThread.start();
    }
}
