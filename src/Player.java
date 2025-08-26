import java.awt.Color;
import java.awt.Point;

public class Player {
    private final String name;
    private final Color color;
    private final Point position;
    private final int[] controls;
    private int direction;

    public Player(String name, Color color, Point startPosition, int[] controls) {
        this.name = name;
        this.color = color;
        this.position = startPosition;
        this.controls = controls;
        this.direction = 0;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Point getPosition() {
        return position;
    }

    public void move() {
        switch (direction) {
            case 0 -> position.translate(0, -1);
            case 1 -> position.translate(-1, 0);
            case 2 -> position.translate(0, 1);
            case 3 -> position.translate(1, 0);
        }
    }

    public int[] getControls() {
        return controls;
    }

    public void setDirection(int keyCode, int[] controls) {
        for (int i = 0; i < controls.length; i++) {
            if (keyCode == controls[i]) {
                direction = i;
                break;
            }
        }
    }

}
