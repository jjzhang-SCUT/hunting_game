import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame f = new Annimation(new World());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
