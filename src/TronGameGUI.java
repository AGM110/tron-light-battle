import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class TronGameGUI extends JPanel implements ActionListener {
    private final int BOARD_WIDTH = 800;
    private final int BOARD_HEIGHT = 600;
    private final int TILE_SIZE = 10;
    private final Timer timer;
    protected Player player1;
    protected Player player2;
    protected final ArrayList<Point> lightTrail1;
    protected final ArrayList<Point> lightTrail2;
    private final DatabaseManager dbManager;
    protected boolean running;
    private int currentLevel;
    protected boolean[][] obstacles;
    private boolean player1Lost;
    private boolean player2Lost;
    private int gameSpeed;
    private long startTime;
    private long elapsedTime;
    private boolean isGamePaused = false;
    private HighScoreTable highScoreTable = new HighScoreTable();

    public TronGameGUI() {
        dbManager = new DatabaseManager();


        String name1 = JOptionPane.showInputDialog("Enter Player 1 Name:");
        String name2 = JOptionPane.showInputDialog("Enter Player 2 Name:");
        Color color1 = JColorChooser.showDialog(null, "Choose Player 1 Color", Color.RED);
        Color color2 = JColorChooser.showDialog(null, "Choose Player 2 Color", Color.BLUE);


        dbManager.addPlayer(name1);
        dbManager.addPlayer(name2);


        player1 = new Player(name1, color1, new Point(20, 30), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        player2 = new Player(name2, color2, new Point(60, 30), new int[]{KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT});

        lightTrail1 = new ArrayList<>();
        lightTrail2 = new ArrayList<>();
        running = true;
        currentLevel = 1;
        obstacles = new boolean[BOARD_WIDTH / TILE_SIZE][BOARD_HEIGHT / TILE_SIZE];
        player1Lost = false;
        player2Lost = false;


        gameSpeed = 100;
        timer = new Timer(gameSpeed, this);
        timer.start();
        startTime = System.currentTimeMillis();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player1.setDirection(e.getKeyCode(), player1.getControls());
                player2.setDirection(e.getKeyCode(), player2.getControls());
            }
        });
        setFocusable(true);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);

        createMenu();
        generateLevel(currentLevel);
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            setupWindowListener(topFrame);
        }
    }

    public void generateLevel(int level) {
        if (level > 10) {
            level = 10;
        }

        for (int i = 0; i < obstacles.length; i++) {
            for (int j = 0; j < obstacles[i].length; j++) {
                obstacles[i][j] = false;
            }
        }

        Random rand = new Random();

        int numberOfObstacles = Math.max(10, level * 5);
        for (int i = 0; i < numberOfObstacles; i++) {
            int x = rand.nextInt(BOARD_WIDTH / TILE_SIZE);
            int y = rand.nextInt(BOARD_HEIGHT / TILE_SIZE);
            while (obstacles[x][y] || (new Point(x, y).equals(player1.getPosition())) || (new Point(x, y).equals(player2.getPosition()) || lightTrail1.contains(new Point(x, y)) || lightTrail2.contains(new Point(x, y)))) {
                x = rand.nextInt(BOARD_WIDTH / TILE_SIZE);
                y = rand.nextInt(BOARD_HEIGHT / TILE_SIZE);
            }
            obstacles[x][y] = true;
        }

        gameSpeed = Math.max(50, 100 - (level * 5));
        timer.setDelay(gameSpeed);
    }

    public JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu highScoresMenu = new JMenu("High Scores");
        JMenuItem viewHighScores = new JMenuItem("View High Scores");
        viewHighScores.addActionListener(e -> {
            isGamePaused = true;
            JOptionPane.showMessageDialog(this, dbManager.getHighScores());
            isGamePaused = false;
        });
        highScoresMenu.add(viewHighScores);

        JMenu gameMenu = new JMenu("Restart Game");
        JMenuItem restartGame = new JMenuItem("Restart Game");
        restartGame.addActionListener(e -> restartGame());
        gameMenu.add(restartGame);
        menuBar.add(highScoresMenu);
        menuBar.add(gameMenu);

        return menuBar;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            drawPlayer(g, player1, lightTrail1);
            drawPlayer(g, player2, lightTrail2);

            // Display obstacles
            g.setColor(Color.GREEN);
            for (int i = 0; i < obstacles.length; i++) {
                for (int j = 0; j < obstacles[i].length; j++) {
                    if (obstacles[i][j]) {
                        g.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Level: " + currentLevel, 20, 40);
            elapsedTime = System.currentTimeMillis() - startTime;
            long seconds = (elapsedTime / 1000) % 60;
            long minutes = (elapsedTime / 60000) % 60;
            String timeString = String.format("Time: %02d:%02d", minutes, seconds);
            g.drawString(timeString, BOARD_WIDTH - 150, 40);

        } else {
            timer.stop();

            if (currentLevel >= 10) {
                String winner = determineWinner();
                dbManager.updateWins(winner);
                JOptionPane.showMessageDialog(this, winner + " wins! You've completed 10 levels, congrats! Game finished");
                System.exit(0);
            } else {
                String winner = determineWinner();
                dbManager.updateWins(winner);
                JOptionPane.showMessageDialog(this, winner + " wins!");
                int response = JOptionPane.showConfirmDialog(this, "Proceed to next level?", "Next Level", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION && currentLevel < 10) {
                    currentLevel++;
                    generateLevel(currentLevel);
                    restartGame();
                } else {
                    System.exit(0);
                }
            }
        }
    }

public void startGameTimer() {
    startTime = System.currentTimeMillis();
}

  private void showHighScores() {
        List<Score> topScores = highScoreTable.getTopScores();
        StringBuilder sb = new StringBuilder();
        sb.append("Top 10 High Scores:\n");

        for (int i = 0; i < topScores.size(); i++) {
            sb.append(i + 1).append(". ")
                    .append(topScores.get(i).getPlayerName()).append(": ")
                    .append(topScores.get(i).getScore()).append("\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    public void drawPlayer(Graphics g, Player player, ArrayList<Point> trail) {
        g.setColor(player.getColor());
        for (Point p : trail) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        g.fillRect(player.getPosition().x * TILE_SIZE, player.getPosition().y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    public void checkCollisions() {
        if (!isInBounds(player1.getPosition()) || lightTrail2.contains(player1.getPosition()) || obstacles[player1.getPosition().x][player1.getPosition().y]) {
            running = false;
            player1Lost = true;
        }

        if (!isInBounds(player2.getPosition()) || lightTrail1.contains(player2.getPosition()) || obstacles[player2.getPosition().x][player2.getPosition().y]) {
            running = false;
            player2Lost = true;
        }
    }

    public boolean isInBounds(Point position) {
        return position.x >= 0 && position.x < BOARD_WIDTH / TILE_SIZE && position.y >= 0 && position.y < BOARD_HEIGHT / TILE_SIZE;
    }

    public String determineWinner() {
        if (player1Lost) {
            return player2.getName();
        }
        if (player2Lost) {
            return player1.getName();
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGamePaused && running) {
            player1.move();
            player2.move();
            lightTrail1.add(new Point(player1.getPosition()));
            lightTrail2.add(new Point(player2.getPosition()));
            checkCollisions();

            elapsedTime = System.currentTimeMillis() - startTime;
        }
        repaint();
    }


    public void restartGame() {

        player1 = new Player(player1.getName(), player1.getColor(), new Point(20, 30), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        player2 = new Player(player2.getName(), player2.getColor(), new Point(60, 30), new int[]{KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT});

        lightTrail1.clear();
        lightTrail2.clear();
        player1Lost = false;
        player2Lost = false;
        running = true;
        timer.start();
        repaint();
    }


    public static void setupWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                askToCloseGame(frame);
            }
        });
    }

  public static void askToCloseGame(JFrame frame) {
      int response = JOptionPane.showConfirmDialog(frame,
              "Do you want to close this game?", "Exit Game",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.YES_OPTION) {
          frame.dispose();
          System.exit(0);
      } else {
          frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      }
  }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Tron Game");
        TronGameGUI gamePanel = new TronGameGUI();

        frame.add(gamePanel);
        frame.setJMenuBar(gamePanel.createMenu());
        frame.setSize(gamePanel.getPreferredSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        setupWindowListener(frame);
        gamePanel.startGameTimer();
    }
}





