import java.awt.Point;
import java.awt.Graphics;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

public class World {

    public final int width = 800, height = 800; // 窗口宽、高
    public static final int FPS = 25; // 每秒帧数

    public double ALPHA = 1.0, BETA = 12.0, RHO = 0.1, Q = 10;  // 蚁群算法参数
    private double[][] Pheromone = new double[width][height];  // 信息素矩阵

    private Date start_time, finish_time;
    boolean ready = false, finish_catching = true;
    private Thief thief;
    private ArrayList<Robot> polices;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Point> four_points = new ArrayList<>();

    public World() {
        start_time = new Date();
        polices = new ArrayList<>();
        obstacles = new ArrayList<>();

        // 初始化信息素
        for (int i = 0; i < width; i++)
            for (int j = 0; j < width; j++)
                Pheromone[i][j] = 1.0;

        // 放置障碍物
        for (int i = 0; i < 6; i++) {
            obstacles.add(new Obstacle(new Point(20 + (100) * (i / 2), 100 + 200 * (i % 2))));
            obstacles.add(new Obstacle(new Point(20 + 2 * Obstacle.SIZE + 100 * (i / 2), 100 + 200 * (i % 2))));
            obstacles.add(new Obstacle(new Point(20 + 4 * Obstacle.SIZE + 100 * (i / 2), 100 + 200 * (i % 2))));
            obstacles.add(new Obstacle(new Point(20 + 100 * (i / 2), 100 + 2 * Obstacle.SIZE + 200 * (i % 2))));
            obstacles.add(new Obstacle(new Point(20 + 4 * Obstacle.SIZE + 100 * (i / 2), 100 + 2 * Obstacle.SIZE + 200 * (i % 2))));
//            obstacles.add(new Obstacle(new Point(600 + (10+4*Obstacle.SIZE) * (i / 3), 150 + 200 * (i % 3))));
        }

//        for (int i = 1; i <= 4; i++) {
//			for (int j = 1; j <= 4; j++) {
//                obstacles.add(new Obstacle(new Point(350+100 * i, 160 * j)));
//			}
//		}
//        for (int i = 1; i <= 3; i++) {
//            obstacles.add(new Obstacle(new Point(150, 100 * i + 50)));
//            obstacles.add(new Obstacle(new Point(250, 100 * i + 30)));
//            obstacles.add(new Obstacle(new Point(350, 100 * i + 20)));
//        }

        thief = new Thief(new Point(100, 50));
        polices.add(new Police(new Point(150, 150)));
//        polices.add(new Police(new Point(50, 150)));
        polices.add(new Police(new Point(250, 150)));
        polices.add(new Police(new Point(150, 350)));
        polices.add(new Police(new Point(250, 350)));

        four_points.add(new Point(thief.location.x, thief.location.y + 2 * Robot.SIZE));
        four_points.add(new Point(thief.location.x, thief.location.y - 2 * Robot.SIZE));
        four_points.add(new Point(thief.location.x - 2 * Robot.SIZE, thief.location.y));
        four_points.add(new Point(thief.location.x + 2 * Robot.SIZE, thief.location.y));
    }

