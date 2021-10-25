package com.benh3n.structs;

public class Vec2D implements Cloneable {
    public float u = 0.0f;
    public float v = 0.0f;
    private float w = 1.0f;

    public Vec2D() {

    }
    public Vec2D(float u, float v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public String toString() {
        return "(" + this.u + ", " + this.v + ") " + this.w;
    }

    @Override
    public Vec2D clone() {
        try {
            Vec2D clone = (Vec2D) super.clone();
            clone.u = this.u;
            clone.v = this.v;
            clone.w = this.w;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }
}
