package com.benh3n.structs;

public class Vec3D implements Cloneable {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public float w = 1.0f;

    public Vec3D() {

    }
    public Vec3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ") " + this.w;
    }

    @Override
    public Vec3D clone() {
        try {
            Vec3D clone = (Vec3D) super.clone();
            clone.x = this.x;
            clone.y = this.y;
            clone.z = this.z;
            clone.w = this.w;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
