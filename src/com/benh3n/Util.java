package com.benh3n;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import com.benh3n.Structs.*;

public final class Util {
    private Util() {
    }

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
    public static vec3D VectorIntersectPlane(vec3D planeP, vec3D planeN, vec3D lineStart, vec3D lineEnd, float[] t) {
        planeN = VectorNormalise(planeN);
        float planeD = -VectorDotProduct(planeN, planeP);
        float ad = VectorDotProduct(lineStart, planeN);
        float bd = VectorDotProduct(lineEnd, planeN);
        t[0] = (-planeD - ad) / (bd - ad);
        vec3D lineStartToEnd = VectorSub(lineEnd, lineStart);
        vec3D lineToIntersect = VectorMul(lineStartToEnd, t[0]);
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
        vec2D[] insideTex  = new vec2D[3];
        vec2D[] outsideTex = new vec2D[3];



        // Get signed distance of each point in triangle to plane
        float d0 = d.dist(inTri.p[0]);
        float d1 = d.dist(inTri.p[1]);
        float d2 = d.dist(inTri.p[2]);

        if (d0 >= 0) {
            insidePoints[nInsidePointCount] = inTri.p[0]; insideTex[nInsidePointCount++] = inTri.t[0];
        }
        else {
            outsidePoints[nOutsidePointCount] = inTri.p[0]; outsideTex[nOutsidePointCount++] = inTri.t[0];
        }
        if (d1 >= 0) {
            insidePoints[nInsidePointCount] = inTri.p[1]; insideTex[nInsidePointCount++] = inTri.t[1];
        }
        else {
            outsidePoints[nOutsidePointCount] = inTri.p[1]; outsideTex[nOutsidePointCount++] = inTri.t[1];
        }
        if (d2 >= 0) {
            insidePoints[nInsidePointCount] = inTri.p[2]; insideTex[nInsidePointCount++] = inTri.t[2];
        }
        else {
            outsidePoints[nOutsidePointCount] = inTri.p[2]; outsideTex[nOutsidePointCount++] = inTri.t[2];
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

            return new returnClip(1, new triangle[]{outTri1.clone(), null}); // Just the one returned original triangle is valid

        } else if (nOutsidePointCount == 2) {
            // Triangle should be clipped. As two points lie outside
            // the plane, the triangle simply becomes a smaller triangle

            // Copy appearance info to new triangle
            outTri1.col = inTri.col;
//            outTri1.col = Color.BLUE;

            // The inside point is valid, so keep that...
            outTri1.p[0] = insidePoints[0];
            outTri1.t[0] = insideTex[0];

            // but the two new points are at the locations where the
            // original sides of the triangle (lines) intersect with the plane
            float[] t = {0};
            outTri1.p[1] = VectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0], t);
            outTri1.t[1].u = t[0] * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u;
            outTri1.t[1].v = t[0] * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v;

