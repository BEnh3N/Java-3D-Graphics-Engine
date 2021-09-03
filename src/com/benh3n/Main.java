package com.benh3n;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JPanel{

    public static class vec3D {
        float x;
        float y;
        float z;
        public vec3D(){
            this.x = 0.0f;
            this.y = 0.0f;
            this.x = 0.0f;
        }
        public vec3D(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class triangle {
        vec3D p1;
        vec3D p2;
        vec3D p3;
        public triangle(){
            this.p1 = new vec3D();
            this.p2 = new vec3D();
            this.p3 = new vec3D();
        }
        public triangle(vec3D p1, vec3D p2, vec3D p3){
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
    }

    }

    public static class mesh {
        ArrayList<triangle> tris;
        public mesh(){
            this.tris = new ArrayList<>();
        }
        public mesh(ArrayList<triangle> tris){
            this.tris = tris;
        }
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

        mesh cubeMesh = new mesh();
        cubeMesh.tris = new ArrayList<>(Arrays.asList(
                // SOUTH
                new triangle(),
                new triangle(),
                // EAST
                new triangle(new vec3D(10.0f, 0.0f, 0.0f), new vec3D(0.0f, 5.0f, 0.0f), new vec3D(7.0f, 0.0f, 0.0f))));

        for (triangle tri : cubeMesh.tris){
            System.out.println(tri.p1.x);
        }

        Main panel = new Main();
        frame.add(panel);
        frame.setVisible(true);

    }
}
