import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;

public class Obstacle {

    private Point location;
    public static final int SIZE = 15;

    public Obstacle(Point l) {
        location = l;
    }

    public void draw(Graphics a) {
        a.setColor(Color.BLACK);
        a.fillRect(location.x - SIZE, location.y - SIZE, SIZE * 2, SIZE * 2);
    }

    public Point getLocation() {
        return location;
    }

}