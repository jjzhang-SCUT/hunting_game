import java.awt.Color;
import java.awt.Point;

public class Thief extends Robot {

    public Thief(Point initial_location) {
        super(initial_location);
    }

    @Override
    protected int get_direction(Point goal) {
        if (Math.random() < 0.2 && goal != null) {
            int vx = goal.x - location.x, vy = goal.y - location.y;
            double hh = Math.sqrt(vx * vx + vy * vy);
            direction = (int) Math.acos((vx / hh) * 180 / Math.PI);
            if (vy >= 0) {
                direction = -direction;
            }
        } else {
            int turn = (int) (Math.random() * 10); // 转的方向
            direction += ((int) (Math.random() * 90)) * ((turn % 2) * 2 - 1);
            format_direction();
        }
        return direction;
    }

    protected void change_direction(int x, int y, World world, boolean hit_obstacle) {
        this.direction += 90;
        format_direction();
    }

    @Override
    protected Point get_goal(World world) {
        double min_dis = world.getHeight() * 2;
        int temp = -1;
        for (int i = 0; i < 4; i++) {
            Police now = (Police) world.getPolice(i);
            double now_dis = Point.distance(location.x, location.y, now.getLocation().x, getLocation().y);
            if (now_dis <= min_dis) {
                min_dis = now_dis;
                temp = i;
            }
        }
        return world.getPolice(temp).location;
    }

    public Color getColor() {
        return Color.RED;
    }
}
