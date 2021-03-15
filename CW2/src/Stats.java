import java.awt.*;
import javax.swing.*;

public class Stats extends JPanel {
    private JFrame f;

    public Stats() {
        f = new JFrame("Distribution of ages");
        int[] inputData = {1, 2, 3, 5, 10, 11, 20};
        f.setLayout(new GridLayout(1, 0, 10, 0));
        /*
        for (int val: inputData){
            JLabel l = addColumn(val,Color.CYAN, 50, val * 5);
            f.add(l);
        }
        f.pack();
        f.setVisible(true);

         */
    }

    /*private JLabel addColumn(int value, Color color, int width, int height){
        Icon i = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                // 5 is the spacing between the bars
                g.fillRect(x, y, width - 5, height);
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
        JLabel l = new JLabel(String.valueOf(value));
        l.setHorizontalTextPosition(JLabel.CENTER);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setVerticalTextPosition(JLabel.TOP);
        l.setVerticalAlignment(JLabel.BOTTOM);
        l.setIcon(i);
        return l;
    }*/




    public static void main(String[] args) {
        SwingUtilities.invokeLater(Stats::new);
    }
}