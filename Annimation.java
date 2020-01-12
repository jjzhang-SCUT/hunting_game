import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Annimation extends JFrame {
    private Timer timer;
    private World points;
    private Draw drawing;

    public Annimation(World world) {
        points = world;
        drawing = new Draw(world);
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        getContentPane().add(drawing);
        pack();
        setResizable(false);
        timer = new Timer(1000 / World.FPS, new Ticker());
        timer.start();
    }

    private class Ticker implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            points.update(); // 每帧更新
            drawing.repaint();
        }
    }
}