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
            this.p1 = new vec3D(0.0f, 0.0f, 0.0f);
            this.p2 = new vec3D(0.0f, 0.0f, 0.0f);
            this.p3 = new vec3D(0.0f, 0.0f, 0.0f);
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

    public static class mat4x4 {
        float[][] m = new float[4][4];
    }

    public vec3D MultiplyMatrixVector(vec3D i, mat4x4 m){
        vec3D o = new vec3D();
        o.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + m.m[3][0];
        o.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + m.m[3][1];
        o.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + m.m[3][2];
        float w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + m.m[3][3];

        if (w != 0.0f){
            o.x /= w; o.y /= w; o.z /= w;
        }

        return o;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);

        /* ArrayList<Integer> xPoints = new ArrayList<>();
        ArrayList<Integer> yPoints = new ArrayList<>();

        for (int[][] tri : tris){
            for (int[] pnt : tri){
                xPoints.add(pnt[0]);
                yPoints.add(pnt[1]);
            }
            g2d.drawPolygon(xPoints.stream().mapToInt(i -> i).toArray(), yPoints.stream().mapToInt(i -> i).toArray(), tri.length);
            xPoints.clear();
            yPoints.clear();
        } */
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
        mat4x4 matProj = new mat4x4();


        cubeMesh.tris = new ArrayList<>(Arrays.asList(
                // SOUTH
                new triangle(new vec3D(0.0f, 0.0f, 0.0f), new vec3D(0.0f, 1.0f, 0.0f), new vec3D(1.0f, 1.0f, 0.0f)),
                new triangle(new vec3D(0.0f, 0.0f, 0.0f), new vec3D(1.0f, 1.0f, 0.0f), new vec3D(1.0f, 0.0f, 0.0f)),
                // EAST
                new triangle(new vec3D(1.0f, 0.0f, 0.0f), new vec3D(1.0f, 1.0f, 0.0f), new vec3D(1.0f, 1.0f, 1.0f)),
                new triangle(new vec3D(1.0f, 0.0f, 0.0f), new vec3D(1.0f, 1.0f, 1.0f), new vec3D(1.0f, 0.0f, 1.0f)),
                // NORTH
                new triangle(new vec3D(1.0f, 0.0f, 1.0f), new vec3D(1.0f, 1.0f, 1.0f), new vec3D(0.0f, 1.0f, 1.0f)),
                new triangle(new vec3D(1.0f, 0.0f, 1.0f), new vec3D(0.0f, 1.0f, 1.0f), new vec3D(0.0f, 0.0f, 1.0f)),
                // WEST
                new triangle(new vec3D(0.0f, 0.0f, 1.0f), new vec3D(0.0f, 1.0f, 1.0f), new vec3D(0.0f, 1.0f, 0.0f)),
                new triangle(new vec3D(0.0f, 0.0f, 1.0f), new vec3D(0.0f, 1.0f, 0.0f), new vec3D(0.0f, 0.0f, 0.0f)),
                // TOP
                new triangle(new vec3D(0.0f, 1.0f, 0.0f), new vec3D(0.0f, 1.0f, 1.0f), new vec3D(1.0f, 1.0f, 1.0f)),
                new triangle(new vec3D(0.0f, 1.0f, 0.0f), new vec3D(1.0f, 1.0f, 1.0f), new vec3D(1.0f, 1.0f, 0.0f)),
                // BOTTOM
                new triangle(new vec3D(1.0f, 0.0f, 1.0f), new vec3D(0.0f, 0.0f, 1.0f), new vec3D(0.0f, 0.0f, 0.0f)),
                new triangle(new vec3D(1.0f, 0.0f, 1.0f), new vec3D(0.0f, 0.0f, 0.0f), new vec3D(1.0f, 0.0f, 0.0f))));

        // Projection Matrix
        float fNear = 0.1f;
        float fFar = 1000.0f;
        float fFov = 90.0f;
        float fAspectRatio = (float)frame.getHeight() / (float)frame.getWidth();
        float fFovRad = 1.0f / (float)Math.tan(fFov * 0.5f / 180.0f * (float)Math.PI);

        matProj.m[0][0] = fAspectRatio * fFovRad;
        matProj.m[1][1] = fFovRad;
        matProj.m[2][2] = fFar / (fFar - fNear);
        matProj.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        matProj.m[2][3] = 1.0f;
        matProj.m[3][3] = 0.0f;


        Main panel = new Main();
        frame.add(panel);
        frame.setVisible(true);

        while (true) {
            for (triangle tri : cubeMesh.tris) {
                triangle triProjected = new triangle();
                triProjected.p1 = panel.MultiplyMatrixVector(tri.p1, matProj);
                triProjected.p2 = panel.MultiplyMatrixVector(tri.p2, matProj);
                triProjected.p3 = panel.MultiplyMatrixVector(tri.p3, matProj);
            }
        }

    }
}
