package com.benh3n;

import javax.swing.*;
import java.awt.*;

public class Main extends JPanel{

    static final int[] x = {0, 100, 0};
    static final int[] y = {0, 0, 100};

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        // g2d.fillPolygon(x, y, 3);
        g2d.drawPolygon(x, y, 3);
    }

    public static void main(String[] args) {
        // JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("3D Graphics Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        frame.setSize(500, 500);

        Main panel = new Main();
        frame.add(panel);
        frame.setVisible(true);

        for (int i = 0; i < 10; i++) {
            x[0] += 1;
            if (x[0] > frame.getWidth() - 1) { x[0] = 0; }
            y[0] += 1;
            if (y[0] > frame.getHeight() - 1) { y[0] = 0; }

            System.out.print(x[0]);
            System.out.print(", ");
            System.out.println(y[0]);
            frame.repaint();
        }
    }
}
