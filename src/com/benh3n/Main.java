package com.benh3n;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends JPanel{

    public class vec3D {
        float x;
        float y;
        float z;
    }

    public class triangle {
        vec3D[] p = new vec3D[3];
    }

    public static class mesh {
        ArrayList<triangle> tris = new ArrayList<>();
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);

        ArrayList<Integer> xPoints = new ArrayList<>();
        ArrayList<Integer> yPoints = new ArrayList<>();

//        for (int[][] tri : tris){
//            for (int[] pnt : tri){
//                xPoints.add(pnt[0]);
//                yPoints.add(pnt[1]);
//            }
//            g2d.drawPolygon(xPoints.stream().mapToInt(i -> i).toArray(), yPoints.stream().mapToInt(i -> i).toArray(), tri.length);
//            xPoints.clear();
//            yPoints.clear();
//        }
    }

    public static void main(String[] args) {

        BufferedImage img = null;
        try { img = ImageIO.read(new File("icon.png")); } catch (IOException e) { System.out.println("you shouldn't be seeing this... (image load failed)"); }

        JFrame frame = new JFrame("3D Graphics Engine");
        frame.setIconImage(img);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        frame.setSize(500, 500);

        mesh meshCube = new mesh();
        meshCube.tris.add(new triangle());
        System.out.println(meshCube.tris);

        Main panel = new Main();
        frame.add(panel);
        frame.setVisible(true);

    }
}
