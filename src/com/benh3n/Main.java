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
    public static class returnClip {
        int numTris;
        triangle[] tris;
        public returnClip(int numTris, triangle[] tris) {
            this.numTris = numTris;
            this.tris = tris;
        }
    }

    static mesh cubeMesh = new mesh();
    static mat4x4 matProj = new mat4x4();

    static vec3D vCamera = new vec3D();
    static vec3D vLookDir;

    static float fYaw;

    static float fTheta;
    static long elapsedTime;

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
    public static mat4x4 MatrixMakeTranslation(float x, float y, float z) {
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
    public static mat4x4 MatrixMakeProjection(float fFovDegrees, float fAspectRatio, float fNear, float fFar) {
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
    public static mat4x4 MatrixMultiplyMatrix(mat4x4 m1, mat4x4 m2) {
        mat4x4 matrix = new mat4x4();
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                matrix.m[r][c] = m1.m[r][0] * m2.m[0][c] + m1.m[r][1] * m2.m[1][c] + m1.m[r][2] * m2.m[2][c] + m1.m[r][3] * m2.m[3][c];
            }
        }
        return matrix;
    }
    public static mat4x4 MatrixPointAt(vec3D pos, vec3D target, vec3D up) {
        // Calculate new forward direction
        vec3D newForward = VectorSub(target, pos);
        newForward = VectorNormalise(newForward);

        // Calculate new up direction
        vec3D a = VectorMul(newForward, VectorDotProduct(up, newForward));
        vec3D newUp = VectorSub(up, a);
        newUp = VectorNormalise(newUp);

        // New Right direction is easy, its just cross product
        vec3D newRight = VectorCrossProduct(newUp, newForward);

        // Construct Dimensioning and Translation Matrix
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1.0f;
        return matrix;
    }
    public static mat4x4 MatrixQuickInverse(mat4x4 m) { // Only for Rotation/Translation Matrices
        mat4x4 matrix = new mat4x4();
        matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
        matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
        matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
        matrix.m[3][3] = 1.0f;
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
        return new vec3D(v1.x / k, v1.y / k, v1.z / k );
    }
    public static float VectorDotProduct(vec3D v1, vec3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
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
    public static vec3D VectorIntersectPlane(vec3D planeP, vec3D planeN, vec3D lineStart, vec3D lineEnd) {
        planeN = VectorNormalise(planeN);
        float planeD = -VectorDotProduct(planeN, planeP);
        float ad = VectorDotProduct(lineStart, planeN);
        float bd = VectorDotProduct(lineEnd, planeN);
        float t = (-planeD - ad) / (bd - ad);
        vec3D lineStartToEnd = VectorSub(lineEnd, lineStart);
        vec3D lineToIntersect = VectorMul(lineStartToEnd, t);
        return VectorAdd(lineStart, lineToIntersect);
    }

    interface Dist {
        float dist(vec3D p);
    }
    public static returnClip TriangleClipAgainstPlane(vec3D planeP, vec3D planeN, triangle inTri) {
        triangle outTri1 = new triangle();
        triangle outTri2 = new triangle();

        // Make sure plane normal is indeed normal
        planeN = VectorNormalise(planeN);

        // Return signed shortest distance from point to plane, place normal must be normalised
        vec3D finalPlaneN = planeN;
        Dist d = (vec3D p) -> {
            // vec3D n = VectorNormalise(p);
            return finalPlaneN.x * p.x + finalPlaneN.y * p.y + finalPlaneN.z * p.z - VectorDotProduct(finalPlaneN, planeP);
        };

        // Create two temporary storage arrays to classify points either side of plane
        // If distance sign is positive, point lies on "inside" of plane
        vec3D[] insidePoints  = new vec3D[3]; int nInsidePointCount  = 0;
        vec3D[] outsidePoints = new vec3D[3]; int nOutsidePointCount = 0;

        // Get signed distance of each point in triangle to plane
        float d0 = d.dist(inTri.p1);
        float d1 = d.dist(inTri.p2);
        float d2 = d.dist(inTri.p3);

        if (d0 >= 0) {
            insidePoints[nInsidePointCount++] = inTri.p1.clone();
        } else {
            outsidePoints[nOutsidePointCount++] = inTri.p1.clone();
        }

        if (d1 >= 0) {
            insidePoints[nInsidePointCount++] = inTri.p2.clone();
        } else {
            outsidePoints[nOutsidePointCount++] = inTri.p2.clone();
        }

        if (d2 >= 0) {
            insidePoints[nInsidePointCount++] = inTri.p3.clone();
        } else {
            outsidePoints[nOutsidePointCount++] = inTri.p3.clone();
        }

        // Now classify triangle points, and break the input triangle into
        // smaller output triangles if required. There are four possible
        // outcomes...

        if (nInsidePointCount == 0) {
            // All points lie on the outside of plane, so clip whole triangle
            // It ceases to exist

            return new returnClip(0, new triangle[]{null, null}); // No returned triangles are valid

        } else if (nInsidePointCount == 3) {
            // All points lie on the inside of plane, so do nothing
            // and allow the triangle to simply pass through
            outTri1 = inTri;

            return new returnClip(1, new triangle[]{outTri1, null}); // Just the one returned original triangle is valid

        } else if (nOutsidePointCount == 2) {
            // Triangle should be clipped. As two points lie outside
            // the plane, the triangle simply becomes a smaller triangle

            // Copy appearance info to new triangle
            outTri1.col =  inTri.col;

            // The inside point is valid, so keep that...
            outTri1.p1 = insidePoints[0];

            // but the two new points are at the locations where the
            // original sides of the triangle (lines) intersect with the plane
            outTri1.p2 = VectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);
            outTri1.p3 = VectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1]);

            return new returnClip(1, new triangle[]{outTri1, null}); // Return the newly formed single triangle

        } else {
            // Triangle should be clipped. As two points lie inside the plane,
            // the clipped triangle becomes a "quad". Fortunately, we can
            // represent a quad with two new triangles

            // Copy appearance info to new triangles
//            outTri1.col =  inTri.col;
//            outTri2.col =  inTri.col;
            outTri1.col = new Color(255, 0 ,0);
            outTri2.col = new Color(0, 0, 255);

            // TODO: Fix outTri1 from not rendering, but good job me on fixing the color problem

            // The first triangle consists of the two inside points and a new
            // point determined by the location where one side of the triangle
            // intersects with the plane
            outTri1.p1 = insidePoints[0];
            outTri1.p2 = insidePoints[1];
            outTri1.p3 = VectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);

            // The second triangle is composed of one of he inside points, a
            // new point determined by the intersection of the other side of the
            // triangle and the plane, and the newly created point above
            outTri2.p1 = insidePoints[1];
            outTri2.p2 = outTri1.p3;
            outTri2.p3 = VectorIntersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0]);

            return new returnClip(2, new triangle[]{outTri1, outTri2}); // Return two newly formed triangles which form a quad
        }
    }

    public static Color getColor(float lum){
        int col = (int)Math.abs(lum * 255);
        return new Color(col, col, col);
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

                vec3D vForward = VectorMul(vLookDir, 8.0f * elapsedTime / 30000000);

                if (e.getKeyCode() == KeyEvent.VK_W)
                    vCamera = VectorAdd(vCamera, vForward);
                if (e.getKeyCode() == KeyEvent.VK_S)
                    vCamera = VectorSub(vCamera, vForward);

                if (e.getKeyCode() == KeyEvent.VK_A)
                    fYaw -= 2.0f * elapsedTime / 30000000;
                if (e.getKeyCode() == KeyEvent.VK_D)
                    fYaw += 2.0f * elapsedTime / 30000000;
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

        // Load Object From File
        cubeMesh = mesh.loadObjectFromFile("axis.obj");

        running = true;
        while (running) {

            try {
                long now = System.nanoTime();

                // Recalculate Projection Matrix
                matProj = MatrixMakeProjection(90.0f, (float)canvas.getHeight() / (float)canvas.getWidth(), 0.1f, 1000.0f);

                // Clear Screen
                g2d = bi.createGraphics();
                g2d.setBackground(background);
                g2d.setColor(background);
                g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Set up Rotation Matrices
                mat4x4 matRotZ, matRotX;

                matRotZ = MatrixMakeRotationZ(fTheta * 0.5f);
                matRotX = MatrixMakeRotationX(fTheta);

                mat4x4 matTrans;
                matTrans = MatrixMakeTranslation(0.0f, 0.0f, 5.0f);

                mat4x4 matWorld;
                // matWorld = MatrixMakeIdentity();
                matWorld = MatrixMultiplyMatrix(matRotZ, matRotX);
                matWorld = MatrixMultiplyMatrix(matWorld, matTrans);

                vec3D vUp = new vec3D(0, 1, 0);
                vec3D vTarget = new vec3D(0, 0, 1);
                mat4x4 matCameraRot = MatrixMakeRotationY(fYaw);
                vLookDir = MatrixMultiplyVector(matCameraRot, vTarget);
                vTarget = VectorAdd(vCamera, vLookDir);

                mat4x4 matCamera = MatrixPointAt(vCamera, vTarget, vUp);

                // Make matrix view from camera
                mat4x4 matView = MatrixQuickInverse(matCamera);

                // Store Triangles for Rastering Later
                ArrayList<triangle> trianglesToRaster = new ArrayList<>();

                // Draw Triangles
                for (triangle tri : cubeMesh.tris) {
                    triangle triProjected = new triangle(), triTransformed = new triangle(), triViewed = new triangle();

                    triTransformed.p1 = MatrixMultiplyVector(matWorld, tri.p1);
                    triTransformed.p2 = MatrixMultiplyVector(matWorld, tri.p2);
                    triTransformed.p3 = MatrixMultiplyVector(matWorld, tri.p3);

                    // Calculate Triangle Normal
                    vec3D normal, line1, line2;

                    // Get lines either side of triangle
                    line1 = VectorSub(triTransformed.p2, triTransformed.p1);
                    line2 = VectorSub(triTransformed.p3, triTransformed.p1);

                    // Take Cross Product of lines to get normal to triangle surface
                    normal = VectorCrossProduct(line1, line2);

                    // You Normally need to Normalise a Normal!
                    normal = VectorNormalise(normal);

                    // Get Ray from Triangle to Camera
                    vec3D vCameraRay = VectorSub(triTransformed.p1, vCamera);

                    // If ray is aligned with normal, then triangle is visible
                    if(VectorDotProduct(normal, vCameraRay) < 0.0f) {

                        // Illumination
                        vec3D lightDirection = new vec3D(0, 1, -1);
                        lightDirection = VectorNormalise(lightDirection);

                        // How "aligned" are light direction and triangle surface normal?
                        float dp = Math.max(0.1f, VectorDotProduct(lightDirection, normal));
                        // Choose Colors as Required
                        triTransformed.col = getColor(dp);

                        // Convert World Space --> View Space
                        triViewed.p1 = MatrixMultiplyVector(matView, triTransformed.p1);
                        triViewed.p2 = MatrixMultiplyVector(matView, triTransformed.p2);
                        triViewed.p3 = MatrixMultiplyVector(matView, triTransformed.p3);
                        triViewed.col = triTransformed.col;

                        // Clip Viewed Triangle against near plane, this could form two additional
                        // triangles
                        returnClip clipResult = TriangleClipAgainstPlane(new vec3D(0.0f, 0.0f, 0.1f), new vec3D(0.0f, 0.0f, 1.0f), triViewed);
                        int nClippedTriangles = clipResult.numTris;
                        triangle[] clipped = clipResult.tris;

                        for (int n = 0; n < nClippedTriangles; n++) {

                            // Project Triangles from 3D --> 2D
                            triProjected.p1 = MatrixMultiplyVector(matProj, clipped[n].p1);
                            triProjected.p2 = MatrixMultiplyVector(matProj, clipped[n].p2);
                            triProjected.p3 = MatrixMultiplyVector(matProj, clipped[n].p3);
                            triProjected.col = clipped[n].col;

                            // Scale into view, we moved the normalising into cartesian space
                            // out of the matrix.vector function from the previous video, so
                            // do this manually
                            triProjected.p1 = VectorDiv(triProjected.p1, triProjected.p1.w);
                            triProjected.p2 = VectorDiv(triProjected.p2, triProjected.p2.w);
                            triProjected.p3 = VectorDiv(triProjected.p3, triProjected.p3.w);

                            // X/Y are inverted so put them back
                            triProjected.p1.x *= -1.0f;
                            triProjected.p2.x *= -1.0f;
                            triProjected.p3.x *= -1.0f;
                            triProjected.p1.y *= -1.0f;
                            triProjected.p2.y *= -1.0f;
                            triProjected.p3.y *= -1.0f;

                            // Offset verts into visible normalised space
                            vec3D vOffsetView = new vec3D(1, 1, 0);
                            triProjected.p1 = VectorAdd(triProjected.p1, vOffsetView);
                            triProjected.p2 = VectorAdd(triProjected.p2, vOffsetView);
                            triProjected.p3 = VectorAdd(triProjected.p3, vOffsetView);
                            triProjected.p1.x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p1.y *= 0.5f * (float) canvas.getHeight();
                            triProjected.p2.x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p2.y *= 0.5f * (float) canvas.getHeight();
                            triProjected.p3.x *= 0.5f * (float) canvas.getWidth();
                            triProjected.p3.y *= 0.5f * (float) canvas.getHeight();

                            // Store Triangles for sorting
                            trianglesToRaster.add(triProjected);
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
                    // Rasterize Triangles
                    g2d.setColor(triToRaster.col);
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
                elapsedTime = end - now;

                // fTheta += elapsedTime / 1000000000.0;

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
