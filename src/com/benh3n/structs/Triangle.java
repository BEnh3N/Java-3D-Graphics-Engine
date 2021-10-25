package com.benh3n.structs;

import java.awt.*;
import java.util.Arrays;

public class Triangle implements Cloneable {
    public Vec3D[] p = new Vec3D[]{new Vec3D(), new Vec3D(), new Vec3D()};
    public Vec2D[] t = new Vec2D[]{new Vec2D(), new Vec2D(), new Vec2D()};
    public Color col;

    public Triangle() {

    }
    public Triangle(Vec3D p1, Vec3D p2, Vec3D p3) {
        this.p = new Vec3D[]{p1, p2, p3};
    }
    public Triangle(float[] p, float[] t) {
        this.p = new Vec3D[]{
                new Vec3D(p[0], p[1], p[2]),
                new Vec3D(p[3], p[4], p[5]),
                new Vec3D(p[6], p[7], p[8])
        };
        this.t = new Vec2D[]{
                new Vec2D(t[0], t[1]),
                new Vec2D(t[2], t[3]),
                new Vec2D(t[4], t[5])
        };
    }
    public String toString() {
        return p[0] + "\n" + p[1] + "\n" + p[2] + "\n" + col + "\n"
                + t[0] + "\n" + t[1] + "\n" + t[2] + "\n";
    }

    @Override
    public Triangle clone() {
        try {
            Triangle clone = (Triangle) super.clone();
            clone.p = this.p.clone();
            clone.t = this.t.clone();
            clone.col = this.col;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