            outTri1.p[2] = VectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1], t);
            outTri1.t[2].u = t[0] * (outsideTex[1].u - insideTex[0].u) + insideTex[0].u;
            outTri1.t[2].v = t[0] * (outsideTex[1].v - insideTex[0].v) + insideTex[0].v;

            return new returnClip(1, new triangle[]{outTri1.clone(), null}); // Return the newly formed single triangle

        } else {
            // Triangle should be clipped. As two points lie inside the plane,
            // the clipped triangle becomes a "quad". Fortunately, we can
            // represent a quad with two new triangles

            // Copy appearance info to new triangles
            outTri1.col =  inTri.col;
            outTri2.col =  inTri.col;
//            outTri1.col = Color.GREEN;
//            outTri2.col = Color.RED;

            // The first triangle consists of the two inside points and a new
            // point determined by the location where one side of the triangle
            // intersects with the plane
            outTri1.p[0] = insidePoints[0];
            outTri1.p[1] = insidePoints[1];
            outTri1.t[0] = insideTex[0];
            outTri1.t[1] = insideTex[1];

            float[] t = {0};
            outTri1.p[2] = VectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0], t);
            outTri1.t[2].u = t[0] * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u;
            outTri1.t[2].v = t[0] * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v;

            // The second triangle is composed of one of the inside points, a
            // new point determined by the intersection of the other side of the
            // triangle and the plane, and the newly created point above
            outTri2.p[0] = insidePoints[1];
            outTri2.t[0] = insideTex[1];
            outTri2.p[1] = outTri1.p[2];
            outTri2.t[1] = outTri1.t[2];
            outTri2.p[2] = VectorIntersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0], t);
            outTri2.t[2].u = t[0] * (outsideTex[0].u - insideTex[1].u) + insideTex[1].u;
            outTri2.t[2].v = t[0] * (outsideTex[0].v - insideTex[1].v) + insideTex[1].v;

            return new returnClip(2, new triangle[]{outTri1, outTri2}); // Return two newly formed triangles which form a quad
        }
    }

    public static Color getColor(float lum){
        int col = (int)Math.abs(lum * 255);
        return new Color(col, col, col);
    }

    private static Object[] swap(Object o1, Object o2) {
        return new Object[]{o2, o1};
    }
    public static void TexturedTriangle(float x1, float y1, float u1, float v1,
                                        float x2, float y2, float u2, float v2,
                                        float x3, float y3, float u3, float v3,
                                        Graphics2D g2d, BufferedImage tex) {

        if (y2 < y1) {
            var temp1 = y1; y1 = y2; y2 = temp1;
            var temp2 = x1; x1 = x2; x2 = temp2;
            var temp3 = u1; u1 = u2; u2 = temp3;
            var temp4 = v1; v1 = v2; v2 = temp4;
        }
        if (y3 < y1) {
            var temp1 = y1; y1 = y3; y3 = temp1;
            var temp2 = x1; x1 = x3; x3 = temp2;
            var temp3 = u1; u1 = u3; u3 = temp3;
            var temp4 = v1; v1 = v3; v3 = temp4;
        }
        if (y3 < y2) {
            var temp1 = y2; y2 = y3; y3 = temp1;
            var temp2 = x2; x2 = x3; x3 = temp2;
            var temp3 = u2; u2 = u3; u3 = temp3;
            var temp4 = v2; v2 = v3; v3 = temp4;
        }

        int dy1 = (int) (y2 - y1);
        int dx1 = (int) (x2 - x1);
        float dv1 = v2 - v1;
        float du1 = u2 - u1;

        int dy2 = (int) (y3 - y1);
        int dx2 = (int) (x3 - x1);
        float dv2 = v3 - v1;
        float du2 = u3 - u1;

        float texU, texV;

        float daxStep = 0, dbxStep = 0,
                du1Step = 0, dv1Step = 0,
                du2Step = 0, dv2Step = 0;

        if (dy1 != 0) daxStep = dx1 / (float)Math.abs(dy1);
        if (dy2 != 0) dbxStep = dx2 / (float)Math.abs(dy2);

        if (dy1 != 0) du1Step = du1 / (float)Math.abs(dy1);
        if (dy1 != 0) dv1Step = dv1 / (float)Math.abs(dy1);

        if (dy2 != 0) du2Step = du2 / (float)Math.abs(dy2);
        if (dy2 != 0) dv2Step = dv2 / (float)Math.abs(dy2);

        if (dy1 != 0) {
            for (int i = (int) y1; i <= y2; i++) {
                int ax = (int) (x1 + (i - y1) * daxStep);
                int bx = (int) (x1 + (i - y1) * dbxStep);

                float texSu = u1 + (i - y1) * du1Step;
                float texSv = v1 + (i - y1) * dv1Step;

                float texEu = u1 + (i - y1) * du2Step;
                float texEv = v1 + (i - y1) * dv2Step;

                if (ax > bx) {
                    var temp1 = ax; ax = bx; bx = temp1;
                    var temp2 = texSu; texSu = texEu; texEu = temp2;
                    var temp3 = texSv; texSv = texEv; texEv = temp3;
                }

                texU = texSu;
                texV = texSv;

                float tStep = 1.0f / ((float)(bx - ax));
                float t = 0.0f;

                for (int j = ax; j < bx; j++) {
                    texU = (1.0f - t) * texSu + t * texEu;
                    texV = (1.0f - t) * texSv + t * texEv;
                    System.out.println(texV);
                    System.out.println((int)(texV * 100));
                    int color = tex.getRGB((int)texU, (int)texV);
                    g2d.setColor(new Color((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff));
                    g2d.drawLine(j, i, j, i);

                    t += tStep;

                }
            }

            dy1 = (int) (y3 - y2);
            dx1 = (int) (x3 - x2);
            dv1 = v3 - v2;
            du1 = u3 - u2;
            if (dy1 != 0) daxStep = dx1 / (float)Math.abs(dy1);
            if (dy2 != 0) dbxStep = dx2 / (float)Math.abs(dy2);

            du1Step = 0; dv1Step = 0;
            if (dy1 != 0) du1Step = du1 / (float)Math.abs(dy1);
            if (dy1 != 0) dv1Step = dv1 / (float)Math.abs(dy1);

            for (int i = (int) y2; i <= y3; i++) {
                int ax = (int) (x2 + (i - y2) * daxStep);
                int bx = (int) (x1 + (i - y1) * dbxStep);

                float texSu = u2 + (i - y2) * du1Step;
                float texSv = v2 + (i - y2) * dv1Step;

                float texEu = u1 + (i - y1) * du2Step;
                float texEv = v1 + (i - y1) * dv2Step;

                if (ax > bx) {
                    var temp1 = ax; ax = bx; bx = temp1;
                    var temp2 = texSu; texSu = texEu; texEu = temp2;
                    var temp3 = texSv; texSv = texEv; texEv = temp3;
                }

                texU = texSu;
                texV = texSv;

                float tStep = 1.0f / ((float)(bx - ax));
                float t = 0.0f;

                for (int j = ax; j < bx; j++) {
                    texU = (1.0f - t) * texSu + t * texEu;
                    texV = (1.0f - t) * texSv + t * texEv;
                    int color = tex.getRGB(0, 0);
                    g2d.setColor(new Color((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff));
                    g2d.drawLine(j, i, j, i);

                    t += tStep;

                }
            }
        }
    }
}
