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
import java.util.ArrayList;
import java.util.Comparator;

import com.benh3n.Meshes.*;

public class Main {

    public static class mat4x4 {
        float[][] m = new float[4][4];
    }

    static mesh cubeMesh = new mesh();
    static mat4x4 matProj = new mat4x4();

    static vec3D vCamera = new vec3D();

    static float fTheta;

    public static vec3D MatrixMultiplyVector(mat4x4 m, vec3D i) {
        vec3D v = new vec3D();
        v.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + i.w * m.m[3][0];
        v.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + i.w * m.m[3][1];
        v.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + i.w * m.m[3][2];
        v.w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + i.w * m.m[3][3];

        return v;
    }
    public static mat4x4 MatrixMakeIdentity() {
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        return matrix;
    }
    public static mat4x4 MatrixMakeRotationX(float fAngleRad) {
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = (float) Math.cos(fAngleRad);
        matrix.m[1][2] = (float) Math.sin(fAngleRad);
        matrix.m[2][1] = (float) -Math.sin(fAngleRad);
        matrix.m[2][2] = (float) Math.cos(fAngleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }
    public static mat4x4 MatrixMakeRotationY(float fAngleRad) {
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = (float) Math.cos(fAngleRad);
        matrix.m[0][2] = (float) Math.sin(fAngleRad);
        matrix.m[2][0] = (float) -Math.sin(fAngleRad);
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = (float) Math.cos(fAngleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }
    public static mat4x4 MatrixMakeRotationZ(float fAngleRad) {
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = (float) Math.cos(fAngleRad);
        matrix.m[0][1] = (float) Math.sin(fAngleRad);
        matrix.m[1][0] = (float) -Math.sin(fAngleRad);
        matrix.m[1][1] = (float) Math.cos(fAngleRad);
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        return matrix;
    }
    public static mat4x4 Matrix_MakeTranslation(float x, float y, float z) {
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        matrix.m[3][0] = x;
        matrix.m[3][1] = y;
        matrix.m[3][2] = z;
        return matrix;
    }
    public static mat4x4 Matrix_MakeProjection(float fFovDegrees, float fAspectRatio, float fNear, float fFar) {
        float fFovRad = 1.0f / (float) Math.tan(fFovDegrees * 0.5f / 180.0f * 3.14159f);
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = fAspectRatio * fFovRad;
        matrix.m[1][1] = fFovRad;
        matrix.m[2][2] = fFar / (fFar - fNear);
        matrix.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        matrix.m[2][3] = 1.0f;
        matrix.m[3][3] = 0.0f;
        return matrix;
    }

    public static vec3D VectorAdd(vec3D v1, vec3D v2) {
        return new vec3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }
    public static vec3D VectorSub(vec3D v1, vec3D v2) {
        return new vec3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }
    public static vec3D VectorMul(vec3D v1, float k) {
        return new vec3D(v1.x * k, v1.y * k, v1.z * k);
    }
    public static vec3D VectorDiv(vec3D v1, float k) {
        return new vec3D(v1.x / k, v1.y / k, v1.z / k);
    }
    public static float VectorDotProduct(vec3D v1, vec3D v2) {
        return (float) (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
    }
    public static float VectorLength(vec3D v) {
        return (float) Math.sqrt(VectorDotProduct(v, v));
    }
    public static vec3D VectorNormalise(vec3D v) {
        float l = VectorLength(v);
        return new vec3D(v.x / l, v.y / l, v.z / l);
    }
    public static vec3D VectorCrossProduct(vec3D v1, vec3D v2) {
        vec3D v = new vec3D();
        v.x = v1.y * v2.z - v1.z * v2.y;
        v.y = v1.z * v2.x - v1.x * v2.z;
        v.z = v1.x * v2.y - v1.y * v2.x;
        return v;
    }


    public static short getColor(float lum){
        return (short) Math.abs(lum * 255);
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

        cubeMesh = mesh.loadObjectFromFile("VideoShip.obj");

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

            try {
                long now = System.nanoTime();

                // Recalculate Projection Matrix
                fAspectRatio = (float)canvas.getHeight() / (float)canvas.getWidth();
                fFovRad = 1.0f / (float)Math.tan(fFov * 0.5f / 180.0f * (float)Math.PI);

                matProj.m[0][0] = fAspectRatio * fFovRad;
                matProj.m[1][1] = fFovRad;
                matProj.m[2][2] = fFar / (fFar - fNear);
                matProj.m[3][2] = viewSpace;

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

                ArrayList<triangle> trianglesToRaster = new ArrayList<>();

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
                    triTranslated.p1.z = triRotatedZX.p1.z + 8.0f;
                    triTranslated.p2.z = triRotatedZX.p2.z + 8.0f;
                    triTranslated.p3.z = triRotatedZX.p3.z + 8.0f;

                    vec3D normal = new vec3D(), line1 = new vec3D(), line2 = new vec3D();
                    line1.x = triTranslated.p2.x - triTranslated.p1.x;
                    line1.y = triTranslated.p2.y - triTranslated.p1.y;
                    line1.z = triTranslated.p2.z - triTranslated.p1.z;

                    line2.x = triTranslated.p3.x - triTranslated.p1.x;
                    line2.y = triTranslated.p3.y - triTranslated.p1.y;
                    line2.z = triTranslated.p3.z - triTranslated.p1.z;

                    normal.x = line1.y * line2.z - line1.z * line2.y;
                    normal.y = line1.z * line2.x - line1.x * line2.z;
                    normal.z = line1.x * line2.y - line1.y * line2.x;

                    float l = (float)Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
                    normal.x /= l; normal.y /= l; normal.z /= l;

                    // if (normal.z < 0)
                    if(normal.x * (triTranslated.p1.x - vCamera.x) +
                       normal.y * (triTranslated.p1.y - vCamera.y) +
                       normal.z * (triTranslated.p1.z - vCamera.z) < 0.0f) {

                        // Illumination
                        vec3D lightDirection = new vec3D(0.0f, 0.0f, -1.0f);
                        l = (float)Math.sqrt(lightDirection.x * lightDirection.x + lightDirection.y * lightDirection.y + lightDirection.z * lightDirection.z);
                        lightDirection.x /= l; lightDirection.y /= l; lightDirection.z /= l;

                        // How similar is normal to light direction
                        float dp = (float) (normal.x * lightDirection.x + normal.y * lightDirection.y + normal.z * lightDirection.z);

                        triTranslated.col = getColor(dp);

                        // Project Triangles from 3D --> 2D
                        triProjected.p1 = MultiplyMatrixVector(triTranslated.p1, matProj);
                        triProjected.p2 = MultiplyMatrixVector(triTranslated.p2, matProj);
                        triProjected.p3 = MultiplyMatrixVector(triTranslated.p3, matProj);
                        triProjected.col = triTranslated.col;

                        // Scale into view
                        triProjected.p1.x += 1.0f;
                        triProjected.p1.y += 1.0f;
                        triProjected.p2.x += 1.0f;
                        triProjected.p2.y += 1.0f;
                        triProjected.p3.x += 1.0f;
                        triProjected.p3.y += 1.0f;
                        triProjected.p1.x *= 0.5f * (float) canvas.getWidth();
                        triProjected.p1.y *= 0.5f * (float) canvas.getHeight();
                        triProjected.p2.x *= 0.5f * (float) canvas.getWidth();
                        triProjected.p2.y *= 0.5f * (float) canvas.getHeight();
                        triProjected.p3.x *= 0.5f * (float) canvas.getWidth();
                        triProjected.p3.y *= 0.5f * (float) canvas.getHeight();

                        trianglesToRaster.add(triProjected);
                    }
                }

                Comparator<triangle> comp = (triangle t1, triangle t2) -> {
                    double z1 = (t1.p1.z + t1.p2.z + t1.p3.z) / 3.0;
                    double z2 = (t2.p1.z + t2.p2.z + t2.p3.z) / 3.0;
                    return Double.compare(z2, z1);
                };
                trianglesToRaster.sort(comp);

                for (triangle triToRaster: trianglesToRaster) {
                    // Rasterize Triangles
                    g2d.setColor(new Color(triToRaster.col, triToRaster.col, triToRaster.col));
                    g2d.fillPolygon(new int[]{(int) triToRaster.p1.x, (int) triToRaster.p2.x, (int) triToRaster.p3.x},
                            new int[]{(int) triToRaster.p1.y, (int) triToRaster.p2.y, (int) triToRaster.p3.y}, 3);

                    g2d.setColor(Color.BLACK);
                    g2d.drawPolygon(new int[]{(int) triToRaster.p1.x, (int) triToRaster.p2.x, (int) triToRaster.p3.x},
                            new int[]{(int) triToRaster.p1.y, (int) triToRaster.p2.y, (int) triToRaster.p3.y}, 3);
                }

                graphics = buffer.getDrawGraphics();
                graphics.drawImage(bi, 0, 0, null);
                if (!buffer.contentsLost()) {
                    buffer.show();
                }

                bi = gc.createCompatibleImage(canvas.getWidth(), canvas.getHeight());
                long end = System.nanoTime();
                long elapsedTime = end - now;

                fTheta += elapsedTime / 1000000000.0;

                Thread.yield();

            } finally {
                if (graphics != null) {
                    graphics.dispose();
                }
                if (g2d != null) {
                    g2d.dispose();
                }
            }
        }
        System.exit(0);
    }
}
