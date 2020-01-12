import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class Police extends Robot {

    public Police(Point loc) {
        super(loc);
    }

    protected Point get_goal(World world) {
        return world.get_goal(this);
    }

    protected int get_direction(Point goal) {
        int del_x = goal.x - location.x, del_y = goal.y - location.y;
        int direction = (int) (Math.acos(del_x / Math.sqrt(del_x * del_x + del_y * del_y)) * 180 / Math.PI);
        if (del_y > 0)
            direction = -direction;
        format_direction();
        return direction;
    }

    @Override
    public void update(World world) {
        move(world);
        double last_pheromone = world.getPheromone()[this.location.x][this.location.y];
        world.setPheromone(this.location.x, this.location.y, last_pheromone * 0.0001);  // 更新信息素
    }

    @Override
    protected void change_direction(int x, int y, World world, boolean hit_obstacle) {
        if (!hit_obstacle) {
            turn_left(x, y);
        } else {
//			human_field(x, y, world); //人工势场法
//            turn_left(x, y); //左转
            ant(world); // 蚁群
//            improved_human_field(x, y, world); // 改进人工势场
        }
    }

    // 蚁群
    private void ant(World world) {
        int[] degrees = new int[8];
        double[] weights = new double[8];
        double sum = 0;
        double target_degree = this.get_direction(this.get_goal(world));
        for (int i = 0; i < 8; i++) {
            degrees[i] = 45 * (i % 2) * 6 + (1 - i % 2) * i;
//            degrees[i] = 45 * i;
            weights[i] = Math.pow(world.getPheromone()[this.location.x][this.location.y], world.ALPHA) *
                    Math.pow((Math.cos(degrees[i] - target_degree) + 1) / 2, world.BETA);  // 选择此方向的概率
            sum += weights[i];
        }
        for (int i = 0; i < 8; i++)
            weights[i] /= sum;

        double total = new Random().nextDouble() - weights[0];
        int index = 0;
        while (total > 0) {
            total -= weights[++index];
        }
        this.direction = degrees[index];

        Point result = new Point((int) (this.location.x + this.speed * Math.cos((double) this.direction * Math.PI / 180.0)),
                (int) (this.location.y - this.speed * Math.sin((double) this.direction * Math.PI / 180.0)));
    }

    // 左转90度
    private void turn_left(int x, int y) {
        int del_x = x - this.location.x, del_y = y - this.location.y;
        int direction = (int) (Math.acos(del_x / Math.sqrt(del_x * del_x + del_y * del_y)) * 180 / Math.PI);
        if (del_y > 0)
            direction = -direction;
        direction += 90;
        format_direction();
        this.direction = direction;
    }

    // 人工势场
    private void human_field(int x, int y, World world) {
        Point goal = world.get_goal(this);
        double x1 = this.location.x - x, y1 = this.location.y - y, x2 = goal.x - x, y2 = goal.y - y;
        this.direction = (int) Math.atan2(10 * y1 + y2 + y, 10 * x1 + x2 + x);
    }

    // 改进人工势场
    private void improved_human_field(int x, int y, World world) {
        Point goal = world.get_goal(this);
        double x1 = this.location.x - x, y1 = this.location.y - y, x2 = goal.x - x, y2 = goal.y - y;
        double vx, vy;
        int rx = (int) (Math.random() * 100), ry = (int) (Math.random() * 100);
        double a = 1;
        if (x2 > 40 * Robot.SIZE) {
            x2 = 40 * Robot.SIZE;
        }
        if (y2 > 40 * Robot.SIZE) {
            y2 = 40 * Robot.SIZE;
        }
        if (x2 + y2 > 80 * Robot.SIZE) {
            a = 0.05;
        } else if (x2 + y2 > 50 * Robot.SIZE) {
            a = 0.2;
        } else if (x2 + y2 > 30 * Robot.SIZE) {
            a = 0.4;
        }

        if (x1 < 0.5 * Robot.SIZE || y1 < 0.5 * Robot.SIZE) {
            this.direction = (int) (Math.random() * 360);
        } else {
            vx = 15 * x1 + Math.pow(x2, a) + x + rx;
            vy = 15 * y1 + Math.pow(y2, a) + y + ry;
            this.direction = (int) Math.atan2(vy, vx);
        }
    }

    public Color getColor() {
        return Color.BLUE;
    }
}