import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

class TronGameGUITest {

    @Test
    void testPlayerInitialization() {
        Player player = new Player("Aqsa", Color.RED, new Point(20, 30), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        assertEquals("Aqsa", player.getName());
        assertEquals(Color.RED, player.getColor());
        assertEquals(new Point(20, 30), player.getPosition());
    }

    @Test
    void testPlayerMovementUp() {
        Player player = new Player("Sana", Color.BLUE, new Point(10, 10), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        player.setDirection(KeyEvent.VK_W, player.getControls());
        player.move();
        assertEquals(new Point(10, 9), player.getPosition());
    }

    @Test
    void testPlayerMovementRight() {
        Player player = new Player("Tayyaba", Color.BLUE, new Point(10, 10), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        player.setDirection(KeyEvent.VK_D, player.getControls());
        player.move();
        assertEquals(new Point(11, 10), player.getPosition());
    }

    @Test
    void testCollisionWithBoundary() {
        TronGameGUI game = new TronGameGUI();
        Player player = new Player("Muqaddas", Color.RED, new Point(0, 0), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        player.setDirection(KeyEvent.VK_A, player.getControls());
        player.move();
        boolean inBounds = game.isInBounds(player.getPosition());
        assertFalse(inBounds);
    }

    @Test
    void testCollisionWithOtherPlayerTrail() {
        TronGameGUI game = new TronGameGUI();
        game.lightTrail1.add(new Point(10, 10));

        Player player2 = new Player("Meer", Color.BLUE, new Point(10, 10), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D});
        game.player2 = player2;
        game.checkCollisions();
        assertFalse(game.running);
    }


    @Test
    void testObstacleCollision() {
        TronGameGUI game = new TronGameGUI();
        game.obstacles[5][5] = true;
        game.player1 = new Player("Mansoor", Color.RED, new Point(5, 5), new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D}); // Set player1
        assertTrue(game.running, "Game should be running initially");
        game.checkCollisions();
        assertFalse(game.running, "Game should stop running when player1 collides with an obstacle");
    }



    @Test
    void testLightTrailAddition() {
        TronGameGUI game = new TronGameGUI();
        game.lightTrail1.add(new Point(15, 15));
        game.lightTrail1.add(new Point(16, 15));
        assertEquals(2, game.lightTrail1.size());
        assertEquals(new Point(16, 15), game.lightTrail1.get(1));
    }

    @Test
    void testRestartGame() {
        TronGameGUI game = new TronGameGUI();
        game.running = false;
        game.restartGame();
        assertTrue(game.running);
        assertEquals(0, game.lightTrail1.size());
        assertEquals(0, game.lightTrail2.size());
    }
}
