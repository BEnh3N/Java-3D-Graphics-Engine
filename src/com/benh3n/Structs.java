package com.benh3n;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Structs {
    public static class vec2D implements Cloneable{
        float u = 0.0f;
        float v = 0.0f;
        public vec2D() {

        }
        public vec2D(float u, float v) {
            this.u = u;
            this.v = v;
        }

        public vec2D clone() {
            try {
                vec2D clone = (vec2D) super.clone();
                clone.u = this.u;
                clone.v = this.v;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
        @Override
        public String toString() {
            return "(" + u + ", " + v + ")";
        }
    }

    public static class vec3D implements Cloneable {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;
        float w = 1.0f;
        public vec3D(){
        }
        public vec3D(float x, float y, float z){
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
        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }

    public static class triangle implements Cloneable {
        vec3D[] p = new vec3D[3];
        vec2D[] t = new vec2D[3];
        Color col;
        public triangle(){
        }
        public triangle(vec3D p1, vec3D p2, vec3D p3) {
            this.p = new vec3D[]{p1, p2, p3};
        }
        public triangle(float[] p, float[] t) {
            this.p = new vec3D[]{
                    new vec3D(p[0], p[1], p[2]),
                    new vec3D(p[3], p[4], p[5]),
                    new vec3D(p[6], p[7], p[8])
            };
            this.t = new vec2D[]{
                    new vec2D(t[0], t[1]),
                    new vec2D(t[2], t[3]),
                    new vec2D(t[4], t[5])
            };
        }

        public triangle clone() {
            try {
                triangle clone = (triangle) super.clone();
                clone.p = this.p.clone();
                clone.t = this.t.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
        @Override
        public String toString() {
            return p[0] + "\n" + p[1] + "\n" + p[2] + "\n" + col + "\n"
                    + t[0] + "\n" + t[1] + "\n" + t[2] + "\n";
        }
    }

    public static class mesh {
        ArrayList<triangle> tris;
        public mesh(){
        }

        public static mesh loadObjectFromFile(String sFilename){
            mesh tempMesh = new mesh();
            try {
                File file = new File(sFilename);
                Scanner in = new Scanner(file);
                ArrayList<vec3D> vecList = new ArrayList<>();
                ArrayList<triangle> triList = new ArrayList<>();
                while (in.hasNextLine()) {
                    String[] line = in.nextLine().split(" ");
                    switch (line[0]) {
                        case "v":
                            float x = Float.parseFloat(line[1]);
                            float y = Float.parseFloat(line[2]);
                            float z = Float.parseFloat(line[3]);
                            vecList.add(new vec3D(x, y, z));
                            break;

                        case "f":
                            int p0 = Integer.parseInt(line[1]) - 1;
                            int p1 = Integer.parseInt(line[2]) - 1;
                            int p2 = Integer.parseInt(line[3]) - 1;
                            triangle tri = new triangle(vecList.get(p0), vecList.get(p1), vecList.get(p2));
                            triList.add(tri);
                            break;
                    }
                }
                in.close();
                tempMesh.tris = triList;
            } catch (FileNotFoundException e) {
                System.out.println("File not Found!");
                e.printStackTrace();
            }
            return tempMesh;
        }
    }
}

