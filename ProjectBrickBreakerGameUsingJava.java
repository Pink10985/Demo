package com.project.brickbreakergame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class BrickMap {
    public int[][] brickLayout;
    public int brickWidth;
    public int brickHeight;

    // Constructor for the brick layout
    public BrickMap(int rows, int columns) {
        brickLayout = new int[rows][columns];
        for (int[] brickLayout1 : brickLayout) {
            for (int j = 0; j < brickLayout[0].length; j++) {
                brickLayout1[j] = 1; 
               // 1 means the brick is present
            }
        }
        brickWidth = 540 / columns;
        brickHeight = 150 / rows;
    }

    // Draws the bricks in the panel
    public void draw(Graphics2D g) {
        for (int i = 0; i < brickLayout.length; i++) {
            for (int j = 0; j < brickLayout[0].length; j++) {
                if (brickLayout[i][j] > 0) { 
                    g.setColor(new Color(0xFFAA33)); 
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    // Draw brick border
                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    // Updates the brick value and sets the value to 0 if the brick is hit
    public void setBrickValue(int value, int row, int col) {
        brickLayout[row][col] = value;
    }

    // Resets the brick map to initial state
    public void resetBrickMap() {
        for (int[] brickLayout1 : brickLayout) {
            for (int j = 0; j < brickLayout[0].length; j++) {
                brickLayout1[j] = 1; 
                // Reset all bricks to 1 (present)
            }
        }
    }
}
/**
 * Documentation for the following code:
 * GamePanel Class: mainly responsible for handling the core mechanism of the Game play
 * 
 * It extends with KeyListener, ActionListener, MouseListener and MouseMotionListener to handle
 * user inputs and event in the game.
 * 
 * This class also manages whether the game is being played or over and also I've include a hover 
 * effect on the Start button which this class is able to manage it.
 */
class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    private boolean isPlaying = false; 
    private boolean isGameOver = false;
    private boolean isHoveringStart = false;
    private int playerScore = 0; 
    private int totalBricks = 21;
    private final Timer timer; 
    private final int delay = 8;
    private int playerX = 310;
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballXDir = -1; 
    private int ballYDir = -2;
    private final BrickMap brickMap;

   /**
    * Constructor for the GamePanel Class which initializes a BrickMap with 3 rows and 7 columns
    * 
    * This constructor also handles keyboard input, MouseListener and 
    * MouseMotionListener to track mouse click and movements
    */
    public GamePanel() {
        brickMap = new BrickMap(3, 7); 
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // Paints the game components
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isPlaying && !isGameOver) {
            drawStartScreen(g); 
        } else {
            // Drawing game elements
            g.setColor(Color.BLACK);
            g.fillRect(1, 1, 692, 592); 
            brickMap.draw((Graphics2D) g);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Serif", Font.BOLD, 25));
            g.drawString("Score: " + playerScore, 590, 30);
            g.setColor(Color.GREEN);
            g.fillRect(playerX, 550, 100, 8);
            g.setColor(Color.YELLOW);
            g.fillOval(ballPosX, ballPosY, 20, 20); 

            // Checks whether the user has won or not if yes the message is displayed
            if (totalBricks <= 0) {
                isGameOver = true;
                endGame(g, "You Won! Score: " + playerScore);
            }

            // Checks whether the game is over or not if yes then message is displayed
            if (ballPosY > 570) {
                isGameOver = true;
                endGame(g, "Game Over, Score: " + playerScore);
            }
        }

        g.dispose();
    }

    // Draws the start screen with the "Start" button
    public void drawStartScreen(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Brick Breaker Game", 100, 200);

        if (isHoveringStart) {
            g2d.setColor(Color.CYAN); 
        } else {
            g2d.setColor(Color.WHITE);
        }

        g2d.setFont(new Font("Arial", Font.PLAIN, 30));
        g2d.drawRect(250, 300, 200, 60); 
        g2d.drawString("Start", 320, 340); 
    }

    // Draws the game over screen
    public void endGame(Graphics g, String message) {
        ballXDir = 0;
        ballYDir = 0;
        g.setColor(new Color(0XFF6464));
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(message, 190, 300); 
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Press Enter to Restart.", 230, 350); 
    }

    // Resets the game when player presses Enter
    public void resetGame() {
        isPlaying = true;
        isGameOver = false;
        playerScore = 0;
        totalBricks = 21;
        brickMap.resetBrickMap();
        ballPosX = 120;
        ballPosY = 350;
        ballXDir = -1;
        ballYDir = -2;
        playerX = 310;
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPlaying && !isGameOver) {
            timer.start(); 

            // Ball-paddle collision logic
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYDir = -ballYDir; 
            }

            // This part handles the logic behind the ball and brick collision
            A: for (int i = 0; i < brickMap.brickLayout.length; i++) {
                for (int j = 0; j < brickMap.brickLayout[0].length; j++) {
                    if (brickMap.brickLayout[i][j] > 0) {
                        int brickX = j * brickMap.brickWidth + 80;
                        int brickY = i * brickMap.brickHeight + 50;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickMap.brickWidth, brickMap.brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            brickMap.setBrickValue(0, i, j); 
                            totalBricks--; 
                            playerScore += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                            break A; 
                        }
                    }
                }
            }

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            if (ballPosX < 0 || ballPosX > 670) ballXDir = -ballXDir; 
                        if (ballPosY < 0) ballYDir = -ballYDir; 

            // Repaint game elements
            repaint();
        }
    }

    //Handles the bar movement and is responsible for restarting the game
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) playerX = 600; 
            else moveRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) playerX = 10;
            else moveLeft();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // If the game is over and the user hits Enter then the game restarts
            if (isGameOver) {
                resetGame();
            }
        }
    }

    // Method to move the bar to the right
    public void moveRight() {
        isPlaying = true; 
        playerX += 50; 
    }

    // Method to move the bar to the left
    public void moveLeft() {
        isPlaying = true;
        playerX -= 50;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Required for KeyListener
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Required for KeyListener
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Starts the screen when the user clicks on the Start Button on the start screen
        if (e.getX() >= 250 && e.getX() <= 450 && e.getY() >= 300 && e.getY() <= 360) {
            isPlaying = true;
            timer.start();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //Start Button hovering 
        isHoveringStart = e.getX() >= 250 && e.getX() <= 450 && e.getY() >= 300 && e.getY() <= 360;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Required for MouseListener
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //Required for MouseListener
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Required for MouseListener
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //Required for MouseListener
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Required for MouseMotionListener
    }
}

public class ProjectBrickBreakerGameUsingJava {
    public static void main(String[] args) {
        // Setting up the JFrame window for the game
        JFrame frame = new JFrame();
        GamePanel gamePanel = new GamePanel();
        // Window size and position
        frame.setBounds(10, 10, 700, 600); 
        frame.setTitle("Brick Breaker Game"); 
        frame.setResizable(false); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.add(gamePanel); 
        frame.setVisible(true); 
    }
}


