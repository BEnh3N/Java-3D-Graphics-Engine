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
import java.util.Arrays;
import java.util.Comparator;

import com.benh3n.Meshes.*;
import com.benh3n.Util.*;

public class Main {

    static mesh meshCube = new mesh();
    static mat4x4 matProj = new mat4x4();

    static vec3D vCamera = new vec3D();
    static vec3D vLookDir;

    static float fYaw;

    static float fTheta;
    static long elapsedTime;

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
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    running = false;
                if (e.getKeyCode() == KeyEvent.VK_UP)
                    vCamera.y += 8.0f * elapsedTime / 30000000;
                if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    vCamera.y -= 8.0f * elapsedTime / 30000000;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    vCamera.x -= 8.0f * elapsedTime / 30000000;
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    vCamera.x += 8.0f * elapsedTime / 30000000;

                vec3D vForward = Util.VectorMul(vLookDir, 8.0f * elapsedTime / 30000000);

                if (e.getKeyCode() == KeyEvent.VK_W)
                    vCamera = Util.VectorAdd(vCamera, vForward);
                if (e.getKeyCode() == KeyEvent.VK_S)
                    vCamera = Util.VectorSub(vCamera, vForward);

                if (e.getKeyCode() == KeyEvent.VK_A)
                    fYaw -= 2.0f * elapsedTime / 30000000;
                if (e.getKeyCode() == KeyEvent.VK_D)
                    fYaw += 2.0f * elapsedTime / 30000000;
            }
        });

        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
