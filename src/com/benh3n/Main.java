package com.benh3n;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static class vec3D implements Cloneable {
        double x;
        double y;
        double z;
        public vec3D(){

        }
        public vec3D(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public vec3D clone() {
            try {
                vec3D clone = (vec3D) super.clone();
                clone.x = this.x;
                clone.y = this.y;
                clone.z = this.z;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class triangle implements Cloneable {
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

        public triangle clone() {
            try {
                triangle clone = (triangle) super.clone();
                clone.p1 = this.p1.clone();
                clone.p2 = this.p2.clone();
                clone.p3 = this.p3.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class mesh {
        ArrayList<triangle> tris;
        public mesh(){
            this.tris = new ArrayList<>();
        }
    }

    public static class mat4x4 {
        float[][] m = new float[4][4];
    }

    static mesh cubeMesh = new mesh();
    static mat4x4 matProj = new mat4x4();
    static float fTheta;

    public static vec3D MultiplyMatrixVector(vec3D i, mat4x4 m){
        vec3D o = new vec3D();
        o.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + m.m[3][0];
        o.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + m.m[3][1];
        o.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + m.m[3][2];
        double w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + m.m[3][3];

        if (w != 0.0f){
            o.x /= w; o.y /= w; o.z /= w;
        }

        return o;
    }

    static boolean running;

    public static void main(String[] args) {

        BufferedImage img = null;
        try { img = ImageIO.read(new File("icon.png")); } catch (IOException e) { System.out.println("you shouldn't be seeing this... (image load failed)"); }

        JFrame frame = new JFrame("3D Graphics Engine");
        frame.setIconImage(img);
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    running = false;
                }
            }
        });

        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        // canvas.setSize(256, 240);
        canvas.setSize(512, 480);
        // canvas.setSize(1000, 1000);
        canvas.setBackground(Color.BLACK);

        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        BufferStrategy buffer = canvas.getBufferStrategy();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage bi = gc.createCompatibleImage(canvas.getWidth(), canvas.getHeight());

        Graphics graphics = null;
        Graphics2D g2d = null;
        Color background = Color.BLACK;

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
        float fAspectRatio = (float)canvas.getHeight() / (float)canvas.getWidth();
        float fFovRad = 1.0f / (float)Math.tan(fFov * 0.5f / 180.0f * (float)Math.PI);

        matProj.m[0][0] = fAspectRatio * fFovRad;
        matProj.m[1][1] = fFovRad;
        matProj.m[2][2] = fFar / (fFar - fNear);
        float viewSpace = (-fFar * fNear) / (fFar - fNear);
        matProj.m[3][2] = viewSpace;
        matProj.m[2][3] = 1.0f;
        matProj.m[3][3] = 0.0f;

        running = true;
        while (running) {

            Instant now = Instant.now();

            fAspectRatio = (float)canvas.getHeight() / (float)canvas.getWidth();
            fFovRad = 1.0f / (float)Math.tan(fFov * 0.5f / 180.0f * (float)Math.PI);

            matProj.m[0][0] = fAspectRatio * fFovRad;
            matProj.m[1][1] = fFovRad;
            matProj.m[2][2] = fFar / (fFar - fNear);
            matProj.m[3][2] = viewSpace;

            try {
                // Clear Screen
                g2d = bi.createGraphics();
                g2d.setBackground(background);
                g2d.setColor(background);
                g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Set up Rotation Matrices
                mat4x4 matRotZ = new mat4x4(), matRotX = new mat4x4();

                // Rotation Z
                matRotZ.m[0][0] = (float) Math.cos(fTheta);
                matRotZ.m[0][1] = (float) Math.sin(fTheta);
                matRotZ.m[1][0] = (float) -Math.sin(fTheta);
                matRotZ.m[1][1] = (float) Math.cos(fTheta);
                matRotZ.m[2][2] = 1;
                matRotZ.m[3][3] = 1;

                // Rotation X
                matRotX.m[0][0] = 1;
                matRotX.m[1][1] = (float) Math.cos(fTheta * 0.5f);
                matRotX.m[1][2] = (float) Math.sin(fTheta * 0.5f);
                matRotX.m[2][1] = (float) -Math.sin(fTheta * 0.5f);
                matRotX.m[2][2] = (float) Math.cos(fTheta * 0.5f);
                matRotX.m[3][3] = 1;

                // Draw Triangles
                for (triangle tri : cubeMesh.tris) {
                    triangle triProjected = new triangle();
                    triangle triTranslated;
                    triangle triRotatedZ = new triangle();
                    triangle triRotatedZX = new triangle();

                    // Rotate in Z-Axis
                    triRotatedZ.p1 = MultiplyMatrixVector(tri.p1, matRotZ);
                    triRotatedZ.p2 = MultiplyMatrixVector(tri.p2, matRotZ);
                    triRotatedZ.p3 = MultiplyMatrixVector(tri.p3, matRotZ);

                    // Rotate in X-Axis
                    triRotatedZX.p1 = MultiplyMatrixVector(triRotatedZ.p1, matRotX);
                    triRotatedZX.p2 = MultiplyMatrixVector(triRotatedZ.p2, matRotX);
                    triRotatedZX.p3 = MultiplyMatrixVector(triRotatedZ.p3, matRotX);

                    // Offset into the Screen
                    triTranslated = triRotatedZX.clone();
                    triTranslated.p1.z = triRotatedZX.p1.z + 3.0f;
                    triTranslated.p2.z = triRotatedZX.p2.z + 3.0f;
                    triTranslated.p3.z = triRotatedZX.p3.z + 3.0f;

                    // Project Triangles from 3D --> 2D
                    triProjected.p1 = MultiplyMatrixVector(triTranslated.p1, matProj);
                    triProjected.p2 = MultiplyMatrixVector(triTranslated.p2, matProj);
                    triProjected.p3 = MultiplyMatrixVector(triTranslated.p3, matProj);

                    // Scale into view
                    triProjected.p1.x += 1.0f; triProjected.p1.y += 1.0f;
                    triProjected.p2.x += 1.0f; triProjected.p2.y += 1.0f;
                    triProjected.p3.x += 1.0f; triProjected.p3.y += 1.0f;
                    triProjected.p1.x *= 0.5f * (float) canvas.getWidth();
                    triProjected.p1.y *= 0.5f * (float) canvas.getHeight();
                    triProjected.p2.x *= 0.5f * (float) canvas.getWidth();
                    triProjected.p2.y *= 0.5f * (float) canvas.getHeight();
                    triProjected.p3.x *= 0.5f * (float) canvas.getWidth();
                    triProjected.p3.y *= 0.5f * (float) canvas.getHeight();

                    // Rasterize Triangles
                    g2d.setColor(Color.WHITE);
                    g2d.drawPolygon(new int[]{(int) triProjected.p1.x, (int) triProjected.p2.x, (int) triProjected.p3.x},
                                    new int[]{(int) triProjected.p1.y, (int) triProjected.p2.y, (int) triProjected.p3.y}, 3);
                }
                graphics = buffer.getDrawGraphics();
                graphics.drawImage(bi, 0, 0, null);
                if (!buffer.contentsLost()) {
                    buffer.show();
                }

                Thread.yield();
            } finally {
                if (graphics != null) {
                    graphics.dispose();
                }
                if (g2d != null) {
                    g2d.dispose();
                }
            }

            bi = gc.createCompatibleImage(canvas.getWidth(), canvas.getHeight());
            Instant end = Instant.now();
            Duration elapsedTime = Duration.between(now, end);
            fTheta += elapsedTime.toMillis() * 0.001;

        }

        System.exit(0);
    }
}
