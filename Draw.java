import java.awt.*;
import javax.swing.*;


public class Draw extends JPanel {
    private World my_world;

    public Draw(World world) {
        my_world = world;
        setPreferredSize(new Dimension(my_world.getWidth(), my_world.getHeight()));
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
    }

    public void paintComponent(Graphics a) {
        super.paintComponent(a);
        my_world.draw(a);
    }
}

 