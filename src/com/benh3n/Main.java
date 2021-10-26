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

import com.benh3n.Util.*;
import com.benh3n.structs.Mesh;
import com.benh3n.structs.Triangle;
import com.benh3n.structs.Vec3D;

public class Main {

    static Mesh meshCube = new Mesh();
    static mat4x4 matProj = new mat4x4();
    static Vec3D vCamera = new Vec3D();
    static Vec3D vLookDir;
    static float fYaw;
    static float fTheta;

    static BufferedImage sprTex1;

    static float elapsedTime;

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
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    vCamera.y += 8.0f * elapsedTime;
                if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    vCamera.y -= 8.0f * elapsedTime;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    vCamera.x -= 8.0f * elapsedTime;
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    vCamera.x += 8.0f * elapsedTime;

                Vec3D vForward = Util.VectorMul(vLookDir, 8.0f * elapsedTime);

                if (e.getKeyCode() == KeyEvent.VK_W)
                    vCamera = Util.VectorAdd(vCamera, vForward);
                if (e.getKeyCode() == KeyEvent.VK_S)
                    vCamera = Util.VectorSub(vCamera, vForward);

                if (e.getKeyCode() == KeyEvent.VK_A)
                    fYaw -= 2.0f * elapsedTime;
                if (e.getKeyCode() == KeyEvent.VK_D)
                    fYaw += 2.0f * elapsedTime;
            }
        });

        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
//        canvas.setSize(256, 240);
        float scale = 1;
        canvas.setSize((int)(256 * scale), (int)(240 * scale));
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

        meshCube.tris = new ArrayList<>(Arrays.asList(
                // SOUTH
                new Triangle(new float[]{0, 0, 0, 0, 1, 0, 1, 1, 0}, new float[]{0, 1, 0, 0, 1, 0}),
                new Triangle(new float[]{0, 0, 0, 1, 1, 0, 1, 0, 0}, new float[]{0, 1, 1, 0, 1, 1}),
                // EAST
                new Triangle(new float[]{1, 0, 0, 1, 1, 0, 1, 1, 1}, new float[]{0, 1, 0, 0, 1, 0}),
                new Triangle(new float[]{1, 0, 0, 1, 1, 1, 1, 0, 1}, new float[]{0, 1, 1, 0, 1, 1}),
                // NORTH
                new Triangle(new float[]{1, 0, 1, 1, 1, 1, 0, 1, 1}, new float[]{0, 1, 0, 0, 1, 0}),
                new Triangle(new float[]{1, 0, 1, 0, 1, 1, 0, 0, 1}, new float[]{0, 1, 1, 0, 1, 1}),
                // WEST
                new Triangle(new float[]{0, 0, 1, 0, 1, 1, 0, 1, 0}, new float[]{0, 1, 0, 0, 1, 0}),
                new Triangle(new float[]{0, 0, 1, 0, 1, 0, 0, 0, 0}, new float[]{0, 1, 1, 0, 1, 1}),
                // TOP
                new Triangle(new float[]{0, 1, 0, 0, 1, 1, 1, 1, 1}, new float[]{0, 1, 0, 0, 1, 0}),
                new Triangle(new float[]{0, 1, 0, 1, 1, 1, 1, 1, 0}, new float[]{0, 1, 1, 0, 1, 1}),
                // BOTTOM
                new Triangle(new float[]{1, 0, 1, 0, 0, 1, 0, 0, 0}, new float[]{0, 1, 0, 0, 1, 0}),
                new Triangle(new float[]{1, 0, 1, 0, 0, 0, 1, 0, 0}, new float[]{0, 1, 1, 0, 1, 1})
        ));

        try {
            sprTex1 = ImageIO.read(new File("rainbow.png"));
        } catch (IOException e) { System.out.println("brush"); }

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
                fTheta += 0.15 * elapsedTime; // Uncomment to spin me right round baby
                matRotZ = Util.MatrixMakeRotationZ(fTheta * 0.5f);
                matRotX = Util.MatrixMakeRotationX(fTheta);

                mat4x4 matTrans;
                matTrans = Util.MatrixMakeTranslation(0.0f, 0.0f, 5.0f);

                mat4x4 matWorld;
                // matWorld = MatrixMakeIdentity();
                matWorld = Util.MatrixMultiplyMatrix(matRotZ, matRotX);
                matWorld = Util.MatrixMultiplyMatrix(matWorld, matTrans);

                Vec3D vUp = new Vec3D(0, 1, 0);
                Vec3D vTarget = new Vec3D(0, 0, 1);
                mat4x4 matCameraRot = Util.MatrixMakeRotationY(fYaw);
                vLookDir = Util.MatrixMultiplyVector(matCameraRot, vTarget);
                vTarget = Util.VectorAdd(vCamera, vLookDir);

                mat4x4 matCamera = Util.MatrixPointAt(vCamera, vTarget, vUp);

                // Make matrix view from camera
                mat4x4 matView = Util.MatrixQuickInverse(matCamera);

                // Store Triangles for Rastering Later
                ArrayList<Triangle> trianglesToRaster = new ArrayList<>();

                // Draw Triangles
