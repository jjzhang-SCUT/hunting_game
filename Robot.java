import java.lang.Math;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;

public abstract class Robot {

    public static final int SIZE = 10;
    public static int speed = 10;
    protected Point location;
    protected int direction = 0;

    public Robot(Point initial_location) {
        if (this.getColor() == Color.RED)
            speed *= 1.1;
        location = initial_location;
    }

    public void draw(Graphics a) {
        a.setColor(this.getColor());
        a.fillOval(location.x - SIZE, location.y - SIZE, 2 * SIZE, 2 * SIZE);
    }

    protected void move(World world) {
        Point goal = this.get_goal(world);
        direction = this.get_direction(goal);
        Point next = new Point((int) (location.x + speed * Math.cos((double) direction * Math.PI / 180.0)),
                (int) (location.y - speed * Math.sin((double) direction * Math.PI / 180.0)));
        for (int i = 0; i < 4; i++) {
            boolean wall = (next.x <= Robot.SIZE || next.y <= Robot.SIZE || next.x >= world.width - Robot.SIZE
                    || next.y >= world.height - Robot.SIZE);
            if (!wall && !has_collision(next.x, next.y, world)) {
                setLocation(next);
                break;
            }
            if (wall) {
                direction -= 90;
                format_direction();
            }
            next.x = (int) (location.x + speed * Math.cos((double) direction * Math.PI / 180.0));
            next.y = (int) (location.y - speed * Math.sin((double) direction * Math.PI / 180.0));
        }
    }

    abstract protected Point get_goal(World world);

    abstract protected int get_direction(Point goal);

    public void update(World world) {
        move(world);
    }

    abstract protected void change_direction(int x, int y, World p, boolean hit_obstacle);

    private boolean has_collision(int x, int y, World world) {
        // 障碍物
        for (int i = 0; i < world.getObstacles().size(); i++) {
            int x1 = world.getObstacles().get(i).getLocation().x, y1 = world.getObstacles().get(i).getLocation().y;
            if (Point.distance(x1, y1, x, y) < Obstacle.SIZE + Robot.SIZE) {
                this.change_direction(x1, y1, world, true);
                return true;
            }
        }

        int index;
        if (this.getColor() == Color.BLUE) {
            for (index = 0; index < world.getPolices().size(); index++) {
                if (location != world.getPolice(index).getLocation()) {
                    if (Point.distanceSq(x, y, world.getPolice(index).getLocation().x, world.getPolice(index).getLocation().y) <= 4 * SIZE * SIZE) {
                        this.change_direction(world.getPolice(index).getLocation().x, world.getPolice(index).getLocation().y, world, false);
                        return true;
                    }
                }
            }
            return Point.distanceSq(x, y, world.getThief().getLocation().x, world.getThief().getLocation().y) <= 4 * SIZE * SIZE;
        } else {
            for (index = 0; index < 4; index++) {
                if (Point.distanceSq(x, y, world.getPolice(index).getLocation().x, world.getPolice(index).getLocation().y) <= 4 * SIZE * SIZE) {
                    this.change_direction(x, y, world, false);
                    return true;
                }
            }
        }
        return false;
    }

    public Color getColor() {
        return Color.yellow;
    }

    public Point getLocation() {
        return location;
    }

    public void format_direction() {
        if (direction > 180)
            direction -= 360;
        if (direction <= -180)
            direction += 360;
    }

    public void setLocation(Point p) {
        location = p;
    }
}