    public void draw(Graphics a) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(a);
        }
        for (Robot police : polices) {
            police.draw(a);
        }
        thief.draw(a);
    }

    public double[][] getPheromone() {
        return Pheromone;
    }

    public void setPheromone(int x, int y, double pheromone) {
        Pheromone[x][y] = pheromone;
    }

    public void update() {
//        simple_allocate();
        safety_area();  // 安全域算法进行围捕
    }

    public void safety_area() {
        ready = if_ready();
        if (Finish_catching(this)) {
            if (finish_catching) {
                finish_time = new Date();
                System.out.println("用时共：" + (finish_time.getTime() - start_time.getTime()) / 1000.0 + "s");
                finish_catching = false;
            }
            return;
        }

        for (Robot police : polices) {
            police.update(this);
        }
        thief.update(this);

        if (ready)
            set_goal();
        else
            updateSafeArea();
    }

    public boolean if_ready() {
        for (Robot p : polices) {
            double distance = Point.distance(thief.location.x, thief.location.y, p.getLocation().x, p.getLocation().y);
            if (distance > 6 * Robot.SIZE)
                return false;
        }
        four_points.clear();
        four_points.add(new Point(thief.location.x, thief.location.y + 2 * Robot.SIZE));
        four_points.add(new Point(thief.location.x, thief.location.y - 2 * Robot.SIZE));
        four_points.add(new Point(thief.location.x - 2 * Robot.SIZE, thief.location.y));
        four_points.add(new Point(thief.location.x + 2 * Robot.SIZE, thief.location.y));
        return true;
    }

    private void updateSafeArea() {
        four_points.clear();
        four_points.add(new Point(thief.location.x + 4 * Robot.SIZE, thief.location.y + 4 * Robot.SIZE));
        four_points.add(new Point(thief.location.x - 4 * Robot.SIZE, thief.location.y + 4 * Robot.SIZE));
        four_points.add(new Point(thief.location.x + 4 * Robot.SIZE, thief.location.y - 4 * Robot.SIZE));
        four_points.add(new Point(thief.location.x - 4 * Robot.SIZE, thief.location.y - 4 * Robot.SIZE));

        Map<Point, ArrayList<Robot>> point_robot = new HashMap<Point, ArrayList<Robot>>();
        ArrayList<Point> t = (ArrayList<Point>) four_points.clone();
        ArrayList<Robot> p = (ArrayList<Robot>) polices.clone();
        while (t.size() > 0 || p.size() > 0) {
            double[][] dis = new double[p.size()][t.size()];
            point_robot.clear();
            for (int i = 0; i < p.size(); i++) {
                Robot police = p.get(i);
                double min_dis = width * 2;
                int index = -1;
                for (int j = 0; j < t.size(); j++) {
                    Point now = t.get(j);
                    dis[i][j] = Point.distance(police.getLocation().x, police.getLocation().y, now.x, now.y);
                    if (dis[i][j] <= min_dis) {
                        min_dis = dis[i][j];
                        index = j;
                    }
                }
                Point temp = t.get(index);
                ArrayList<Robot> pList;
                if (!point_robot.containsKey(temp)) {
                    pList = new ArrayList<Robot>();
                    point_robot.put(temp, pList);
                }
                pList = point_robot.get(temp);
                pList.add(police);
                point_robot.put(temp, pList);

            }
            for (Map.Entry<Point, ArrayList<Robot>> entry : point_robot.entrySet()) {
                ArrayList<Robot> pList = entry.getValue();
                Point goal = entry.getKey();
                int t_index = t.indexOf(goal);
                if (pList.size() == 1) {
                    four_points.set(polices.indexOf(pList.get(0)), goal);
                    t.remove(t_index);
                    p.remove(pList.get(0));
                } else {
                    double max = 0;
                    Robot worst_p = null;
                    for (Robot police : pList) {
                        int p_index = p.indexOf(police);
                        if (dis[p_index][t_index] > max) {
                            max = dis[p_index][t_index];
                            worst_p = police;
                        }
                    }
                    four_points.set(polices.indexOf(worst_p), goal);
                    t.remove(t_index);
                    p.remove(worst_p);
                }
            }
        }
    }

    // 下标分配
    public void simple_allocate() {
        ready = true;
        if (Finish_catching(this)) {
            if (finish_catching) {
                finish_time = new Date();
                System.out.println("用时共：" + (finish_time.getTime() - start_time.getTime()) / 1000.0 + "s");
                finish_catching = false;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                polices.get(i).update(this);
            }
            thief.update(this);
            set_goal();
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    Pheromone[i][j] *= (1 - RHO);
        }
    }

    // 下标分配
    private void set_goal() {
        four_points.clear();
        four_points.add(new Point(thief.location.x, thief.location.y + 2 * Robot.SIZE));
        four_points.add(new Point(thief.location.x, thief.location.y - 2 * Robot.SIZE));
        four_points.add(new Point(thief.location.x - 2 * Robot.SIZE, thief.location.y));
        four_points.add(new Point(thief.location.x + 2 * Robot.SIZE, thief.location.y));
    }

    public Point get_goal(Robot police) {
        return four_points.get(polices.indexOf(police));
    }

    private boolean Finish_catching(World world) {
        if (!ready)
            return false;
        for (int i = 0; i < 4; i++) {
            Point point = four_points.get(i);
            int x = point.x, y = point.y;
            if (!(x <= Robot.SIZE || y <= Robot.SIZE || x >= world.getWidth() - Robot.SIZE || y >= world.getHeight() - Robot.SIZE)) {
                if (!hit_area(x, y, world))
                    return false;
            }
        }
        return true;
    }

    public ArrayList<Robot> getPolices() {
        return polices;
    }

    public Robot getPolice(int i) {
        return polices.get(i);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Robot getThief() {
        return thief;
    }

    public static boolean hit_area(int x, int y, World world) {
        for (int i = 0; i < world.getPolices().size(); i++) {
            int x1 = world.getPolice(i).getLocation().x;
            int y1 = world.getPolice(i).getLocation().y;
            if (Point.distance(x1, y1, x, y) < 2 * Robot.SIZE) {
                return true;
            }
        }
        return false;
    }

}