//                for (Triangle tri : meshCube.tris) {
                for (int t = 0; t < meshCube.tris.size(); t++) {
                    Triangle tri = meshCube.tris.get(t).clone();

                    if (tri.t[0].w != 1.0f || tri.t[1].w != 1.0f || tri.t[2].w != 1.0f)
                        System.out.println(tri);

                    Triangle triProjected = new Triangle(), triTransformed = new Triangle(), triViewed = new Triangle();

                    triTransformed.p[0] = Util.MatrixMultiplyVector(matWorld, tri.p[0]);
                    triTransformed.p[1] = Util.MatrixMultiplyVector(matWorld, tri.p[1]);
                    triTransformed.p[2] = Util.MatrixMultiplyVector(matWorld, tri.p[2]);
                    triTransformed.t = tri.clone().t;

                    // Calculate Triangle Normal
                    Vec3D normal, line1, line2;

                    // Get lines either side of triangle
                    line1 = Util.VectorSub(triTransformed.p[1], triTransformed.p[0]);
                    line2 = Util.VectorSub(triTransformed.p[2], triTransformed.p[0]);

                    // Take Cross Product of lines to get normal to triangle surface
                    normal = Util.VectorCrossProduct(line1, line2);

                    // You Normally need to Normalise a Normal!
                    normal = Util.VectorNormalise(normal);

                    // Get Ray from Triangle to Camera
                    Vec3D vCameraRay = Util.VectorSub(triTransformed.p[0], vCamera);

                    // If ray is aligned with normal, then triangle is visible
                    if(Util.VectorDotProduct(normal, vCameraRay) < 0.0f) {

                        // Illumination
                        Vec3D lightDirection = new Vec3D(0, 1, -1);
                        lightDirection = Util.VectorNormalise(lightDirection);

                        // How "aligned" are light direction and triangle surface normal?
                        float dp = Math.max(0.1f, Util.VectorDotProduct(lightDirection, normal));
                        // Choose Colors as Required
                        triTransformed.col = Util.getColor(dp);

                        // Convert World Space --> View Space
                        triViewed.p[0] = Util.MatrixMultiplyVector(matView, triTransformed.p[0]);
                        triViewed.p[1] = Util.MatrixMultiplyVector(matView, triTransformed.p[1]);
                        triViewed.p[2] = Util.MatrixMultiplyVector(matView, triTransformed.p[2]);
                        triViewed.col = triTransformed.col;
                        triViewed.t = triTransformed.t.clone();

                        // Clip Viewed Triangle against near plane, this could form two additional
                        // triangles
                        returnClip clipResult = Util.TriangleClipAgainstPlane(new Vec3D(0.0f, 0.0f, 0.1f), new Vec3D(0.0f, 0.0f, 1.0f), triViewed);
                        int nClippedTriangles = clipResult.numTris;
                        Triangle[] clipped = clipResult.tris;

                        for (int n = 0; n < nClippedTriangles; n++) {

                            // Project Triangles from 3D --> 2D
                            triProjected.p[0] = Util.MatrixMultiplyVector(matProj, clipped[n].p[0]);
                            triProjected.p[1] = Util.MatrixMultiplyVector(matProj, clipped[n].p[1]);
                            triProjected.p[2] = Util.MatrixMultiplyVector(matProj, clipped[n].p[2]);
                            triProjected.col = clipped[n].col;
                            triProjected.t = clipped[n].t.clone();

                            triProjected.t[0].u /= triProjected.p[0].w;
                            triProjected.t[1].u /= triProjected.p[1].w;
                            triProjected.t[2].u /= triProjected.p[2].w;

                            triProjected.t[0].v /= triProjected.p[0].w;
                            triProjected.t[1].v /= triProjected.p[1].w;
                            triProjected.t[2].v /= triProjected.p[2].w;

                            triProjected.t[0].w = 1.0f / triProjected.p[0].w;
                            triProjected.t[1].w = 1.0f / triProjected.p[1].w;
                            triProjected.t[2].w = 1.0f / triProjected.p[2].w;

                            // Scale into view, we moved the normalising into cartesian space
                            // out of the matrix.vector function from the previous video, so
                            // do this manually
                            triProjected.p[0] = Util.VectorDiv(triProjected.p[0], triProjected.p[0].w);
                            triProjected.p[1] = Util.VectorDiv(triProjected.p[1], triProjected.p[1].w);
                            triProjected.p[2] = Util.VectorDiv(triProjected.p[2], triProjected.p[2].w);

                            // X/Y are inverted so put them back
                            triProjected.p[0].x *= -1.0f;
                            triProjected.p[1].x *= -1.0f;
                            triProjected.p[2].x *= -1.0f;
                            triProjected.p[0].y *= -1.0f;
                            triProjected.p[1].y *= -1.0f;
                            triProjected.p[2].y *= -1.0f;

                            // Offset verts into visible normalised space
                            Vec3D vOffsetView = new Vec3D(1, 1, 0);
                            triProjected.p[0] = Util.VectorAdd(triProjected.p[0], vOffsetView);
                            triProjected.p[1] = Util.VectorAdd(triProjected.p[1], vOffsetView);
                            triProjected.p[2] = Util.VectorAdd(triProjected.p[2], vOffsetView);
                            triProjected.p[0].x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p[0].y *= 0.5f * (float) canvas.getHeight();
                            triProjected.p[1].x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p[1].y *= 0.5f * (float) canvas.getHeight();
                            triProjected.p[2].x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p[2].y *= 0.5f * (float) canvas.getHeight();

                            // Store Triangles for sorting
                            trianglesToRaster.add(triProjected.clone());
                        }
                    }
                }

                Comparator<Triangle> comp = (Triangle t1, Triangle t2) -> {
                    double z1 = (t1.p[0].z + t1.p[1].z + t1.p[2].z) / 3.0;
                    double z2 = (t2.p[0].z + t2.p[1].z + t2.p[2].z) / 3.0;
                    return Double.compare(z1, z2);
                };
                trianglesToRaster.sort(comp.reversed());

                for (Triangle triToRaster: trianglesToRaster) {

                    // Clip triangles against all four screen edges, this could yield
                    // a bunch of triangles
                    Triangle[] clipped;
                    ArrayList<Triangle> listTriangles = new ArrayList<>();

                    // Add initial triangle
                    listTriangles.add(triToRaster);
                    int nNewTriangles = 1;

                    for (int p = 0; p < 4; p++) {

                        int nTrisToAdd;
                        while (nNewTriangles > 0) {

                            // Take triangle from front of queue
                            Triangle test = listTriangles.get(0);
                            listTriangles.remove(0);
                            nNewTriangles--;

                            // Clip it against a plane. We only need to test each
                            // subsequent plane, against subsequent new triangles
                            // as all triangles after a plane clip are guaranteed
                            // to lie on the inside of the plane. I like how this
                            // comment is almost completely and utterly justified
                            returnClip clip = null;

                            switch (p) {
                                case 0: clip = Util.TriangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), test); break;
                                case 1: clip = Util.TriangleClipAgainstPlane(new Vec3D(0, canvas.getHeight() - 1, 0), new Vec3D(0, -1, 0), test); break;
                                case 2: clip = Util.TriangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), test); break;
                                case 3: clip = Util.TriangleClipAgainstPlane(new Vec3D(canvas.getWidth() - 1, 0, 0), new Vec3D(-1, 0, 0), test); break;
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
                    for (Triangle t : listTriangles) {
//                        g2d.setColor(t.col);
//                        g2d.fillPolygon(new int[]{(int) t.p[0].x, (int) t.p[1].x, (int) t.p[2].x}, new int[]{(int) t.p[0].y, (int) t.p[1].y, (int) t.p[2].y}, 3);

                        Util.TexturedTriangle(new ArrayList<>(Arrays.asList((int)t.p[0].x, (int)t.p[1].x, (int)t.p[2].x)),
                                            new ArrayList<>(Arrays.asList((int)t.p[0].y, (int)t.p[1].y, (int)t.p[2].y)),
                                            new ArrayList<>(Arrays.asList(t.t[0].u, t.t[1].u, t.t[2].u)),
                                            new ArrayList<>(Arrays.asList(t.t[0].v, t.t[1].v, t.t[2].v)),
                                            new ArrayList<>(Arrays.asList(t.t[0].w, t.t[1].w, t.t[2].w)),
                                            g2d, sprTex1);

                        g2d.setColor(Color.WHITE);
                        g2d.drawPolygon(new int[]{(int) t.p[0].x, (int) t.p[1].x, (int) t.p[2].x}, new int[]{(int) t.p[0].y, (int) t.p[1].y, (int) t.p[2].y}, 3);
                    }
                }

                graphics = buffer.getDrawGraphics();
                graphics.drawImage(bi, 0, 0, null);
                if (!buffer.contentsLost())
                    buffer.show();

                bi = gc.createCompatibleImage(canvas.getWidth(), canvas.getHeight());
                long end = System.nanoTime();
                elapsedTime = (end - now) / 100000000.0f;

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