//        canvas.setSize(256, 240);
        canvas.setSize(256 * 2, 240 * 2);
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

        // Load Object From File
        // meshCube = mesh.loadObjectFromFile("mountains.obj");

        meshCube.tris = new ArrayList<>(Arrays.asList(
                // SOUTH
                new triangle(new vec3D(0, 0, 0), new vec3D(0, 1, 0), new vec3D(1, 1, 0)),
                new triangle(new vec3D(0, 0, 0), new vec3D(1, 1, 0), new vec3D(1, 0, 0)),

                // EAST
                new triangle(new vec3D(1, 0, 0), new vec3D(1, 1, 0), new vec3D(1, 1, 1)),
                new triangle(new vec3D(1, 0, 0), new vec3D(1, 1, 1), new vec3D(1, 0, 1)),

                // NORTH
                new triangle(new vec3D(1, 0, 1), new vec3D(1, 1, 1), new vec3D(0, 1, 1)),
                new triangle(new vec3D(1, 0, 1), new vec3D(0, 1, 1), new vec3D(0, 0, 1)),

                // WEST
                new triangle(new vec3D(0, 0, 1), new vec3D(0, 1, 1), new vec3D(0, 1, 0)),
                new triangle(new vec3D(0, 0, 1), new vec3D(0, 1, 0), new vec3D(0, 0, 0)),

                // TOP
                new triangle(new vec3D(0, 1, 0), new vec3D(0, 1, 1), new vec3D(1, 1, 1)),
                new triangle(new vec3D(0, 1, 0), new vec3D(1, 1, 1), new vec3D(1, 1, 0)),

                // BOTTOM
                new triangle(new vec3D(1, 0, 1), new vec3D(0, 0, 1), new vec3D(0, 0, 0)),
                new triangle(new vec3D(1, 0, 1), new vec3D(0, 0, 0), new vec3D(1, 0, 0))
        ));


        running = true;
        while (running) {
            try {
                long now = System.nanoTime();

                // Recalculate Projection Matrix
                matProj = Util.MatrixMakeProjection(90.0f, (float)canvas.getHeight() / (float)canvas.getWidth(), 0.1f, 1000.0f);

                // Clear Screen
                g2d = bi.createGraphics();
                g2d.setBackground(background);
                g2d.setColor(background);
                g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Set up Rotation Matrices
                mat4x4 matRotZ, matRotX;

                matRotZ = Util.MatrixMakeRotationZ(fTheta * 0.5f);
                matRotX = Util.MatrixMakeRotationX(fTheta);

                mat4x4 matTrans;
                matTrans = Util.MatrixMakeTranslation(0.0f, 0.0f, 5.0f);

                mat4x4 matWorld;
                // matWorld = MatrixMakeIdentity();
                matWorld = Util.MatrixMultiplyMatrix(matRotZ, matRotX);
                matWorld = Util.MatrixMultiplyMatrix(matWorld, matTrans);

                vec3D vUp = new vec3D(0, 1, 0);
                vec3D vTarget = new vec3D(0, 0, 1);
                mat4x4 matCameraRot = Util.MatrixMakeRotationY(fYaw);
                vLookDir = Util.MatrixMultiplyVector(matCameraRot, vTarget);
                vTarget = Util.VectorAdd(vCamera, vLookDir);

                mat4x4 matCamera = Util.MatrixPointAt(vCamera, vTarget, vUp);

                // Make matrix view from camera
                mat4x4 matView = Util.MatrixQuickInverse(matCamera);

                // Store Triangles for Rastering Later
                ArrayList<triangle> trianglesToRaster = new ArrayList<>();

                // Draw Triangles
                for (triangle tri : meshCube.tris) {
                    triangle triProjected = new triangle(), triTransformed = new triangle(), triViewed = new triangle();

                    triTransformed.p1 = Util.MatrixMultiplyVector(matWorld, tri.p1);
                    triTransformed.p2 = Util.MatrixMultiplyVector(matWorld, tri.p2);
                    triTransformed.p3 = Util.MatrixMultiplyVector(matWorld, tri.p3);

                    // Calculate Triangle Normal
                    vec3D normal, line1, line2;

                    // Get lines either side of triangle
                    line1 = Util.VectorSub(triTransformed.p2, triTransformed.p1);
                    line2 = Util.VectorSub(triTransformed.p3, triTransformed.p1);

                    // Take Cross Product of lines to get normal to triangle surface
                    normal = Util.VectorCrossProduct(line1, line2);

                    // You Normally need to Normalise a Normal!
                    normal = Util.VectorNormalise(normal);

                    // Get Ray from Triangle to Camera
                    vec3D vCameraRay = Util.VectorSub(triTransformed.p1, vCamera);

                    // If ray is aligned with normal, then triangle is visible
                    if(Util.VectorDotProduct(normal, vCameraRay) < 0.0f) {

                        // Illumination
                        vec3D lightDirection = new vec3D(0, 1, -1);
                        lightDirection = Util.VectorNormalise(lightDirection);

                        // How "aligned" are light direction and triangle surface normal?
                        float dp = Math.max(0.1f, Util.VectorDotProduct(lightDirection, normal));
                        // Choose Colors as Required
                        triTransformed.col = Util.getColor(dp);

                        // Convert World Space --> View Space
                        triViewed.p1 = Util.MatrixMultiplyVector(matView, triTransformed.p1);
                        triViewed.p2 = Util.MatrixMultiplyVector(matView, triTransformed.p2);
                        triViewed.p3 = Util.MatrixMultiplyVector(matView, triTransformed.p3);
                        triViewed.col = triTransformed.col;

                        // Clip Viewed Triangle against near plane, this could form two additional
                        // triangles
                        returnClip clipResult = Util.TriangleClipAgainstPlane(new vec3D(0.0f, 0.0f, 0.1f), new vec3D(0.0f, 0.0f, 1.0f), triViewed);
                        int nClippedTriangles = clipResult.numTris;
                        triangle[] clipped = clipResult.tris;

                        for (int n = 0; n < nClippedTriangles; n++) {

                            // Project Triangles from 3D --> 2D
                            triProjected.p1 = Util.MatrixMultiplyVector(matProj, clipped[n].p1);
                            triProjected.p2 = Util.MatrixMultiplyVector(matProj, clipped[n].p2);
                            triProjected.p3 = Util.MatrixMultiplyVector(matProj, clipped[n].p3);
                            triProjected.col = clipped[n].col;

                            // Scale into view, we moved the normalising into cartesian space
                            // out of the matrix.vector function from the previous video, so
                            // do this manually
                            triProjected.p1 = Util.VectorDiv(triProjected.p1, triProjected.p1.w);
                            triProjected.p2 = Util.VectorDiv(triProjected.p2, triProjected.p2.w);
                            triProjected.p3 = Util.VectorDiv(triProjected.p3, triProjected.p3.w);

                            // X/Y are inverted so put them back
                            triProjected.p1.x *= -1.0f;
                            triProjected.p2.x *= -1.0f;
                            triProjected.p3.x *= -1.0f;
                            triProjected.p1.y *= -1.0f;
                            triProjected.p2.y *= -1.0f;
                            triProjected.p3.y *= -1.0f;

                            // Offset verts into visible normalised space
                            vec3D vOffsetView = new vec3D(1, 1, 0);
                            triProjected.p1 = Util.VectorAdd(triProjected.p1, vOffsetView);
                            triProjected.p2 = Util.VectorAdd(triProjected.p2, vOffsetView);
                            triProjected.p3 = Util.VectorAdd(triProjected.p3, vOffsetView);
                            triProjected.p1.x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p1.y *= 0.5f * (float) canvas.getHeight();
                            triProjected.p2.x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p2.y *= 0.5f * (float) canvas.getHeight();
                            triProjected.p3.x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p3.y *= 0.5f * (float) canvas.getHeight();

                            // Store Triangles for sorting
                            trianglesToRaster.add(triProjected.clone());
                        }
                    }
                }

                Comparator<triangle> comp = (triangle t1, triangle t2) -> {
                    double z1 = (t1.p1.z + t1.p2.z + t1.p3.z) / 3.0;
                    double z2 = (t2.p1.z + t2.p2.z + t2.p3.z) / 3.0;
                    return Double.compare(z1, z2);
                };
                trianglesToRaster.sort(comp.reversed());

                for (triangle triToRaster: trianglesToRaster) {

                    // Clip triangles against all four screen edges, this could yield
                    // a bunch of triangles
                    triangle[] clipped;
                    ArrayList<triangle> listTriangles = new ArrayList<>();

                    // Add initial triangle
                    listTriangles.add(triToRaster);
                    int nNewTriangles = 1;

                    for (int p = 0; p < 4; p++) {

                        int nTrisToAdd;
                        while (nNewTriangles > 0) {

                            // Take triangle from front of queue
                            triangle test = listTriangles.get(0);
                            listTriangles.remove(0);
                            nNewTriangles--;

                            // Clip it against a plane. We only need to test each
                            // subsequent plane, against subsequent new triangles
                            // as all triangles after a plane clip are guaranteed
                            // to lie on the inside of the plane. I like how this
                            // comment is almost completely and utterly justified
                            returnClip clip = null;
                            switch (p) {
                                case 0: clip = Util.TriangleClipAgainstPlane(new vec3D(0, 0, 0), new vec3D(0, 1, 0), test); break;
                                case 1: clip = Util.TriangleClipAgainstPlane(new vec3D(0, canvas.getHeight() - 1, 0), new vec3D(0, -1, 0), test); break;
                                case 2: clip = Util.TriangleClipAgainstPlane(new vec3D(0, 0, 0), new vec3D(1, 0, 0), test); break;
                                case 3: clip = Util.TriangleClipAgainstPlane(new vec3D(canvas.getWidth() - 1, 0, 0), new vec3D(-1, 0, 0), test); break;
                                default: break;
                            }
                            nTrisToAdd = clip.numTris;
                            clipped = clip.tris;

                            // Clipping may yield a variable number of triangles, so
                            // add these new ones to the back of the queue for subsequent
                            // clipping against next planes
                            listTriangles.addAll(Arrays.asList(clipped).subList(0, nTrisToAdd));
                        }
                        nNewTriangles = listTriangles.size();

                    }

                    // Draw the transformed, viewed, clipped, projected, sorted, clipped triangles
                    for (triangle t : listTriangles) {
                        g2d.setColor(t.col);
                        g2d.fillPolygon(new int[]{(int) t.p1.x, (int) t.p2.x, (int) t.p3.x}, new int[]{(int) t.p1.y, (int) t.p2.y, (int) t.p3.y}, 3);

//                        g2d.setColor(Color.BLACK);
//                        g2d.drawPolygon(new int[]{(int) t.p1.x, (int) t.p2.x, (int) t.p3.x}, new int[]{(int) t.p1.y, (int) t.p2.y, (int) t.p3.y}, 3);
                    }
                }

                graphics = buffer.getDrawGraphics();
                graphics.drawImage(bi, 0, 0, null);
                if (!buffer.contentsLost())
                    buffer.show();

                bi = gc.createCompatibleImage(canvas.getWidth(), canvas.getHeight());
                long end = System.nanoTime();
                elapsedTime = end - now;

                // fTheta += elapsedTime / 1000000000.0;

                Thread.yield();

            } finally {
                if (graphics != null)
                    graphics.dispose();
                if (g2d != null)
                    g2d.dispose();
            }
        }
        System.exit(0);
    }
}
